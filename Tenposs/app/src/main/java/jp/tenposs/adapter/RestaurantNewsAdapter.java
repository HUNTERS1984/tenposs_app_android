package jp.tenposs.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.tenposs.datamodel.UrlImageObject;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 11/14/16.
 */
public class RestaurantNewsAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<?> mainData;

    public RestaurantNewsAdapter(Context context, ArrayList<?> data) {
        this.mContext = context;
        this.mainData = data;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.restaurant_news_pager_item, null);

        ImageView itemThumbnail;

        itemThumbnail = (ImageView) root.findViewById(R.id.item_image);

        UrlImageObject image = getItem(position);
        if (image.getImageUrl() != null) {
            Picasso ps = Picasso.with(mContext);
            ps.load(image.getImageUrl())
                    .into(itemThumbnail);
        }
        container.addView(root);
        return root;
    }

    public UrlImageObject getItem(int position) {
        return (UrlImageObject) this.mainData.get(position);
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
