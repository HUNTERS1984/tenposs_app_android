package jp.tenposs.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jp.tenposs.datamodel.HomeObject;
import jp.tenposs.datamodel.HomeScreenItem;
import jp.tenposs.datamodel.Key;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.ThemifyIcon;


/**
 * Created by ambient on 7/28/16.
 */
public class LeftMenuView extends FrameLayout {

    public interface OnLeftMenuItemClickListener {
        void onClick(int position, Bundle params);
    }

    class LeftMenuAdapter extends ArrayAdapter<HomeScreenItem> {
        LayoutInflater mInflater;

        public LeftMenuAdapter(Context context, int resource, List<HomeScreenItem> objects) {
            super(context, resource, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            if (null == convertView) {
                row = mInflater.inflate(R.layout.nav_content_item, null);
            } else {
                row = convertView;
            }
            HomeScreenItem item = getItem(position);
            ImageView menuIcon = (ImageView) row.findViewById(R.id.item_image);
            TextView menuTitle = (TextView) row.findViewById(R.id.item_label);
            menuTitle.setText(item.itemName);
            menuIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                    item.itemIcon,
                    60,
                    Color.argb(0, 0, 0, 0),
                    Color.argb(255, 255, 255, 255)
            ));
            return row;
        }
    }

    Context mContext;

    ImageView userAvatarImage;
    TextView userNameLabel;

    LinearLayout userInfoLayout;
    Button signinButton;

    ListView listView;
    LeftMenuAdapter leftMenuAdapter;

    HomeObject screenData;
    OnLeftMenuItemClickListener onItemClickListener;

    public LeftMenuView(Context context) {
        super(context);
        this.mContext = context;
        initView(this.mContext);
    }

    public LeftMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(this.mContext);
    }

    public LeftMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(this.mContext);
    }

    public void setOnItemClickListener(OnLeftMenuItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    void initView(Context context) {
        View rootView = View.inflate(context, R.layout.nav_content_main, this);
        this.userInfoLayout = (LinearLayout) rootView.findViewById(R.id.user_info_layout);
        this.signinButton = (Button) rootView.findViewById(R.id.signin_button);
        this.userAvatarImage = (ImageView) rootView.findViewById(R.id.user_avatar_image);
        this.userNameLabel = (TextView) rootView.findViewById(R.id.user_name_label);
        this.listView = (ListView) rootView.findViewById(R.id.list_view);

        this.userInfoLayout.setVisibility(View.GONE);
        this.signinButton.setVisibility(View.VISIBLE);
        this.signinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
//                    params.putSerializable(Key.RequestObject, );
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void updateMenuItems(HomeObject homeObject) {
        screenData = homeObject;
        leftMenuAdapter = new LeftMenuAdapter(mContext, R.layout.nav_content_item, screenData.items);
        this.listView.setAdapter(leftMenuAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putSerializable(Key.RequestObject, screenData.items.get(position));
                    onItemClickListener.onClick(position, params);
                }
            }
        });
    }
}