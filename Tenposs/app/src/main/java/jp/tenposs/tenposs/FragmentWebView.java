package jp.tenposs.tenposs;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 9/28/16.
 */

public abstract class FragmentWebView extends AbstractFragment implements View.OnClickListener {
    protected static final String SCREEN_URL = "SCREEN_URL";
    ProgressBar mLoadingProgress;
    WebView mWebView;

    LinearLayout mNavControlLayout;
    ImageButton mBackButton;//ti-arrow-left
    ImageButton mForwardButton;//ti-arrow-right
    ImageButton mRefreshButton;//ti-reload
    ImageButton mStopButton;//ti-close

    String mUrl = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        this.mWebView.loadUrl(mUrl);
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_web_view, null);
        this.mLoadingProgress = (ProgressBar) root.findViewById(R.id.loading_progress);
        this.mWebView = (WebView) root.findViewById(R.id.web_view);
        this.mNavControlLayout = (LinearLayout) root.findViewById(R.id.nav_control_layout);
        this.mBackButton = (ImageButton) root.findViewById(R.id.back_button);
        this.mForwardButton = (ImageButton) root.findViewById(R.id.forward_button);
        this.mRefreshButton = (ImageButton) root.findViewById(R.id.refresh_button);
        this.mStopButton = (ImageButton) root.findViewById(R.id.stop_button);

        this.mBackButton.setOnClickListener(this);
        this.mForwardButton.setOnClickListener(this);
        this.mRefreshButton.setOnClickListener(this);
        this.mStopButton.setOnClickListener(this);

        if (AppData.sharedInstance().getTemplate() != AppData.TemplateId.RestaurantTemplate) {
            this.mNavControlLayout.setBackgroundColor(this.mToolbarSettings.getToolbarBackgroundColor());
        }
        enableBack(false);
        enableForward(false);
        enableRefresh(false);
        enableStop(false);

        this.mWebView.measure(100, 100);
        this.mWebView.getSettings().setUseWideViewPort(true);
        this.mWebView.getSettings().setLoadWithOverviewMode(true);
        this.mWebView.getSettings().setDomStorageEnabled(true);
        this.mWebView.getSettings().setDatabaseEnabled(true);
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {

                if (url != null && url.startsWith("intent://")) {
                    try {
//                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("intent://#Intent;scheme=videoflyvn://play/video?vid=395668228757622224&amp;pos=0;package=vn.com.videofly.videofly;end")));


                        return true;
                    } catch (Exception ignored) {
                        return false;
                    }

                } else {
                    Log.i(Tag, "shouldOverrideUrlLoading " + url);
                    return false;
                }
            }
        });
        this.mWebView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                        Log.i(Tag, consoleMessage.message());
                        return super.onConsoleMessage(consoleMessage);
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress < 100) {
                            mLoadingProgress.setVisibility(View.VISIBLE);
                            Log.i(Tag, "Loading " + newProgress + "%");
                            mLoadingProgress.setProgress(newProgress);
                            enableStop(true);
                        } else {
                            mLoadingProgress.setVisibility(View.INVISIBLE);
                            enableStop(false);
                            enableRefresh(true);
                            enableBack(mWebView.canGoBack());
                            enableForward(mWebView.canGoForward());
                        }
                    }
                });
        return root;
    }

    protected void enableBack(boolean enable) {
        if (this.isAdded() == false) {
            return;
        }
        this.mBackButton.setEnabled(enable);
        int color = Color.argb(255, 128, 128, 128);
        if (enable == true) {
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                color = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            } else {
                color = this.mToolbarSettings.getToolbarIconColor();
            }
        }
        this.mBackButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                "flaticon-back",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                color,
                FontIcon.FLATICON
        ));
    }

    protected void enableForward(boolean enable) {
        if (this.isAdded() == false) {
            return;
        }
        this.mForwardButton.setEnabled(enable);
        int color = Color.argb(255, 128, 128, 128);

        if (enable == true) {
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                color = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            } else {
                color = this.mToolbarSettings.getToolbarIconColor();
            }
        }
        this.mForwardButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                "flaticon-next",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                color,
                FontIcon.FLATICON
        ));
    }

    protected void enableRefresh(boolean enable) {
        if (this.isAdded() == false) {
            return;
        }
        this.mRefreshButton.setEnabled(enable);
        int color = Color.argb(255, 128, 128, 128);
        if (enable == true) {
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                color = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            } else {
                color = this.mToolbarSettings.getToolbarIconColor();
            }
        }
        this.mRefreshButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                "flaticon-reload",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                color,
                FontIcon.FLATICON
        ));
    }

    protected void enableStop(boolean enable) {
        if (this.isAdded() == false) {
            return;
        }
        this.mStopButton.setEnabled(enable);
        int color = Color.argb(255, 128, 128, 128);
        if (enable == true) {
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                color = Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
            } else {
                color = this.mToolbarSettings.getToolbarIconColor();
            }
        }
        this.mStopButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                "flaticon-close",
                Utils.CatIconSize,
                Color.argb(0, 0, 0, 0),
                color,
                FontIcon.FLATICON
        ));
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public void onClick(View v) {

        if (v == this.mBackButton) {
            if (this.mWebView.canGoBack()) {
                this.mWebView.goBack();
                enableStop(true);
                enableRefresh(false);
            }

        } else if (v == this.mForwardButton) {
            if (this.mWebView.canGoForward()) {
                this.mWebView.goForward();
                enableStop(true);
                enableRefresh(false);
            }

        } else if (v == this.mRefreshButton) {
            this.mWebView.reload();
            enableStop(true);

        } else if (v == this.mStopButton) {
            this.mWebView.stopLoading();
            enableStop(false);
            enableRefresh(true);
            enableBack(mWebView.canGoBack());
            enableForward(mWebView.canGoForward());
        }
    }


    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_URL) == true) {
            this.mUrl = savedInstanceState.getString(SCREEN_URL);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putString(SCREEN_URL, this.mUrl);
    }
}
