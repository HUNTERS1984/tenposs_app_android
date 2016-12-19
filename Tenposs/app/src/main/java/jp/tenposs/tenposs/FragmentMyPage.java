package jp.tenposs.tenposs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Locale;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UserPointCommunicator;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.datamodel.UserPointInfo;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.CircleImageView;
import jp.tenposs.view.CircularProgressBar;

/**
 * Created by ambient on 10/14/16.
 */

public class FragmentMyPage extends AbstractFragment {

    UserInfo.User mScreenData;
    UserPointInfo.Response mPointData;

    CircleImageView mUserAvatarImage;
    ImageButton mEditProfileButton;

    TextView mUserNameLabel;
    TextView mEmailLabel;

    CircularProgressBar mMilesProgress;
    ImageView mAppLogo;
    TextView mAppNameLabel;
    TextView mTotalPointLabel;
    TextView mTotalMilesLabel;
    TextView mMilesLabel;
    ImageView mBarcodeImage;
    TextView mNextStatusLabel;

//    public static FragmentMyPage newInstance(Serializable extras) {
//        FragmentMyPage fragment = new FragmentMyPage();
//        return fragment;
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            previewScreenData();
        }
    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.my_page);
        mToolbarSettings.toolbarLeftIcon = "flaticon-main-menu";
        mToolbarSettings.toolbarRightIcon = "ti-settings";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {

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
                toolbarIconColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_icon_color);
                toolbarTitleColor = Color.WHITE;//Utils.getColor(getContext(), R.color.restaurant_toolbar_text_color);
                //toolbarIconColor = this.mToolbarSettings.getToolbarIconColor();
                //toolbarTitleColor = this.mToolbarSettings.getToolbarTitleColor();
            }

            if (this.mLeftToolbarButton != null) {
                this.mLeftToolbarButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
                        Color.WHITE,
                        FontIcon.THEMIFY
                ));
                this.mRightToolbarButton.setOnClickListener(
                        new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View v) {
                                openSetting();
                            }
                        }
                );
            } else if (this.mRightToolbarLayout != null) {
                this.mRightToolbarLayout.setVisibility(View.INVISIBLE);
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

    private void openSetting() {
        this.mActivityListener.showScreen(AbstractFragment.SETTING_SCREEN, null, null, false);
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        reloadImage();
        try {
            if (this.mUserNameLabel != null) {
                this.mUserNameLabel.setText(this.mScreenData.profile.name);
            }

            String s = Utils.iToCurrency(mPointData.data.next_points)
                    + "ポイント獲得まであと、"
                    + Utils.iToCurrency(mPointData.data.next_miles)
                    + "マイル";

            this.mNextStatusLabel.setText(s);

            if (this.mEmailLabel != null) {
                this.mEmailLabel.setText(this.mScreenData.getEmail());
            }
            if (this.mTotalPointLabel != null) {
                this.mTotalPointLabel.setText(Utils.iToCurrency(mPointData.data.points) + "ポイント");
            }

            if (this.mTotalMilesLabel != null) {
                this.mTotalMilesLabel.setText(Utils.iToCurrency(mPointData.data.miles));
            }

            float percent = 0.0f;
            if (mPointData.data.next_miles > 0)
                percent = mPointData.data.miles * 100.0f / mPointData.data.next_miles;
            this.mMilesProgress.setProgress(percent);

        } catch (Exception ignored) {

        }
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;

        AppData.TemplateId templateId = AppData.sharedInstance().getTemplate();
        if (templateId == AppData.TemplateId.CommonTemplate) {
            root = inflater.inflate(R.layout.fragment_my_page, null);

        } else if (templateId == AppData.TemplateId.RestaurantTemplate) {
            root = inflater.inflate(R.layout.restaurant_fragment_my_page, null);

        } else {
            root = null;
        }

        this.mUserAvatarImage = (CircleImageView) root.findViewById(R.id.user_avatar_image);
        this.mEditProfileButton = (ImageButton) root.findViewById(R.id.edit_profile_button);

        this.mUserNameLabel = (TextView) root.findViewById(R.id.user_name_label);
        this.mEmailLabel = (TextView) root.findViewById(R.id.email_label);
        this.mNextStatusLabel = (TextView) root.findViewById(R.id.next_status_label);

        this.mMilesProgress = (CircularProgressBar) root.findViewById(R.id.miles_progress);
        this.mAppLogo = (ImageView) root.findViewById(R.id.app_logo);
        this.mAppNameLabel = (TextView) root.findViewById(R.id.app_name_label);
        this.mTotalPointLabel = (TextView) root.findViewById(R.id.total_point_label);
        this.mTotalMilesLabel = (TextView) root.findViewById(R.id.total_miles_label);
        this.mMilesLabel = (TextView) root.findViewById(R.id.miles_label);
        this.mBarcodeImage = (ImageView) root.findViewById(R.id.barcode_image);

        String userProfile = Utils.getPrefString(getContext(), Key.UserProfile);
        this.mScreenData = (UserInfo.User) CommonObject.fromJSONString(userProfile, UserInfo.User.class, null);

        if (this.mEditProfileButton != null) {
            this.mEditProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSignedIn() == true) {
                        FragmentMyPage.this.mActivityListener.showScreen(AbstractFragment.PROFILE_SCREEN, null, null, false);
                    } else {
                        Utils.showAlert(FragmentMyPage.this.getContext(),
                                getString(R.string.info),
                                getString(R.string.msg_not_sign_in),
                                getString(R.string.close),
                                null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    }
                }
            });
        }
        return root;
    }

    @Override
    protected void customResume() {
        if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload && isSignedIn()) {
            Bundle params = new Bundle();

            Utils.showProgress(getContext(), getString(R.string.msg_loading));
            UserPointInfo.Request request = new UserPointInfo.Request();
            params.putSerializable(Key.RequestObject, request);
            params.putString(Key.TokenKey, Utils.getPrefString(getContext(), Key.TokenKey));
            UserPointCommunicator communicator = new UserPointCommunicator(
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
                                    try {
                                        FragmentMyPage.this.mPointData = (UserPointInfo.Response) responseParams.getSerializable(Key.ResponseObject);
                                    } catch (Exception ignored) {
                                        Utils.log(ignored);
                                    }
                                    previewScreenData();
                                } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
                                    refreshToken(new TenpossCallback() {
                                        @Override
                                        public void onSuccess(Bundle params) {
                                            customResume();
                                        }

                                        @Override
                                        public void onFailed(Bundle params) {
                                            //Logout, then do something
                                            mActivityListener.logoutBecauseExpired();
                                        }
                                    });
                                } else {
                                    String strMessage = responseParams.getString(Key.ResponseMessage);
                                    errorWithMessage(responseParams, strMessage, null);
                                }
                            } else {
                                String strMessage = responseParams.getString(Key.ResponseMessage);
                                errorWithMessage(responseParams, strMessage, null);
                            }
                        }
                    }
            );
            communicator.execute(params);
        } else {
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (UserInfo.User) savedInstanceState.getSerializable(SCREEN_DATA);
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
        return false;
    }

    private void reloadImage() {
        Picasso ps = Picasso.with(getContext());
        String url = this.mScreenData.profile.getImageUrl().toLowerCase(Locale.US);
        if (url.contains("http://") == true || url.contains("https://") == true) {
            ps.load(this.mScreenData.profile.getImageUrl())
                    .placeholder(R.drawable.mypage_no_avatar)
                    .into(mUserAvatarImage);
        } else {
            File f = new File(this.mScreenData.profile.getImageUrl());
            ps.load(f)
                    .placeholder(R.drawable.no_avatar_gray)
                    .into(mUserAvatarImage);
        }
    }
}
