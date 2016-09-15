package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

import jp.tenposs.communicator.SocialSignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SocialSigninInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.FlatIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentSignIn extends AbstractFragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {
    private static final String SCREEN_TYPE = "SCREEN_TYPE";
    LinearLayout signInGroupLayout;

    ImageView facebookIcon;
    Button facebookButton;
    LoginButton facebookLogin;
    TwitterLoginButton twitterLogin;

    ImageView twitterIcon;
    Button twitterButton;

    ImageView emailIcon;
    Button emailButton;


    ViewGroup skipButtonLayout;
    Button skipButton;
    TextView gotoSignUpLabel;

//    LinearLayout signInEmailLayout;
//    EditText emailEdit;
//    EditText passwordEdit;
//    Button backButton;
    //Button signInButton;

    ProfileTracker mProfileTracker;
    CallbackManager callbackManager;

    public static FragmentSignIn newInstance(boolean showToolbar) {
        FragmentSignIn instance = new FragmentSignIn();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SCREEN_TYPE, showToolbar);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == 0xFACE) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        //} else {
        twitterLogin.onActivityResult(requestCode, resultCode, data);
        //}
    }

    @Override
    protected void customClose() {
        this.activityListener.getFM().removeOnBackStackChangedListener(this);
    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.sign_in);
        toolbarSettings.toolbarLeftIcon = "flaticon-back";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

        this.activityListener.getFM().addOnBackStackChangedListener(this);
        if (this.screenToolBarHidden == true) {
            this.skipButtonLayout.setVisibility(View.GONE);
        } else {
            this.skipButtonLayout.setVisibility(View.VISIBLE);
        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signin, null);

        this.signInGroupLayout = (LinearLayout) root.findViewById(R.id.sign_in_group_layout);
        this.facebookIcon = (ImageView) root.findViewById(R.id.facebook_image);
        this.facebookButton = (Button) root.findViewById(R.id.facebook_button);
        this.facebookLogin = (LoginButton) root.findViewById(R.id.facebook_login);
        this.twitterLogin = (TwitterLoginButton) root.findViewById(R.id.twitter_login);

        this.twitterIcon = (ImageView) root.findViewById(R.id.twitter_image);
        this.twitterButton = (Button) root.findViewById(R.id.twitter_button);

        this.emailIcon = (ImageView) root.findViewById(R.id.email_image);
        this.emailButton = (Button) root.findViewById(R.id.email_button);

        this.gotoSignUpLabel = (TextView) root.findViewById(R.id.go_to_sign_up_label);
        this.skipButton = (Button) root.findViewById(R.id.skip_button);
        this.skipButtonLayout = (ViewGroup) root.findViewById(R.id.skip_button_layout);

//        this.signInEmailLayout = (LinearLayout) root.findViewById(R.id.sign_in_email_layout);
//        this.emailEdit = (EditText) root.findViewById(R.id.email_edit);
//        this.passwordEdit = (EditText) root.findViewById(R.id.password_edit);
        //this.signInButton = (Button) root.findViewById(R.id.sign_in_button);
//        this.backButton = (Button) root.findViewById(R.id.back_to_group_button);

        Utils.setTextViewHTML(this.gotoSignUpLabel, getString(R.string.not_sign_up),
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        activityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null);
                    }
                });
        List<String> permission = new ArrayList<>();
        permission.add("public_profile");
        permission.add("email");

        this.callbackManager = activityListener.getCallbackManager();

        this.facebookLogin.setReadPermissions(permission);
        this.facebookLogin.setFragment(this);
        this.facebookLogin.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                //Save AccessToken
                final String token = loginResult.getAccessToken().getToken();
                setKeyString(Key.FacebookTokenKey, token);

                Profile profile = Profile.getCurrentProfile();
                if (profile == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            performSocialSignIn(SocialSigninInfo.FACEBOOK,
                                    profile2.getId(),
                                    profile2.getName(),
                                    token, null);
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                } else {
                    //profile = Profile.getCurrentProfile();
                    performSocialSignIn(SocialSigninInfo.FACEBOOK,
                            profile.getId(),
                            profile.getName(),
                            token, null);
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                System.out.print("FragmentSignIn " + exception.getMessage());
            }
        });

        this.twitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterAuthToken token = result.data.getAuthToken();
                String twitterToken = token.token;

                //Save Token
                setKeyString(Key.TwitterTokenKey, twitterToken);

                performSocialSignIn(SocialSigninInfo.TWITTER,
                        Long.toString(result.data.getId()),
                        result.data.getUserName(),//name
                        twitterToken,//token
                        SocialSigninInfo.TWITTER_CONSUMER_SECRET
                );
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        facebookIcon.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-facebook",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));

        twitterIcon.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-twitter",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));

        emailIcon.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                "flaticon-email",
                40,
                Color.argb(0, 0, 0, 0),
                Color.argb(255, 255, 255, 255)
        ));


        facebookButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
