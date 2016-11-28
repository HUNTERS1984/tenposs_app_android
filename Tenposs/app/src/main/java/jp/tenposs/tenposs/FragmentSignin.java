package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.style.ClickableSpan;
import android.util.Log;
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

import jp.tenposs.communicator.SetPushKeyCommunicator;
import jp.tenposs.communicator.SocialSignInCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserInfoCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SetPushKeyInfo;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SocialSigninInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 7/29/16.
 */
public class FragmentSignIn extends AbstractFragment implements View.OnClickListener, FragmentManager.OnBackStackChangedListener {
    private static final String SCREEN_TYPE = "SCREEN_TYPE";

    TextView mAppTitle;
    LinearLayout mSignInGroupLayout;

    ImageView mFacebookIcon;
    Button mFacebookButton;
    LoginButton mFacebookLogin;
    TwitterLoginButton mTwitterLogin;

    ImageView mTwitterIcon;
    Button mTwitterButton;

    ImageView mEmailIcon;
    Button mEmailButton;


    ViewGroup mSkipButtonLayout;
    Button mSkipButton;
    TextView mGotoSignUpLabel;

    ProfileTracker mProfileTracker;
    CallbackManager mCallbackManager;

    private FragmentSignIn() {

    }

    public static FragmentSignIn newInstance(boolean showToolbar, String appTitle) {
        FragmentSignIn instance = new FragmentSignIn();
        Bundle bundle = new Bundle();
        bundle.putBoolean(SCREEN_TYPE, showToolbar);
        bundle.putString(SCREEN_TITLE, appTitle);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Tag, "onActivityResult " + requestCode + " - " + System.currentTimeMillis());
        this.mCallbackManager.onActivityResult(requestCode, resultCode, data);
        this.mTwitterLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected boolean customClose() {
        this.mActivityListener.getFM().removeOnBackStackChangedListener(this);
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.sign_in);
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        this.mActivityListener.getFM().addOnBackStackChangedListener(this);
        if (this.mScreenToolBarHidden == true) {
            this.mSkipButtonLayout.setVisibility(View.GONE);
        } else {
            this.mSkipButtonLayout.setVisibility(View.VISIBLE);
        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            root = inflater.inflate(R.layout.restaurant_fragment_signin, null);
        } else {
            root = inflater.inflate(R.layout.fragment_signin, null);
        }

        this.mAppTitle = (TextView) root.findViewById(R.id.app_title);
        this.mSignInGroupLayout = (LinearLayout) root.findViewById(R.id.sign_in_group_layout);
        this.mFacebookIcon = (ImageView) root.findViewById(R.id.facebook_image);
        this.mFacebookButton = (Button) root.findViewById(R.id.facebook_button);
        this.mFacebookLogin = (LoginButton) root.findViewById(R.id.facebook_login);
        this.mTwitterLogin = (TwitterLoginButton) root.findViewById(R.id.twitter_login);

        this.mTwitterIcon = (ImageView) root.findViewById(R.id.twitter_image);
        this.mTwitterButton = (Button) root.findViewById(R.id.twitter_button);

        this.mEmailIcon = (ImageView) root.findViewById(R.id.email_image);
        this.mEmailButton = (Button) root.findViewById(R.id.email_button);

        this.mGotoSignUpLabel = (TextView) root.findViewById(R.id.go_to_sign_up_label);
        this.mSkipButton = (Button) root.findViewById(R.id.skip_button);
        this.mSkipButtonLayout = (ViewGroup) root.findViewById(R.id.skip_button_layout);

//        this.signInEmailLayout = (LinearLayout) root.findViewById(R.id.sign_in_email_layout);
//        this.mEmailEdit = (EditText) root.findViewById(R.id.email_edit);
//        this.mPasswordEdit = (EditText) root.findViewById(R.id.password_edit);
        //this.mSignInButton = (Button) root.findViewById(R.id.sign_in_button);
//        this.backButton = (Button) root.findViewById(R.id.back_to_group_button);

