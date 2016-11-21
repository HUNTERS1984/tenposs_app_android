package jp.tenposs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.CouponRequestInfo;
import jp.tenposs.staffapp.R;

/**
 * Created by ambient on 10/17/16.
 */

public class CouponRequestAdapter extends ArrayAdapter<CouponRequestInfo.RequestInfo> {
    public CouponRequestAdapter(Context context, ArrayList<CouponRequestInfo.RequestInfo> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CouponRequestInfo.RequestInfo coupon = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.coupon_list_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.coupon_image);
        TextView titleLabel = (TextView) convertView.findViewById(R.id.requestor_label);
        TextView descriptionLabel = (TextView) convertView.findViewById(R.id.coupon_title_label);
        TextView timeLabel = (TextView) convertView.findViewById(R.id.time_label);

        titleLabel.setText(coupon.title);
        descriptionLabel.setText(coupon.title);
        timeLabel.setText("Just now");

        Picasso ps = Picasso.with(getContext());
        ps.load(coupon.getImageUrl())
                .resize(320, 320)
                .centerInside()
                .into(imageView);

        return convertView;
    }
}