//        backButton.setOnClickListener(this);
        //signInButton.setOnClickListener(this);
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_TYPE)) {
            this.screenToolBarHidden = savedInstanceState.getBoolean(SCREEN_TYPE);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public void onClick(View v) {
        if (v == this.facebookButton) {
            //TODO: Login with Facebook
            Profile profile = Profile.getCurrentProfile();
            String token = getKeyString(Key.FacebookTokenKey);
            if (profile != null && token.length() > 0) {
                // user has logged in

                performSocialSignIn(SocialSigninInfo.FACEBOOK,
                        profile.getId(),
                        profile.getName(),
                        token,
                        null);

            } else {
                this.facebookLogin.performClick();
            }

        } else if (v == this.twitterButton) {
            //TODO: Login with Twitter
            this.twitterLogin.performClick();

        } else if (v == this.emailButton) {
            activityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null);
            //signInGroupLayout.setVisibility(View.GONE);
            //signInEmailLayout.setVisibility(View.VISIBLE);

            //} else if (v == backButton) {
            //signInGroupLayout.setVisibility(View.VISIBLE);
            //signInEmailLayout.setVisibility(View.GONE);

            //} else if (v == signInButton) {
            //  if (checkInput() == true) {
            //    performEmailSignIn();
            //}
        } else if (v == this.skipButton) {
            close();
            //TODO: ??
            activityListener.showScreen(HOME_SCREEN, null);
        }
    }

    void performSocialSignIn(String social_type, String social_id, String name, String social_token, String social_secret) {
        Bundle params = new Bundle();

        SocialSigninInfo.Request request = new SocialSigninInfo.Request();
        request.social_type = social_type;
        request.name = name;
        request.social_id = social_id;
        request.social_token = social_token;
        request.social_secret = social_secret;

        params.putSerializable(Key.RequestObject, request);

        SocialSignInCommunicator communicator = new SocialSignInCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            hideProgress();
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                //TODO:
                                SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                                String token = response.data.token;
                                SignInInfo.Profile profile = response.data.profile;
                                setKeyString(Key.TokenKey, token);
                                setKeyString(Key.Profile, CommonObject.toJSONString(profile, profile.getClass()));
                                activityListener.updateUserInfo(profile);
                                //close();

                                getUserDetail(token);


                            } else {
                                Utils.showAlert(getContext(),
                                        getString(R.string.error),
                                        getString(R.string.msg_unable_to_sign_in),
                                        getString(R.string.close),
                                        null,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }
                                );
                                //String strMessage = responseParams.getString(Key.ResponseMessage);
                                //errorWithMessage(responseParams, strMessage);
                            }
                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                });
        showProgress(getString(R.string.msg_signing_in));
        communicator.execute(params);
    }

    @Override
    public void onBackStackChanged() {
        AbstractFragment topFragment = getTopFragment();
        if (topFragment == this) {
            String token = getKeyString(Key.TokenKey);
            if (token.length() > 0) {
                getUserDetail(token);
            } else {
                //do nothing
            }
        } else {
            //do nothing
        }
    }

    void getUserDetail(String token) {
        if (token.length() > 0) {
            Bundle params = new Bundle();
            UserInfo.Request request = new UserInfo.Request();
            request.token = token;
            params.putSerializable(Key.RequestObject, request);

            UserInfoCommunicator communicator = new UserInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile

                                    UserInfo.Response response = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    setKeyString(Key.UserProfile, CommonObject.toJSONString(response.data.user, UserInfo.User.class));

                                    activityListener.updateUserInfo(response.data.user.profile);
                                    close();
                                    //TODO: ??
                                    activityListener.showScreen(HOME_SCREEN, null);
                                } else {
                                    //clear token
                                    setKeyString(Key.TokenKey, "");
                                    setKeyString(Key.UserProfile, "");
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage);
                                }
                            } else {
                                //clear token
                                setKeyString(Key.TokenKey, "");
                                setKeyString(Key.UserProfile, "");
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        }
                    });
            showProgress(getString(R.string.msg_loading_profile));
            communicator.execute(params);
        }
    }
}
