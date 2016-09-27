package jp.tenposs.tenposs;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.tenposs.communicator.InstagramAPI;
import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.SocialProfileCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UpdateProfileCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignOutInfo;
import jp.tenposs.datamodel.SocialProfileInfo;
import jp.tenposs.datamodel.SocialSigninInfo;
import jp.tenposs.datamodel.UpdateProfileInfo;
import jp.tenposs.utils.CropUtil;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.CircleImageView;

/**
 * Created by ambient on 8/17/16.
 */
public class FragmentEditProfile extends AbstractFragment implements View.OnClickListener {

    final static String InstagramClientID = "cd9f614f85f44238ace18045a51c44d1";
    final static String InstagramClientSecret = "d839149848c04447bd379ce8bff4d890";
    final static String InstagramRedirectUri = "https://www.facebook.com/hunters5inc/?notif_t=page_admin&notif_id=1461120763662795";


    CircleImageView mUserAvatarImage;
    Button mChangeAvatarButton;

    TextView mIdLabel;
    EditText mIdEdit;

    TextView mUserNameLabel;
    EditText mUserNameEdit;

    TextView mEmailLabel;
    EditText mEmailEdit;

    TextView mGenderLabel;
    Spinner mGenderSpinner;

    TextView mProvinceLabel;
    Spinner mProvinceSpinner;

    ImageView mFacebookIcon;
    TextView mLinkWithFacebookLabel;
    Button mLinkFacebookButton;
    LoginButton mFacebookLogin;

    ImageView mTwitterIcon;
    TextView mLinkWithTwitterLabel;
    Button mLinkTwitterButton;
    TwitterLoginButton mTwitterLogin;

    ImageView mInstagramIcon;
    TextView mLinkWithInstagram;
    Button mLinkInstagramButton;

    SignInInfo.User mScreenData;

    CallbackManager mCallbackManager;
    ProfileTracker mProfileTracker;

    InstagramAPI mInstagramAPI;
    private String[] mJapanPrefectures;


    int userIsInteracting = -1;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            previewScreenData();
        }
    }


    @Override
    protected boolean customClose() {
        String userName = (String) mApplication.getSerializable(Key.UpdateProfileName);
        String gender = (String) mApplication.getSerializable(Key.UpdateProfileGender);
        String address = (String) mApplication.getSerializable(Key.UpdateProfileAddress);
        Uri selectedImage = (Uri) mApplication.getParcelable(Key.UpdateProfileAvatar);

        if (userName == null && gender == null && address == null && selectedImage == null) {
            return false;
        }

        updateProfile(gender, userName, address, selectedImage);
        return true;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.edit_profile);
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarRightIcon = "flaticon-sign-out";
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
        this.mJapanPrefectures = getResources().getStringArray(R.array.japan_prefectures);
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        this.mChangeAvatarButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //show image capture
                        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.CAMERA,
                                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    AbstractFragment.CAPTURE_IMAGE_REQUEST);
                        } else {
                            showImageCapture();
                        }
                        /*FragmentSelectAvatar fragmentSelectAvatar = new FragmentSelectAvatar();
                        fragmentSelectAvatar.setTargetFragment(FragmentEditProfile.this, FragmentSelectAvatar.CAPTURE_IMAGE_INTENT);
                        //fragmentSelectAvatar.
                        mActivityListener.showFragment(fragmentSelectAvatar, FragmentSelectAvatar.class.getCanonicalName(), true);
                        /**/
                    }
                }
        );
        this.mRightToolbarButton.setVisibility(View.INVISIBLE);
        this.mRightToolbarButton.setOnClickListener(
                new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        //showalert
                        performSignOut();
                    }
                }

        );
