package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import jp.tenposs.communicator.SignOutCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignOutInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.view.CircleImageView;

/**
 * Created by ambient on 8/17/16.
 */
public class FragmentEditProfile extends AbstractFragment {
    ScrollView scrollView;

    CircleImageView userAvatarImage;
    //TextView userNameLabel;

    TextView idLabel;
    EditText idEdit;

    TextView userNameLabel;
    EditText userNameEdit;

    TextView emailLabel;
    EditText emailEdit;

    TextView genderLabel;
    Spinner genderSpinner;

    TextView provinceLabel;
    Spinner provinceSpinner;

    ImageView facebookIcon;
    TextView linkWithFacebookLabel;
    Button linkFacebookButton;

    ImageView twitterIcon;
    TextView linkWithTwitterLabel;
    Button linkTwitterButton;

    ImageView instagramIcon;
    TextView linkWithInstagram;
    Button linkInstargramButton;

    UserInfo.User screenData;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.edit_profile);
        toolbarSettings.toolbarLeftIcon = "flaticon-back";
        toolbarSettings.toolbarRightIcon = "flaticon-sign-out";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

        this.rightToolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showalert
                performSignOut();
            }
        });
        this.rightToolbarButton.setVisibility(View.VISIBLE);
//        this.userAvatarImage;
        //TextView userNameLabel;
        Picasso ps = Picasso.with(getContext());
        ps.load(this.screenData.profile.getImageUrl())
                .resize(640, 640)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                        //Set it in the ImageView
                        userAvatarImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }
                });

        this.idEdit.setText(Integer.toString(this.screenData.id));

        this.userNameEdit.setText(this.screenData.profile.name);

        this.emailEdit.setText(this.screenData.email);

        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.genderSpinner.setAdapter(adapterGender);
        try {
            this.genderSpinner.setSelection(screenData.profile.gender);
        } catch (Exception ignored) {

        }
        ArrayAdapter<CharSequence> adapterProvince = ArrayAdapter.createFromResource(this.getContext(),
                R.array.japan_prefectures, android.R.layout.simple_spinner_item);
        adapterProvince.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.provinceSpinner.setAdapter(adapterProvince);
        try {
            this.provinceSpinner.setSelection(screenData.profile.province);
        } catch (Exception ignored) {

        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, null);
        this.userAvatarImage = (CircleImageView) root.findViewById(R.id.user_avatar_image);
        //TextView userNameLabel;
        //        this.idLabel = root.findViewById(R.id.id_la);
        this.idEdit = (EditText) root.findViewById(R.id.id_edit);

        this.userNameLabel = (TextView) root.findViewById(R.id.user_name_label);
        this.userNameEdit = (EditText) root.findViewById(R.id.user_name_edit);

        this.emailLabel = (TextView) root.findViewById(R.id.email_label);
        this.emailEdit = (EditText) root.findViewById(R.id.email_edit);

        this.genderLabel = (TextView) root.findViewById(R.id.gender_label);
        this.genderSpinner = (Spinner) root.findViewById(R.id.gender_spinner);

        this.provinceLabel = (TextView) root.findViewById(R.id.province_label);
        this.provinceSpinner = (Spinner) root.findViewById(R.id.province_spinner);

        this.facebookIcon = (ImageView) root.findViewById(R.id.facebook_icon);
        this.linkWithFacebookLabel = (TextView) root.findViewById(R.id.facebook_label);
        this.linkFacebookButton = (Button) root.findViewById(R.id.facebook_button);

        this.twitterIcon = (ImageView) root.findViewById(R.id.twitter_icon);
        this.linkWithTwitterLabel = (TextView) root.findViewById(R.id.twitter_label);
        this.linkTwitterButton = (Button) root.findViewById(R.id.twitter_button);

        this.instagramIcon = (ImageView) root.findViewById(R.id.instagram_icon);
        this.linkWithInstagram = (TextView) root.findViewById(R.id.instagram_label);
        this.linkInstargramButton = (Button) root.findViewById(R.id.instargram_button);

        String userProfile = getKeyString(Key.UserProfile);
        this.screenData = (UserInfo.User) CommonObject.fromJSONString(userProfile, UserInfo.User.class, null);

        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (UserInfo.User) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        outState.putSerializable(SCREEN_DATA, screenData);
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    void performSignOut() {
        showAlert(null, getString(R.string.msg_sign_out_confirm),
                getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: {
                                SignOutCommunicator communicator = new SignOutCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
                                    @Override
                                    public void completed(TenpossCommunicator request, Bundle responseParams) {
                                        int result = responseParams.getInt(Key.ResponseResult);
                                        if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                            int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                            if (resultApi == CommonResponse.ResultSuccess) {
                                                //clear token and user profile
                                                setKeyString(Key.TokenKey, "");
                                                setKeyString(Key.UserProfile, "");
                                                setKeyString(Key.Profile, "");
                                                close();
                                                activityListener.updateUserInfo(null);
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
}
