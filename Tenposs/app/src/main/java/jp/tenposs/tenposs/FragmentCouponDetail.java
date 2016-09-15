package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.view.AspectRatioImageView;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentCouponDetail extends AbstractFragment {

    AspectRatioImageView couponImage;
    TextView couponIdLabel;
    TextView couponTypeLabel;
    TextView couponNameLabel;
    TextView validityLabel;
    TextView hashTagLabel;
    Button copyHashTagButton;
    TextView couponDescriptionLabel;

    LinearLayout takeAdvantageOfCouponLayout;
    LinearLayout couponCannotUseLayout;

    CouponInfo.Coupon screenData;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "";
        toolbarSettings.toolbarLeftIcon = "flaticon-back";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        toolbarSettings.toolbarTitle = screenData.title;

        Picasso ps = Picasso.with(getContext());
        ps.load(screenData.getImageUrl())
                .resize(fullImageSize, fullImageSize)
                .centerCrop()
                .into(couponImage);

        this.couponIdLabel.setText(Integer.toString(screenData.id));
        this.couponTypeLabel.setText(Integer.toString(screenData.type));
        this.couponNameLabel.setText(screenData.title);
        this.validityLabel.setText(screenData.end_date);
        this.couponDescriptionLabel.setText(screenData.description);

        if (this.screenData.status == 1) {
            this.takeAdvantageOfCouponLayout.setVisibility(View.VISIBLE);
            this.couponCannotUseLayout.setVisibility(View.GONE);
        } else {
            this.takeAdvantageOfCouponLayout.setVisibility(View.GONE);
            this.couponCannotUseLayout.setVisibility(View.VISIBLE);
        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon_detail, null);
        this.couponImage = (AspectRatioImageView) root.findViewById(R.id.coupon_image);
        this.couponIdLabel = (TextView) root.findViewById(R.id.coupon_id_label);
        this.couponTypeLabel = (TextView) root.findViewById(R.id.coupon_type_label);
        this.couponNameLabel = (TextView) root.findViewById(R.id.coupon_name_label);
        this.validityLabel = (TextView) root.findViewById(R.id.validity_label);
        this.hashTagLabel = (TextView) root.findViewById(R.id.hash_tag_label);
        this.copyHashTagButton = (Button) root.findViewById(R.id.copy_hash_tag_button);
        this.couponDescriptionLabel = (TextView) root.findViewById(R.id.coupon_description_label);
        this.takeAdvantageOfCouponLayout = (LinearLayout) root.findViewById(R.id.take_advantage_of_coupon_layout);
        this.couponCannotUseLayout = (LinearLayout) root.findViewById(R.id.coupon_cannot_use_layout);
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (CouponInfo.Coupon) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }
}
