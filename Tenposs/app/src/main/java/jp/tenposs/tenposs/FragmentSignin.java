package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.utils.ThemifyIcon;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentSignin extends AbstractFragment implements View.OnClickListener {

    ImageView facebookIcon;
    Button facebookButton;

    ImageView twitterIcon;
    Button twitterButton;

    ImageView emailIcon;
    Button emailButton;

    Button skipButton;


    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Sign In";
        toolbarSettings.toolbarIcon = "ti-arrow-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
    }

    @Override
    protected void startup() {

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
        facebookButton = (Button) mRoot.findViewById(R.id.facebook_button);

        twitterIcon = (ImageView) mRoot.findViewById(R.id.twitter_image);
        twitterButton = (Button) mRoot.findViewById(R.id.twitter_button);

        emailIcon = (ImageView) mRoot.findViewById(R.id.email_image);
        emailButton = (Button) mRoot.findViewById(R.id.email_button);

        skipButton = (Button) mRoot.findViewById(R.id.skip_button);


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


        facebookButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        if (v == facebookButton) {
            //TODO: Login with Facebook

        } else if (v == twitterButton) {
            //TODO: Login with Twitter

        } else if (v == emailButton) {
            //TODO: Login with email and password

        } else {
            close();
        }
    }
}
