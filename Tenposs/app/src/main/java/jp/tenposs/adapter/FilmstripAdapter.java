package jp.tenposs.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.tenposs.datamodel.Key;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/1/16.
 */
public class FilmstripAdapter extends PagerAdapter {

    public static abstract class ImageUrl {
        public abstract String getImageUrl();
    }

    Context mContext;
    ArrayList<?> mainData;
    OnCommonItemClickListener mClickListener;

    public FilmstripAdapter(Context context, ArrayList<?> data) {
        this.mContext = context;
        this.mainData = data;
    }

    public FilmstripAdapter(Context context, ArrayList<?> data, OnCommonItemClickListener l) {
        this.mContext = context;
        this.mainData = data;
        this.mClickListener = l;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.film_strip_item, null);

        ImageView itemThumbnail;

        itemThumbnail = (ImageView) root.findViewById(R.id.item_thumbnail);

        ImageUrl image = getItem(position);
        if (image.getImageUrl() != null) {
            Picasso ps = Picasso.with(mContext);
            ps.load(image.getImageUrl())
                    .resize(640, 360)
                    .centerInside()
                    .into(itemThumbnail);
        }
        final int itemPosition = position;
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    Bundle extraData = new Bundle();
                    extraData.putSerializable(Key.RequestObject, (Serializable) getItem(itemPosition));
                    mClickListener.onCommonItemClick(itemPosition, extraData);
                }
            }
        });
        container.addView(root);
        return root;
    }

    public int getItemViewType(int position) {

        Object item = getItem(position);

        if (item == null) {
            return -1;
        }

        /*if (item instanceof VideoObject) {
            return HotItemType.HOT_ITEM_VIDEO.getValue();
        } else {
            return HotItemType.HOT_ITEM_PLAYLIST.getValue();
        }*/

        return 0;
    }

    public ImageUrl getItem(int position) {
        return (ImageUrl) this.mainData.get(position);
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