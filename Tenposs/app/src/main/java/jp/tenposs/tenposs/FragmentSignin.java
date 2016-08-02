package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.utils.ThemifyIcon;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentSignin extends AbstractFragment {

    ImageView facebookIcon;
    ImageView twitterIcon;
    ImageView emailIcon;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Sign In";
        toolbarSettings.toolbarIcon = "ti-angle-left";
        toolbarSettings.toolbarType = 1;//1 Home, 0 child

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_signin, null);
        facebookIcon = (ImageView) mRoot.findViewById(R.id.facebook_image);
        twitterIcon = (ImageView) mRoot.findViewById(R.id.twitter_image);
        emailIcon = (ImageView) mRoot.findViewById(R.id.email_image);


        facebookIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-facebook",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));

        twitterIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-twitter-alt",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));

        emailIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-email",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));
        //ti-email
        //ti-facebook
        //ti-twitter-alt
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }
}
