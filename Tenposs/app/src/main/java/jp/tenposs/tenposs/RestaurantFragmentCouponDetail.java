package jp.tenposs.tenposs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 8/4/16.
 */
public class RestaurantFragmentCouponDetail extends AbstractFragment {

    ImageView mCouponImage;
    TextView mCouponIdLabel;
    TextView mCouponTypeLabel;
    TextView mCouponNameLabel;
    TextView mValidityLabel;

    ImageView mBarcodeImage;
    TextView mCouponDescriptionLabel;

    Button mUseCouponButton;

    CouponInfo.Coupon mScreenData;


    public RestaurantFragmentCouponDetail() {

    }

    public static RestaurantFragmentCouponDetail newInstance(Context context, Serializable extras) {

        Bundle b = new Bundle();
        b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
        //fragment.setArguments(b);
        //RestaurantFragmentCouponDetail fragment = new RestaurantFragmentCouponDetail();
        RestaurantFragmentCouponDetail fragment = (RestaurantFragmentCouponDetail) Fragment.instantiate(context, RestaurantFragmentCouponDetail.class.getName(), b);
        return fragment;
    }

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
        mToolbarSettings.toolbarTitle = mScreenData.getTitle();

        this.mCouponIdLabel.setText(Integer.toString(mScreenData.id));
        this.mCouponTypeLabel.setText(Integer.toString(mScreenData.type));
        this.mCouponNameLabel.setText(mScreenData.getTitle());
        this.mValidityLabel.setText(mScreenData.end_date);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Picasso ps = Picasso.with(getContext());
                ps.load(mScreenData.getImageUrl()).into(mCouponImage);

                try {
                    Utils.generateQRCode("ABCXYZ", mBarcodeImage, 200);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_fragment_coupon_detail, null);
        this.mCouponImage = (ImageView) root.findViewById(R.id.coupon_image);
        this.mCouponIdLabel = (TextView) root.findViewById(R.id.coupon_id_label);
        this.mCouponTypeLabel = (TextView) root.findViewById(R.id.coupon_type_label);
        this.mCouponNameLabel = (TextView) root.findViewById(R.id.coupon_name_label);
        this.mValidityLabel = (TextView) root.findViewById(R.id.validity_label);
        this.mBarcodeImage = (ImageView) root.findViewById(R.id.coupon_barcode_image);
        this.mCouponDescriptionLabel = (TextView) root.findViewById(R.id.coupon_description_label);
        this.mUseCouponButton = (Button) root.findViewById(R.id.use_coupon_button);
        this.mUseCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
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
