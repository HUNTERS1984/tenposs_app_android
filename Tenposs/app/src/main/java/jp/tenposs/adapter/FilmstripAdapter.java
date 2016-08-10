package jp.tenposs.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.TopInfo;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 8/1/16.
 */
public class FilmstripAdapter extends PagerAdapter {
    Context mContext;
    List<TopInfo.Response.ResponseData.Image> mainData;
    OnCommonItemClickListener mClickListener;

    public FilmstripAdapter(Context context, List<TopInfo.Response.ResponseData.Image> data) {
        this.mContext = context;
        this.mainData = data;
    }

    public FilmstripAdapter(Context context, List<TopInfo.Response.ResponseData.Image> data, OnCommonItemClickListener l) {
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

        TopInfo.Response.ResponseData.Image image = getItem(position);
        String strThumbUrl = image.image_url;
        Picasso ps = Picasso.with(mContext);
        ps.load(strThumbUrl)
                .resize(640, 360)
                .centerInside()
                .into(itemThumbnail);

        final int itemPosition = position;
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extraData = new Bundle();
                extraData.putSerializable(Key.RequestObject, getItem(itemPosition));
                mClickListener.onCommonItemClick(itemPosition, extraData);
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

    public TopInfo.Response.ResponseData.Image getItem(int position) {
        return this.mainData.get(position);
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