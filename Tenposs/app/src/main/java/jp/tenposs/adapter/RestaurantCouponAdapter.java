package jp.tenposs.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.Utils;

/**
 * Created by ambient on 11/13/16.
 */

//public class RestaurantCouponAdapter
//        extends FragmentPagerAdapter {
//
//    ArrayList<CouponInfo.Coupon> mDatas;
//
//    Context mContext;
//    ArrayList<Fragment> mFragments;
//
//    public RestaurantCouponAdapter(FragmentManager fm, Context context) {
//        super(fm);
//        this.mContext = context;
//        this.mFragments = new ArrayList<>();
//
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        CouponInfo.Coupon coupon = mDatas.get(position);
//        Fragment curFragment = this.mFragments.get(position);
//        if (curFragment == null) {
//            curFragment = RestaurantFragmentCouponDetail.newInstance(mContext, coupon);
//            this.mFragments.set(position, curFragment);
//        }
//        return curFragment;
//    }
//
//    @Override
//    public int getCount() {
//        return mDatas.size();
//    }
//
//    public void setData(ArrayList<CouponInfo.Coupon> datas) {
//        this.mDatas = datas;
//        for (int i = 0; i < datas.size(); i++) {
//            this.mFragments.add(null);
//        }
//    }
//}

public class RestaurantCouponAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<?> mainData;
    OnCommonItemClickListener mClickListener;
    int fullImageSize;

    public RestaurantCouponAdapter(Context context, ArrayList<?> data, OnCommonItemClickListener listener) {
        this.mContext = context;
        this.mainData = data;
        this.mClickListener = listener;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.restaurant_fragment_coupon_detail, null);

        final ImageView mCouponImage;
        TextView mCouponIdLabel;
        TextView mCouponTypeLabel;
        TextView mCouponNameLabel;
        TextView mValidityLabel;

        final ImageView mBarcodeImage;
        TextView mCouponDescriptionLabel;

        Button mUseCouponButton;

        final CouponInfo.Coupon mScreenData = (CouponInfo.Coupon) this.mainData.get(position);


        mCouponImage = (ImageView) root.findViewById(R.id.coupon_image);
        mCouponIdLabel = (TextView) root.findViewById(R.id.coupon_id_label);
        mCouponTypeLabel = (TextView) root.findViewById(R.id.coupon_type_label);
        mCouponNameLabel = (TextView) root.findViewById(R.id.coupon_name_label);
        mValidityLabel = (TextView) root.findViewById(R.id.validity_label);
        mBarcodeImage = (ImageView) root.findViewById(R.id.coupon_barcode_image);
        mCouponDescriptionLabel = (TextView) root.findViewById(R.id.coupon_description_label);

        mUseCouponButton = (Button) root.findViewById(R.id.use_coupon_button);
        mCouponIdLabel.setText(Integer.toString(mScreenData.id));
        mCouponTypeLabel.setText(Integer.toString(mScreenData.type));
        mCouponNameLabel.setText(mScreenData.getTitle());
        mValidityLabel.setText(mScreenData.end_date);

        mUseCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onCommonItemClick(position, null);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Picasso ps = Picasso.with(mContext);
                ps.load(mScreenData.getImageUrl()).into(mCouponImage);

                try {
                    Utils.generateQRCode("ABCXYZ", mBarcodeImage, 200);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }, 200);

        container.addView(root);
        return root;
    }

    @Override
    public int getCount() {
        return mainData.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}