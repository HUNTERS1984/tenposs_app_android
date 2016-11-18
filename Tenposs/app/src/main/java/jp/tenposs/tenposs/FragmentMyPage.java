package jp.tenposs.tenposs;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.utils.FontIcon;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.CircleImageView;

/**
 * Created by ambient on 10/14/16.
 */

public class FragmentMyPage extends AbstractFragment {

    SignInInfo.User mScreenData;
    private CircleImageView mUserAvatarImage;

    private FragmentMyPage() {

    }

    public static FragmentMyPage newInstance(Serializable extras) {
        FragmentMyPage fragment = new FragmentMyPage();
        return fragment;
    }

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
            this.mToolbar.setBackgroundColor(Color.alpha(0));
            if (this.mLeftToolbarButton != null) {
                this.mLeftToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarLeftIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        Color.WHITE,
                        FontIcon.FLATICON
                ));
            }
            if (this.mRightToolbarButton != null && this.mToolbarSettings.toolbarRightIcon != null) {
                this.mRightToolbarButton.setVisibility(View.VISIBLE);
                this.mRightToolbarButton.setImageBitmap(FontIcon.imageForFontIdentifier(getActivity().getAssets(),
                        this.mToolbarSettings.toolbarRightIcon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        Color.WHITE,
                        FontIcon.THEMIFY
                ));
            }

            if (this.mTitleToolbarLabel != null) {
                this.mTitleToolbarLabel.setText(mToolbarSettings.toolbarTitle);
                try {
                    Utils.setTextApperance(getContext(), this.mTitleToolbarLabel, mToolbarSettings.getToolbarTitleFontSize());
                    Typeface type = Utils.getTypeFaceForFont(getActivity(), mToolbarSettings.getToolBarTitleFont());
                    this.mTitleToolbarLabel.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.mTitleToolbarLabel.setTextColor(Color.WHITE);
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

    @Override
    protected void previewScreenData() {
        reloadImage();
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        AppData.TemplateId templateId = AppData.sharedInstance().getTemplate();
        if (templateId == AppData.TemplateId.CommonTemplate) {
            root = inflater.inflate(R.layout.fragment_my_page, null);

            //} else if (templateId == AppData.TemplateId.RestaurantTemplate) {
        } else {
            root = inflater.inflate(R.layout.restaurant_fragment_my_page, null);
        }

        this.mUserAvatarImage = (CircleImageView) root.findViewById(R.id.user_avatar_image);

        String userProfile = getPrefString(Key.UserProfile);
        this.mScreenData = (SignInInfo.User) CommonObject.fromJSONString(userProfile, SignInInfo.User.class, null);
        return root;
    }

    @Override
    protected void customResume() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (SignInInfo.User) savedInstanceState.getSerializable(SCREEN_DATA);
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
                    .resize(mFullImageSize, 640)
                    .centerInside()
                    .placeholder(R.drawable.no_avatar)
                    .into(mUserAvatarImage);
        } else {
            File f = new File(this.mScreenData.profile.getImageUrl());
            ps.load(f)
                    .resize(mFullImageSize, 640)
                    .centerInside()
                    .placeholder(R.drawable.no_avatar)
                    .into(mUserAvatarImage);
        }
    }
}
