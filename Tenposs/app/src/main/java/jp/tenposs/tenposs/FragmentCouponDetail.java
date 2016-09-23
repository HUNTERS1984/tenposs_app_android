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

    AspectRatioImageView mCouponImage;
    TextView mCouponIdLabel;
    TextView mCouponTypeLabel;
    TextView mCouponNameLabel;
    TextView mValidityLabel;
    TextView mHashTagLabel;
    Button mCopyHashTagButton;
    TextView mCouponDescriptionLabel;

    LinearLayout mTakeAdvantageOfCouponLayout;
    Button mTakeAdvantageOfCouponButton;
    LinearLayout mCouponCannotUseLayout;

    CouponInfo.Coupon mScreenData;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = "";
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
        mToolbarSettings.toolbarTitle = mScreenData.title;

        Picasso ps = Picasso.with(getContext());
        ps.load(mScreenData.getImageUrl())
                .resize(mFullImageSize, mFullImageSize)
                .centerCrop()
                .into(mCouponImage);

        this.mCouponIdLabel.setText(Integer.toString(mScreenData.id));
        this.mCouponTypeLabel.setText(Integer.toString(mScreenData.type));
        this.mCouponNameLabel.setText(mScreenData.title);
        this.mValidityLabel.setText(mScreenData.end_date);
        this.mCouponDescriptionLabel.setText(mScreenData.description);

        if (this.mScreenData.status == 1) {
            this.mTakeAdvantageOfCouponLayout.setVisibility(View.VISIBLE);
            this.mCouponCannotUseLayout.setVisibility(View.GONE);
        } else {
            this.mTakeAdvantageOfCouponLayout.setVisibility(View.GONE);
            this.mCouponCannotUseLayout.setVisibility(View.VISIBLE);
        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon_detail, null);
        this.mCouponImage = (AspectRatioImageView) root.findViewById(R.id.coupon_image);
        this.mCouponIdLabel = (TextView) root.findViewById(R.id.coupon_id_label);
        this.mCouponTypeLabel = (TextView) root.findViewById(R.id.coupon_type_label);
        this.mCouponNameLabel = (TextView) root.findViewById(R.id.coupon_name_label);
        this.mValidityLabel = (TextView) root.findViewById(R.id.validity_label);
        this.mHashTagLabel = (TextView) root.findViewById(R.id.hash_tag_label);
        this.mCopyHashTagButton = (Button) root.findViewById(R.id.copy_hash_tag_button);
        this.mCouponDescriptionLabel = (TextView) root.findViewById(R.id.coupon_description_label);
        this.mTakeAdvantageOfCouponLayout = (LinearLayout) root.findViewById(R.id.take_advantage_of_coupon_layout);
        this.mTakeAdvantageOfCouponButton = (Button) root.findViewById(R.id.take_advantage_of_coupon_button);
        this.mCouponCannotUseLayout = (LinearLayout) root.findViewById(R.id.coupon_cannot_use_layout);

        this.mCopyHashTagButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupHashTagCopied popupHashTagCopied = new PopupHashTagCopied(getContext());
                popupHashTagCopied.show();
            }
        });
        this.mTakeAdvantageOfCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use coupon
                //generate barcode
                //show Popup
                PopupCouponQR qrPreview = new PopupCouponQR(getContext());
                //photoPreview.setData(extras);
                qrPreview.show();
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
            this.mScreenData = (CouponInfo.Coupon) savedInstanceState.getSerializable(SCREEN_DATA);
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
}
