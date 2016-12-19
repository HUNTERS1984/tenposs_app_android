package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;

import jp.tenposs.communicator.CompleteProfileCommunicator;
import jp.tenposs.communicator.SignUpCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CompleteProfileInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.SignUpInfo;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/15/16.
 */
public class FragmentSignUpNext extends AbstractFragment implements View.OnClickListener {
    TextView mGotoSignInLabel;
    Button mSignUpButton;

    TextView mGenderLabel;
    EditText mGenderEdit;
    Spinner mGenderSpinner;
    Button mGenderButton;

    TextView mBirthdayLabel;
    EditText mBirthdayEdit;
    Spinner mBirthdaySpinner;
    Button mBirthdayButton;

    TextView mAddressLabel;
    EditText mAddressEdit;
    Spinner mAddressSpinner;
    Button mAddressButton;

    int selectedGender = 2;
    int selectedBirthday = 1916;


    SignUpInfo.Request mScreenData;

//    public static FragmentSignUpNext newInstance(Serializable extras) {
//        FragmentSignUpNext fragment = new FragmentSignUpNext();
//        Bundle b = new Bundle();
//        b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
//        fragment.setArguments(b);
//        return fragment;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.sign_up_2);
        if(AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate){
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        }else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-close";
        }
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
        updateToolbar();
        final String[] genderArray = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this.getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.mGenderSpinner.setAdapter(adapterGender);
        this.mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = position;
                mGenderEdit.setText(genderArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            root = inflater.inflate(R.layout.restaurant_fragment_signup_next, null);
        } else {
            root = inflater.inflate(R.layout.fragment_signup_next, null);
        }

        this.mGenderLabel = (TextView) root.findViewById(R.id.gender_label);
        this.mGenderEdit = (EditText) root.findViewById(R.id.gender_edit);
        this.mGenderSpinner = (Spinner) root.findViewById(R.id.gender_spinner);
        this.mGenderButton = (Button) root.findViewById(R.id.gender_select_button);

        this.mBirthdayLabel = (TextView) root.findViewById(R.id.age_label);
        this.mBirthdayEdit = (EditText) root.findViewById(R.id.age_edit);
        this.mBirthdaySpinner = (Spinner) root.findViewById(R.id.age_spinner);
        this.mBirthdayButton = (Button) root.findViewById(R.id.age_select_button);

        this.mGotoSignInLabel = (TextView) root.findViewById(R.id.go_to_sign_in_label);
        this.mSignUpButton = (Button) root.findViewById(R.id.sign_up_button);

        this.mSignUpButton.setOnClickListener(this);
        this.mGenderButton.setOnClickListener(this);
        this.mBirthdayButton.setOnClickListener(this);


        Utils.setTextViewHTML(mGotoSignInLabel, getString(R.string.already_sign_up),
                new ClickableSpan() {
                    public void onClick(View view) {
                        close();
                        mActivityListener.showScreen(AbstractFragment.SIGN_IN_EMAIL_SCREEN, null, null, false);
                    }
                });
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (SignUpInfo.Request) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {
        if (this.mScreenData != null) {
            outState.putSerializable(SCREEN_DATA, mScreenData);
        }
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
        if (v == mSignUpButton) {
            if (checkInput() == false) {
                return;
            }
            Bundle params = new Bundle();
            params.putSerializable(Key.RequestObject, this.mScreenData);
            SignUpCommunicator communicator = new SignUpCommunicator(
                    new TenpossCommunicator.TenpossCommunicatorListener() {
                        @Override
                        public void completed(TenpossCommunicator request, Bundle responseParams) {
                            Utils.hideProgress();
                            int result = responseParams.getInt(Key.ResponseResult);
                            if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
                                int resultApi = responseParams.getInt(Key.ResponseResultApi);
                                if (resultApi == CommonResponse.ResultSuccess) {
                                    SignInInfo.Response response = (SignInInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    String token = response.data.token;
                                    Utils.setPrefString(getContext(), Key.TokenKey, token);
                                    Utils.setPrefString(getContext(), Key.UserProfile, CommonObject.toJSONString(response.data, response.data.getClass()));
                                    close();
                                } else {
                                    Utils.showAlert(getContext(),
                                            getString(R.string.error),
                                            getString(R.string.msg_unable_to_sign_up),
                                            getString(R.string.close),
                                            null,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage, null);
                            }
                        }
                    });
            Utils.showProgress(getContext(), getString(R.string.msg_signing_up));
            communicator.execute(params);

        } else if (v == this.mGenderButton) {
            this.mGenderSpinner.performClick();

        } else if (v == this.mBirthdayButton) {
            this.mBirthdaySpinner.performClick();
        }
    }


    @Override
    protected void updateToolbar() {
        try {
            int toolbarIconColor;
            int toolbarTitleColor;
            this.mToolbar.setBackgroundColor(Color.alpha(0));
            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                toolbarIconColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
                toolbarTitleColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_text_color);
            } else {
                this.mToolbar.setBackgroundColor(this.mToolbarSettings.getToolbarBackgroundColor());
                toolbarIconColor = this.mToolbarSettings.getToolbarIconColor();
                toolbarTitleColor = this.mToolbarSettings.getToolbarTitleColor();
            }

            if (this.mLeftToolbarButton != null) {
                this.mLeftToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarLeftIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        toolbarIconColor,
                        FontIcon.FLATICON
                ));
            }
            if (this.mRightToolbarButton != null && this.mToolbarSettings.toolbarRightIcon != null) {
                this.mRightToolbarLayout.setVisibility(View.VISIBLE);
                this.mRightToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarRightIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        toolbarIconColor,
                        FontIcon.FLATICON
                ));
            }

            if (this.mTitleToolbarLabel != null) {
                this.mTitleToolbarLabel.setText(mToolbarSettings.toolbarTitle);
                try {
                    Utils.setTextAppearanceTitle(getContext(), this.mTitleToolbarLabel, mToolbarSettings.getToolbarTitleFontSize());
//                    Typeface type = Utils.getTypeFaceForFont(getActivity(), mToolbarSettings.getToolBarTitleFont());
//                    this.mTitleToolbarLabel.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.mTitleToolbarLabel.setTextColor(toolbarTitleColor);
            }

            if (this.mToolbarSettings.toolbarType == ToolbarSettings.LEFT_MENU_BUTTON) {
                if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoaded) {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

            } else {
                this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            if (this.mScreenToolBarHidden == true) {
                this.mToolbar.setVisibility(View.VISIBLE);
            } else {
                this.mActivityListener.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                this.mToolbar.setVisibility(View.GONE);
            }

        } catch (Exception ignored) {

        }
    }

    private boolean checkInput() {
        Utils.hideKeyboard(this.getActivity(), null);
        String gender = mGenderEdit.getEditableText().toString();
        if (gender.length() <= 0) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_select_gender),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        String age = mBirthdayEdit.getEditableText().toString();
        selectedBirthday = Utils.atoi(age);
        if (selectedBirthday < 1916 || selectedBirthday > 2016) {
            Utils.showAlert(getContext(),
                    getString(R.string.warning),
                    getString(R.string.msg_select_age),
                    getString(R.string.close),
                    null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                }
                                break;
                            }
                        }
                    });
            return false;
        }
        return true;
    }

    void updateInviteInfo() {
        Bundle params = new Bundle();
        CompleteProfileInfo.Request request = new CompleteProfileInfo.Request();
        request.birthday = Integer.toString(selectedBirthday);
        request.gender = Integer.toString(selectedGender);
        new CompleteProfileCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {

            }
        }).execute(params);
    }
}

