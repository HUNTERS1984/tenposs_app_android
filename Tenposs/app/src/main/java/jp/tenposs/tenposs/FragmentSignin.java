package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.tenposs.communicator.SocialSignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.utils.ThemifyIcon;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentSignIn extends AbstractFragment implements View.OnClickListener {

    ImageView facebookIcon;
    Button facebookButton;
    LoginButton facebookLogin;

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
        toolbarSettings.toolbarTitle = getString(R.string.signin);
        toolbarSettings.toolbarIcon = "ti-arrow-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
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
        facebookLogin = (LoginButton) mRoot.findViewById(R.id.facebook_login);

        twitterIcon = (ImageView) mRoot.findViewById(R.id.twitter_image);
        twitterButton = (Button) mRoot.findViewById(R.id.twitter_button);

        emailIcon = (ImageView) mRoot.findViewById(R.id.email_image);
        emailButton = (Button) mRoot.findViewById(R.id.email_button);

        skipButton = (Button) mRoot.findViewById(R.id.skip_button);


        List<String> permission = new ArrayList<>();
        permission.add("public_profile");
        permission.add("email");
        this.facebookLogin.setReadPermissions(permission);

        // If using in a fragment
        facebookLogin.setFragment(this);

        //facebookLogin.setReadPermissions(Arrays.asList("email", "public_profile", "AccessToken"));
        // Callback registration
        facebookLogin.registerCallback(activityListener.getCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                System.out.println("onSuccess");
            }

            @Override
            public void onCancel() {
                // App code
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.print("FragmentSignIn " + exception.getMessage());
            }
        });


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
    protected void customResume() {
        previewScreenData();
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

    @Override
    public void onClick(View v) {
        if (v == facebookButton) {
            //TODO: Login with Facebook
            Profile profile = Profile.getCurrentProfile().getCurrentProfile();
            if (profile != null) {
                // user has logged in
                Bundle params = new Bundle();

                SocialSignInCommunicator communicator = new SocialSignInCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {

                    }
                });
                communicator.execute(params);
            } else {

                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "AccessToken"));

//                facebookButton.post(new Runnable() {
//                    @Override
//                    public void run() {
                        // user has not logged in
                        facebookLogin.performClick();
//                    }
//                });
            }

        } else if (v == twitterButton) {
            //TODO: Login with Twitter
            close();
        } else if (v == emailButton) {
            //TODO: Login with email and password
            close();
        } else {
            close();
        }
    }
}
