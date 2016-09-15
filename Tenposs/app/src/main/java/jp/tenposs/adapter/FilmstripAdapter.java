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

import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.UrlImageObject;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/1/16.
 */
public class FilmstripAdapter extends PagerAdapter {

    Context mContext;
    ArrayList<?> mainData;
    OnCommonItemClickListener mClickListener;
    int fullImageSize;

    public FilmstripAdapter(Context context, ArrayList<?> data) {
        this.mContext = context;
        this.mainData = data;
    }

    public FilmstripAdapter(Context context, ArrayList<?> data, OnCommonItemClickListener l) {
        this.mContext = context;
        this.mainData = data;
        this.mClickListener = l;
        this.fullImageSize = mContext.getResources().getInteger(R.integer.full_image_size);
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

        UrlImageObject image = getItem(position);
        if (image.getImageUrl() != null) {
            Picasso ps = Picasso.with(mContext);
            ps.load(image.getImageUrl())
                    .resize(fullImageSize, fullImageSize)
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