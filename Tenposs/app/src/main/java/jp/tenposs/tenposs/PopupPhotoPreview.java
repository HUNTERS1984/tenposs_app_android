package jp.tenposs.tenposs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.CommonItem;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.view.ZoomableImageView;

/**
 * Created by ambient on 8/29/16.
 */

public class PopupPhotoPreview {
    protected AlertDialog alert;
    protected AlertDialog.Builder alertBuilder;
    protected View contentView;

    //    ViewPager viewPager;
//    GalleryAdapter adapter;
    ZoomableImageView imageView;
    //ArrayList<?> popupData;
    Context mContext;
    String popupData;
    ImageButton closeButton;
    public int fullImageSize = 1024;


    public PopupPhotoPreview(Context context) {
        mContext = context;
    }

    public void setData(Serializable extras) {
        popupData = (String) extras;

    }

    public void show() {
        alertBuilder = new AlertDialog.Builder(this.mContext, R.style.CustomDialog);

        contentView = LayoutInflater.from(this.mContext).inflate(R.layout.popup_photo_preview, null);
        imageView = (ZoomableImageView) contentView.findViewById(R.id.item_thumbnail);
        closeButton = (ImageButton) contentView.findViewById(R.id.close_button);

        alertBuilder.setView(contentView);
        alertBuilder.setCancelable(false);
        contentView.setBackgroundColor(Color.argb(0, 0, 0, 0));

        alert = alertBuilder.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        closeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                }
        );

        Picasso ps = Picasso.with(mContext);
        ps.load(popupData)
                .into(imageView);
    }

    public class GalleryAdapter extends PagerAdapter {

        Context mContext;
        ArrayList<?> mainData;
        OnCommonItemClickListener mClickListener;

        public GalleryAdapter(Context context, ArrayList<?> data) {
            this.mContext = context;
            this.mainData = data;
        }

        public GalleryAdapter(Context context, ArrayList<?> data, OnCommonItemClickListener l) {
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
            View root = inflater.inflate(R.layout.photo_preview_item, null);

            final ImageView itemThumbnail;

            itemThumbnail = (ImageView) root.findViewById(R.id.item_thumbnail);

            CommonItem image = getItem(position);
            if (image.getImageUrl() != null) {
                Picasso ps = Picasso.with(mContext);
                ps.load(image.getImageUrl())
                        .into(itemThumbnail);
                                /*new Target() {
                            @Override
                            public vo`id onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){

                                //Set it in the ImageView
                                itemThumbnail.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }
                        });
                        */
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
            return 0;
        }

        public CommonItem getItem(int position) {
            return (CommonItem) this.mainData.get(position);
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
}