//        this.mRightToolbarButton.setVisibility(View.VISIBLE);
        //        this.mUserAvatarImage;
        //TextView mUserNameLabel;

        Picasso ps = Picasso.with(getContext());
        ps.load(this.mScreenData.profile.getImageUrl())
                .resize(mFullImageSize, 640)
                .centerCrop()
                .placeholder(R.drawable.no_avatar)
                .into(mUserAvatarImage);

        this.mIdEdit.setText(Integer.toString(this.mScreenData.id));

        this.mUserNameEdit.setText(this.mScreenData.profile.name);

        this.mEmailEdit.setText(this.mScreenData.email);

        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userIsInteracting++;
                if (userIsInteracting > 2) {
                    mScreenData.profile.gender = position;
                    mApplication.putSerializable(Key.UpdateProfileGender, Integer.toString(position));
                    mApplication.putSerializable(Key.UpdateProfileName, mUserNameEdit.getEditableText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.mGenderSpinner.setAdapter(adapterGender);
        try {
            this.mGenderSpinner.setSelection(mScreenData.profile.gender);
        } catch (Exception ignored) {

        }

        int pos = 0;
        if (mScreenData.profile.address == null) {
            mScreenData.profile.address = this.mJapanPrefectures[0];
            mApplication.putSerializable(Key.UpdateProfileAddress, mScreenData.profile.address);
        } else {

            int i = 0;

            for (String s : this.mJapanPrefectures) {
                if (s.compareToIgnoreCase(mScreenData.profile.address) == 0) {
                    pos = i;
                    break;
                }
                i++;
            }
        }
        try {
            this.mProvinceSpinner.setSelection(pos);
        } catch (Exception ignored) {

        }
        this.mProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userIsInteracting++;
                if (userIsInteracting > 2) {
                    try {
                        mScreenData.profile.address = mJapanPrefectures[position];
                    } catch (Exception ignored) {

                    }
                    mApplication.putSerializable(Key.UpdateProfileAddress, mScreenData.profile.address);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterProvince = ArrayAdapter.createFromResource(this.getContext(),
                R.array.japan_prefectures, android.R.layout.simple_spinner_item);
        adapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.mProvinceSpinner.setAdapter(adapterProvince);

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
                final String token = loginResult.getAccessToken().getToken();
                Profile profile = Profile.getCurrentProfile();
                if (profile == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            linkWithSocialAccount(getString(R.string.msg_link_with_facebook),
                                    SocialSigninInfo.FACEBOOK,
                                    profile2.getId(),
                                    token,
                                    profile2.getName(),
                                    null);
                            mProfileTracker.stopTracking();
                        }
                    };
                } else {
                    linkWithSocialAccount(getString(R.string.msg_link_with_facebook),
                            SocialSigninInfo.FACEBOOK,
                            profile.getId(),
                            token,
                            profile.getName(),
                            null);
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

                String secretKey = token.secret;

                linkWithSocialAccount(getString(R.string.msg_link_with_twitter),
                        SocialSigninInfo.TWITTER,
                        Long.toString(result.data.getId()),
                        twitterToken,//token
                        result.data.getUserName(),
                        secretKey);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        mInstagramAPI = new InstagramAPI(getContext(),
                InstagramClientID,
                InstagramClientSecret,
                InstagramRedirectUri);

        mInstagramAPI.setListener(new InstagramAPI.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                //mInstagramAPI.fetchUserName();
                linkWithSocialAccount(getString(R.string.msg_link_with_instagram),
                        SocialSigninInfo.INSTAGRAM,
                        mInstagramAPI.getId(),
                        mInstagramAPI.getAccessToken(),
                        mInstagramAPI.getUserName(),
                        null);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        updateLinkButton();
        updateToolbar();
    }

    void updateLinkButton() {

        if (mScreenData.profile.facebook_status != 0) {
            this.mLinkFacebookButton.setText(R.string.disconnect);
        } else {
            this.mLinkFacebookButton.setText(R.string.link);
        }

        if (mScreenData.profile.twitter_status != 0) {
            this.mLinkTwitterButton.setText(R.string.disconnect);
        } else {
            this.mLinkTwitterButton.setText(R.string.link);
        }

        if (mScreenData.profile.instagram_status != 0) {
            this.mLinkInstagramButton.setText(R.string.disconnect);
        } else {
            this.mLinkInstagramButton.setText(R.string.link);
        }
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, null);
        this.mUserAvatarImage = (CircleImageView) root.findViewById(R.id.user_avatar_image);
        this.mChangeAvatarButton = (Button) root.findViewById(R.id.change_avatar_button);
        //TextView mUserNameLabel;
        //        this.idLabel = root.findViewById(R.id.id_la);
        this.mIdEdit = (EditText) root.findViewById(R.id.id_edit);

        this.mUserNameLabel = (TextView) root.findViewById(R.id.user_name_label);
        this.mUserNameEdit = (EditText) root.findViewById(R.id.user_name_edit);

        this.mEmailLabel = (TextView) root.findViewById(R.id.email_label);
        this.mEmailEdit = (EditText) root.findViewById(R.id.email_edit);

        this.mGenderLabel = (TextView) root.findViewById(R.id.gender_label);
        this.mGenderSpinner = (Spinner) root.findViewById(R.id.gender_spinner);

        this.mProvinceLabel = (TextView) root.findViewById(R.id.province_label);
        this.mProvinceSpinner = (Spinner) root.findViewById(R.id.province_spinner);

        this.mFacebookIcon = (ImageView) root.findViewById(R.id.facebook_icon);
        this.mLinkWithFacebookLabel = (TextView) root.findViewById(R.id.facebook_label);
        this.mLinkFacebookButton = (Button) root.findViewById(R.id.facebook_button);
        this.mFacebookLogin = (LoginButton) root.findViewById(R.id.facebook_login);

        this.mTwitterIcon = (ImageView) root.findViewById(R.id.twitter_icon);
        this.mLinkWithTwitterLabel = (TextView) root.findViewById(R.id.twitter_label);
        this.mLinkTwitterButton = (Button) root.findViewById(R.id.twitter_button);
        this.mTwitterLogin = (TwitterLoginButton) root.findViewById(R.id.twitter_login);

        this.mInstagramIcon = (ImageView) root.findViewById(R.id.instagram_icon);
        this.mLinkWithInstagram = (TextView) root.findViewById(R.id.instagram_label);
        this.mLinkInstagramButton = (Button) root.findViewById(R.id.instargram_button);

        this.mLinkFacebookButton.setOnClickListener(this);
        this.mLinkTwitterButton.setOnClickListener(this);
        this.mLinkInstagramButton.setOnClickListener(this);
        String userProfile = getKeyString(Key.UserProfile);
        this.mScreenData = (SignInInfo.User) CommonObject.fromJSONString(userProfile, SignInInfo.User.class, null);

        return root;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (SignInInfo.User) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, mScreenData);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            if (resultCode == PackageManager.PERMISSION_GRANTED) {
                showImageCapture();
            }
        } else if (requestCode == CAPTURE_IMAGE_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                String strFilePath = "";
                try {
                    if (data != null) {
                        Uri selectedImage = (Uri) data.getData();
                        if (selectedImage != null) {
                            //strFilePath = path.getPath();
                            if (selectedImage.getScheme().compareToIgnoreCase("file") == 0) {
                                strFilePath = selectedImage.getPath();
                            } else if (selectedImage.getScheme().compareToIgnoreCase("content") == 0) {

                                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                strFilePath = cursor.getString(columnIndex);

                                cursor.close();
                                System.out.println("file Path +" + strFilePath);  //path of sdcard
                            } else {
                                //showAlert("Not support file format.", "OK", null, AlertTag.AlertCommon.ordinal());
                                return;
                            }
                        } else {
                            Bundle extras = data.getExtras();
                            if (extras == null) {
                                strFilePath = null;
                            } else {
                                File outputDir = getActivity().getCacheDir(); // context being the Activity pointer
                                File inputFile = File.createTempFile("IMG_IN_", ".jpg", outputDir);
                                strFilePath = inputFile.getPath();
                                Bitmap bitmap = (Bitmap) extras.get("data");
                                OutputStream outputStream = null;
                                try {
                                    outputStream = getActivity().getContentResolver().openOutputStream(Uri.fromFile(inputFile));
                                    if (outputStream != null) {
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                                    }
                                } catch (IOException e) {
                                } finally {
                                    CropUtil.closeSilently(outputStream);
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    strFilePath = null;
                }

                try {
                    File outputDir = getActivity().getCacheDir(); // context being the Activity pointer
                    File outputFile = File.createTempFile("IMG_OUT_", ".png", outputDir);
                    FragmentSelectAvatar fragmentSelectAvatar =
                            FragmentSelectAvatar.newInstance(
                                    Uri.fromFile(new File(strFilePath)),
                                    Uri.fromFile(outputFile));
                    fragmentSelectAvatar.setTargetFragment(FragmentEditProfile.this, FragmentSelectAvatar.CAPTURE_IMAGE_INTENT);
                    mActivityListener.showFragment(fragmentSelectAvatar, FragmentSelectAvatar.class.getCanonicalName(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_IMAGE_INTENT) {
            if (data != null) {
                Bundle extras = data.getExtras();
                mApplication.putParceable(Key.UpdateProfileAvatar, extras.getParcelable(Key.UpdateProfileAvatar));
            }
        } else {
            this.mCallbackManager.onActivityResult(requestCode, resultCode, data);
            this.mTwitterLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    void performSignOut() {
        Utils.showAlert(this.getContext(),
                getString(R.string.info),
                getString(R.string.msg_sign_out_confirm),
                getString(R.string.yes),
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                SignOutCommunicator communicator = new SignOutCommunicator(
                                        new TenpossCommunicator.TenpossCommunicatorListener() {
                                            @Override
                                            public void completed(TenpossCommunicator request, Bundle responseParams) {
                                                hideProgress();
                                                int result = responseParams.getInt(Key.ResponseResult);
                                                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                                    if (resultApi == CommonResponse.ResultSuccess ||
                                                            resultApi == CommonResponse.ResultErrorInvalidToken) {
                                                        //clear token and user profile
                                                        setKeyString(Key.TokenKey, "");
                                                        setKeyString(Key.UserProfile, "");
                                                        mActivityListener.updateUserInfo(null);
                                                        close();
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
                                Bundle params = new Bundle();
                                SignOutInfo.Request request = new SignOutInfo.Request();
                                request.token = getKeyString(Key.TokenKey);
                                params.putSerializable(Key.RequestObject, request);
                                showProgress(getString(R.string.msg_signing_out));
                                communicator.execute(params);
                            }
                            break;

                            case DialogInterface.BUTTON_NEGATIVE: {
                                //Do nothing
                            }
                            break;
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == this.mLinkFacebookButton) {
            Profile profile = Profile.getCurrentProfile();
            String token = getKeyString(Key.FacebookTokenKey);
            if (profile != null && token.length() > 0) {
                // user has logged in
                linkWithSocialAccount(getString(R.string.msg_link_with_facebook),
                        SocialSigninInfo.FACEBOOK,
                        profile.getId(),
                        token,
                        profile.getName(),
                        null);
            } else {
                this.mFacebookLogin.performClick();
            }
        } else if (v == this.mLinkTwitterButton) {
            this.mTwitterLogin.performClick();

        } else if (v == this.mLinkInstagramButton) {
            connectOrDisconnectUser();
//            showPopupInstagram();
        }
    }

    void disconnectSocialAccount(String socialType) {

    }

    void linkWithSocialAccount(String message,
                               final String socialType,
                               String socialId,
                               String socialToken,
                               String socialName,
                               String socialSecret) {
        Bundle params = new Bundle();
        SocialProfileInfo.Request request = new SocialProfileInfo.Request();

        request.social_type = socialType;
        request.social_id = socialId;//1: facebook 2:twitter
        request.social_token = socialToken;
        request.social_secret = socialSecret;//twitter secret (used for twitter only)
        request.nickname = socialName;

        request.token = getKeyString(Key.TokenKey);
        params.putSerializable(Key.RequestObject, request);
        SocialProfileCommunicator communicator = new SocialProfileCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        hideProgress();
                        int resultApi = responseParams.getInt(Key.ResponseResultApi);
                        if (resultApi == CommonResponse.ResultSuccess) {
                            //update user profile

                            if (socialType.equalsIgnoreCase(SocialSigninInfo.FACEBOOK) == true) {
                                mScreenData.profile.facebook_status = 1;

                            } else if (socialType.equalsIgnoreCase(SocialSigninInfo.TWITTER) == true) {
                                mScreenData.profile.twitter_status = 1;

                            } else if (socialType.equalsIgnoreCase(SocialSigninInfo.INSTAGRAM) == true) {
                                mScreenData.profile.instagram_status = 1;
                            }
                            setKeyString(Key.UserProfile, CommonObject.toJSONString(mScreenData, mScreenData.getClass()));
                            updateLinkButton();

                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                }
        );

        showProgress(message);
        communicator.execute(params);
    }

    void showImageCapture() {
        final Intent videoGalleryIntent =
                new Intent(Intent.ACTION_PICK)
                        .setType("image/*")
                        .putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            final Intent chooserIntent = Intent.createChooser(videoGalleryIntent, getString(R.string.title_select_or_capture_picture));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
            getActivity().startActivityForResult(chooserIntent, CAPTURE_IMAGE_INTENT);
        }
    }

    void updateProfile(String gender, String userName, String address, Uri avatar) {
        Bundle params = new Bundle();
        UpdateProfileInfo.Request request = new UpdateProfileInfo.Request();
        request.token = getKeyString(Key.TokenKey);

        if (userName != null) {
            request.username = userName;
        }

        if (gender != null) {
            request.gender = Utils.atoi(gender);
        }

        if (address != null) {
            request.address = address;//getString(R.string.tokyo);
        }

        if (avatar != null) {
            request.avatar = avatar.getPath();
        }

        params.putSerializable(Key.RequestObject, request);
        UpdateProfileCommunicator communicator = new UpdateProfileCommunicator(
                new TenpossCommunicator.TenpossCommunicatorListener() {
                    @Override
                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                        hideProgress();
                        int resultApi = responseParams.getInt(Key.ResponseResultApi);
                        if (resultApi == CommonResponse.ResultSuccess) {
                            //clear all data

                            mApplication.remove(Key.UpdateProfileName);
                            mApplication.remove(Key.UpdateProfileGender);
                            mApplication.remove(Key.UpdateProfileAddress);
                            mApplication.remove(Key.UpdateProfileAvatar);

                            close();

                        } else {
                            String strMessage = responseParams.getString(Key.ResponseMessage);
                            errorWithMessage(responseParams, strMessage);
                        }
                    }
                }
        );

        showProgress(getString(R.string.msg_updating_profile));
        communicator.execute(params);
    }


    private void connectOrDisconnectUser() {
        if (mInstagramAPI.hasAccessToken()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    getContext());
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mInstagramAPI.resetAccessToken();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            mInstagramAPI.authorize();
        }
    }
}
