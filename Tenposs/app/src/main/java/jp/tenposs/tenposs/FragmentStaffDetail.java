package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.AspectRatioImageView;

/**
 * Created by ambient on 7/27/16.
 */
public class FragmentStaffDetail extends AbstractFragment implements View.OnClickListener {

    AspectRatioImageView mStaffImage;
    TextView mStaffCategoryLabel;
    TextView mStaffTitleLabel;
    Button mUnknownStaffButon;
    Button mStaffDescriptionButton;
    Button mStaffProfileButton;
    TextView mStaffDescriptionLabel;
    LinearLayout mStaffProfileLayout;
    FrameLayout mStaffContentLayout;
    TextView mGenderValueLabel;
    TextView mPriceValueLabel;
    TextView mBirthdayValueLabel;
    TextView mPhoneValueLabel;
    Button mMoreButton;

    boolean mShowDescription = true;

    StaffInfo.Staff mScreenData;

//    private FragmentStaffDetail() {
//
//    }
//
//    public static FragmentStaffDetail newInstance(Serializable extras) {
//        FragmentStaffDetail fragment = new FragmentStaffDetail();
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
        mToolbarSettings.toolbarTitle = "";
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-close";
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
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
        Picasso ps = Picasso.with(getContext());
        ps.load(mScreenData.getImageUrl())
                .resize(mFullImageSize, mFullImageSize)
                .centerCrop()
                .into(this.mStaffImage);

        this.mStaffTitleLabel.setText(mScreenData.name);
        this.mStaffCategoryLabel.setText(mScreenData.staff_categories.name);
        Utils.setTextViewHTML(this.mStaffDescriptionLabel, this.mScreenData.introduction, null);

        showDescriptionOrProfile();

        try {
            if (Utils.atoi(this.mScreenData.gender) == 0) {
                this.mGenderValueLabel.setText(getString(R.string.gender_male));
            } else if (Utils.atoi(this.mScreenData.gender) == 1) {
                this.mGenderValueLabel.setText(getString(R.string.gender_female));
            } else {
                this.mGenderValueLabel.setText("");
            }
        } catch (Exception ignored) {
        }
        try {
            this.mPriceValueLabel.setText(this.mScreenData.getPrice());
        } catch (Exception ignored) {
        }

        try {
            this.mBirthdayValueLabel.setText(Utils.formatDateTime(this.mScreenData.birthday, "yyyy-MM-dd", "yyyy.MM.dd"));
        } catch (Exception ignored) {
        }
        try {
            this.mPhoneValueLabel.setText(Utils.formatPhone(this.mScreenData.tel));
        } catch (Exception ignored) {
        }

        mToolbarSettings.toolbarTitle = mScreenData.name;
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff_detail, null);
        this.mStaffImage = (AspectRatioImageView) root.findViewById(R.id.staff_image);
        this.mStaffContentLayout = (FrameLayout)root.findViewById(R.id.staff_detail_content);

        this.mStaffCategoryLabel = (TextView) root.findViewById(R.id.staff_category_label);
        this.mStaffTitleLabel = (TextView) root.findViewById(R.id.staff_title_label);
        this.mUnknownStaffButon = (Button) root.findViewById(R.id.unknown_staff_button);

        this.mMoreButton = (Button) root.findViewById(R.id.more_button);
        this.mMoreButton.setOnClickListener(this);

        View staffContent;
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            staffContent = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_staff_detail_restaurant_des_layout, container);
        } else {
            staffContent = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_staff_detail_restaurant_des_layout, container);

            this.mStaffDescriptionButton = (Button) root.findViewById(R.id.staff_description_button);
            this.mStaffProfileButton = (Button) root.findViewById(R.id.staff_profile_button);

            this.mStaffDescriptionButton.setOnClickListener(this);
            this.mStaffProfileButton.setOnClickListener(this);
        }

        if(staffContent != null) {
            this.mStaffDescriptionLabel = (TextView) staffContent.findViewById(R.id.staff_description_label);
            this.mGenderValueLabel = (TextView) staffContent.findViewById(R.id.gender_value_label);
            this.mPriceValueLabel = (TextView) staffContent.findViewById(R.id.price_value_label);
            this.mBirthdayValueLabel = (TextView) staffContent.findViewById(R.id.birthday_value_label);
            this.mPhoneValueLabel = (TextView) staffContent.findViewById(R.id.phone_value_label);
            this.mStaffProfileLayout = (LinearLayout) staffContent.findViewById(R.id.staff_profile_layout);
        }
        this.mStaffContentLayout.addView(staffContent);

        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (StaffInfo.Staff) savedInstanceState.getSerializable(SCREEN_DATA);
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

    void showDescriptionOrProfile() {
        if (mShowDescription == true) {
            mStaffDescriptionButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            mStaffDescriptionButton.setBackgroundResource(R.drawable.bg_tab_button);

            mStaffProfileButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            mStaffProfileButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);

            mStaffDescriptionLabel.setVisibility(View.VISIBLE);
            mStaffProfileLayout.setVisibility(View.GONE);
        } else {
            mStaffProfileButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            mStaffProfileButton.setBackgroundResource(R.drawable.bg_tab_button);

            mStaffDescriptionButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            mStaffDescriptionButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);

            mStaffProfileLayout.setVisibility(View.VISIBLE);
            mStaffDescriptionLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mStaffDescriptionButton) {
            mShowDescription = true;
            showDescriptionOrProfile();
        } else if (v == mStaffProfileButton) {
            mShowDescription = false;
            showDescriptionOrProfile();
        } else if (v == mMoreButton) {
            //TODO: chua co spec
        }
    }
}
