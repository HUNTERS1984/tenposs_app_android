package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

import jp.tenposs.communicator.ReserveInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.MenuInfo;
import jp.tenposs.datamodel.ReserveInfo;
import jp.tenposs.datamodel.ScreenDataStatus;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentReserve extends AbstractFragment {
    WebView webView;
ReserveInfo.Response screenData;
    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Reserve";
        toolbarSettings.toolbarIcon = "ti-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
    }

    @Override
    protected void startup() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
            loadReserveInfo();

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        String strUrl = screenData.data.reserve.reserve_url.toLowerCase(Locale.US);
        String strTemp= screenData.data.reserve.reserve_url.toLowerCase(Locale.US);

        if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
            strUrl  = "http://" + strUrl ;

        this.webView.loadUrl(strUrl);
        //this.webView.loadUrl("http://tabelog.com");
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_reserve, null);
        this.webView = (WebView) mRoot.findViewById(R.id.web_view);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return false;
            }
        });
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }

    void loadReserveInfo() {
        ReserveInfo.Request requestParams = new ReserveInfo.Request();
        requestParams.store_id = 1;

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        ReserveInfoCommunicator communicator = new ReserveInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                screenData = (ReserveInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                previewScreenData();
            }
        });
        communicator.execute(params);
    }
}
