package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;

import jp.tenposs.communicator.ReserveInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ReserveInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.TopInfo;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentReserve extends AbstractFragment {
    WebView webView;
    ReserveInfo.Reserve screenData;
    TopInfo.Contact storeInfo;

    public static String STORE_INFO = "STORE_INFO";

    public static FragmentReserve newInstance(@NonNull TopInfo.Contact storeInfo) {
        FragmentReserve gm = new FragmentReserve();
        Bundle b = new Bundle();
        b.putSerializable(STORE_INFO, storeInfo);
        gm.setArguments(b);
        return gm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.storeInfo = (TopInfo.Contact) getArguments().getSerializable(STORE_INFO);
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "Reserve";
        toolbarSettings.toolbarIcon = "ti-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void customResume() {
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
        String strUrl = screenData.reserve_url.toLowerCase(Locale.US);
        String strTemp = screenData.reserve_url.toLowerCase(Locale.US);

        if (strTemp.contains("http://") == false && strTemp.contains("https://") == false)
            strUrl = "http://" + strUrl;

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
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            //this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    void loadReserveInfo() {
        ReserveInfo.Request requestParams = new ReserveInfo.Request();
        if (storeInfo != null) {
            requestParams.store_id = storeInfo.id;
        } else {
            requestParams.store_id = 1;
        }

        Bundle params = new Bundle();
        params.putSerializable(Key.RequestObject, requestParams);
        ReserveInfoCommunicator communicator = new ReserveInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                int result = responseParams.getInt(Key.ResponseResult);
                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                    if (resultApi == CommonResponse.ResultSuccess) {
                        //TODO:
                        screenData = (ReserveInfo.Reserve) responseParams.getSerializable(Key.ResponseObject);
                        previewScreenData();
                    } else {
                        String strMessage = responseParams.getString(Key.ResponseMessage);
                        errorWithMessage(responseParams, strMessage);
                    }
                } else {
                    String strMessage = responseParams.getString(Key.ResponseMessage);
                    errorWithMessage(responseParams, strMessage);
                }
            }
        });
        communicator.execute(params);
    }
}