        this.mAppTitle.setText(this.mScreenTitle);
        Utils.setTextViewHTML(this.mGotoSignUpLabel, getString(R.string.not_sign_up),
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        mActivityListener.showScreen(AbstractFragment.SIGN_UP_SCREEN, null, null);
                    }
                });
        List<String> permission = new ArrayList<>();
        permission.add("public_profile");
        permission.add("email");

        this.mCallbackManager = mActivityListener.getCallbackManager();

        this.mFacebookLogin.setReadPermissions(permission);
        this.mFacebookLogin.setFragment(this);
        this.mFacebookLogin.registerCallback(this.mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                Log.i(Tag, "onSuccess " + System.currentTimeMillis());
                //Save AccessToken
                final String token = loginResult.getAccessToken().getToken();
                setPref(Key.FacebookTokenKey, token);

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

        this.mTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterAuthToken token = result.data.getAuthToken();
                String twitterToken = token.token;

                //Save Token
                setPref(Key.TwitterTokenKey, twitterToken);

                //Save secretKey
                String secretKey = token.secret;

                performSocialSignIn(SocialSigninInfo.TWITTER,
                        Long.toString(result.data.getId()),
                        result.data.getUserName(),//name
                        twitterToken,//token
                        secretKey
                );
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        mFacebookButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        mEmailButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_TYPE)) {
            this.mScreenToolBarHidden = savedInstanceState.getBoolean(SCREEN_TYPE);
        }
        if (savedInstanceState.containsKey(SCREEN_TITLE)) {
            this.mScreenTitle = savedInstanceState.getString(SCREEN_TITLE);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == this.mFacebookButton) {
            Profile profile = Profile.getCurrentProfile();
            String token = getPrefString(Key.FacebookTokenKey);
            if (profile != null && token.length() > 0) {
                // user has logged in

                performSocialSignIn(SocialSigninInfo.FACEBOOK,
                        profile.getId(),
                        profile.getName(),
                        token,
                        null);

            } else {
                this.mFacebookLogin.performClick();
            }

        } else if (v == this.mTwitterButton) {
            this.mTwitterLogin.performClick();

        } else if (v == this.mEmailButton) {
            mActivityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null, null);
            //mSignInGroupLayout.setVisibility(View.GONE);
            //signInEmailLayout.setVisibility(View.VISIBLE);

            //} else if (v == backButton) {
            //mSignInGroupLayout.setVisibility(View.VISIBLE);
            //signInEmailLayout.setVisibility(View.GONE);

            //} else if (v == mSignInButton) {
            //  if (checkInput() == true) {
            //    performEmailSignIn();
            //}
        } else if (v == this.mSkipButton) {
            close();
            mActivityListener.showFirstFragment();
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
                        if (isAdded() == false) {
                            return;
                        }
                        int result = responseParams.getInt(Key.ResponseResult);
                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                            Utils.hideProgress();
                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                            if (resultApi == CommonResponse.ResultSuccess) {
                                SignInInfo.Response response = (SignInInfo.Response) responseParams.get(Key.ResponseObject);
                                String token = response.data.token;
                                setPref(Key.TokenKey, token);
                                setPref(Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
                                mActivityListener.updateUserInfo(response.data);
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
        Utils.showProgress(getContext(), getString(R.string.msg_signing_in));
        communicator.execute(params);
    }

    @Override
    public void onBackStackChanged() {
        AbstractFragment topFragment = getTopFragment();
        if (topFragment == this) {
            String token = getPrefString(Key.TokenKey);
            if (token.length() > 0) {
                getUserDetail(token);
            } else {
                //do nothing
            }
        } else {
            //do nothing
        }
    }

    void getUserDetail(final String token) {
        if (token.length() > 0) {
            Bundle params = new Bundle();
            UserInfo.Request request = new UserInfo.Request();
            request.token = token;
            params.putSerializable(Key.RequestObject, request);

            UserInfoCommunicator communicator = new UserInfoCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            if (isAdded() == false) {
                                return;
                            }
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile

                                    UserInfo.Response response = (UserInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    setPref(Key.UserProfile, CommonObject.toJSONString(response.data.user, SignInInfo.User.class));

                                    mActivityListener.updateUserInfo(response.data.user);

                                    setPushKey(token);
                                } else {
                                    //clear token
                                    setPref(Key.TokenKey, "");
                                    setPref(Key.UserProfile, "");
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage);
                                }
                            } else {
                                Utils.hideProgress();
                                //clear token
                                setPref(Key.TokenKey, "");
                                setPref(Key.UserProfile, "");
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage);
                            }
                        }
                    });
            Utils.showProgress(getContext(), getString(R.string.msg_loading_profile));
            communicator.execute(params);
        }
    }

    private void setPushKey(String token) {
        String firebaseToken = getPrefString(Key.FireBaseTokenKey);
        if (firebaseToken != null && firebaseToken.length() > 0) {
            Bundle params = new Bundle();
            SetPushKeyInfo.Request request = new SetPushKeyInfo.Request();
            request.token = token;
            request.key = firebaseToken;
            params.putSerializable(Key.RequestObject, request);

            SetPushKeyCommunicator communicator = new SetPushKeyCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            if (isAdded() == false) {
                                return;
                            }
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    //Update User profile
                                    SetPushKeyInfo.Response response = (SetPushKeyInfo.Response) responseParams.getSerializable(Key.ResponseObject);

                                    close();
                                    mActivityListener.showScreen(TOP_SCREEN, null, null);
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
            Utils.showProgress(getContext(), getString(R.string.msg_loading_profile));
            communicator.execute(params);
        } else {
            Utils.hideProgress();
            close();
            mActivityListener.showFirstFragment();
        }
    }
}
