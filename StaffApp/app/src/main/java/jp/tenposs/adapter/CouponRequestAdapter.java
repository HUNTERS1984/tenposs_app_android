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

import jp.tenposs.datamodel.CouponRequestInfo;
import jp.tenposs.staffapp.R;
import jp.tenposs.utils.Utils;

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

        titleLabel.setText(coupon.name);
        descriptionLabel.setText(coupon.title);
        timeLabel.setText(Utils.formatDateTime(coupon.user_use_date, "yyyy-MM-dd", "yyyy.MM.dd") );

        Picasso ps = Picasso.with(getContext());
        ps.load(coupon.getImageUrl())
                .into(imageView);

        return convertView;
    }
}
