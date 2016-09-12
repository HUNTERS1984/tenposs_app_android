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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.FlatIcon;


/**
 * Created by ambient on 7/28/16.
 */
public class LeftMenuView extends FrameLayout {

    public interface OnLeftMenuItemClickListener {
        void onClick(int position, Bundle params);
    }

    class LeftMenuAdapter extends ArrayAdapter<AppInfo.SideMenu> {
        LayoutInflater mInflater;

        public LeftMenuAdapter(Context context, int resource, ArrayList<AppInfo.SideMenu> objects) {
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
            AppInfo.SideMenu item = getItem(position);
            ImageView menuIcon = (ImageView) row.findViewById(R.id.item_image);
            TextView menuTitle = (TextView) row.findViewById(R.id.item_label);
            menuTitle.setTextColor(settings.getMenuTitleColor());
            menuTitle.setText(item.name);
            menuIcon.setImageBitmap(FlatIcon.fromFlatIcon(getContext().getAssets(),
                    AbstractFragment.getMenuIconName(item.id),
                    60,
                    Color.argb(0, 0, 0, 0),
                    settings.getMenuIconColor()
            ));
            return row;
        }
    }

    Context mContext;

    LinearLayout mainLayout;
    CircleImageView userAvatarImage;
    TextView userNameLabel;

    Button userInfoButton;
    Button signinButton;

    ListView listView;
    LeftMenuAdapter leftMenuAdapter;

    ArrayList<AppInfo.SideMenu> screenData;
    AppInfo.AppSetting settings;
    SignInInfo.Profile profile;

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
        this.mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);
        this.userInfoButton = (Button) rootView.findViewById(R.id.user_info_button);
        this.signinButton = (Button) rootView.findViewById(R.id.sign_in_button);
        this.userAvatarImage = (CircleImageView) rootView.findViewById(R.id.user_avatar_image);
        this.userNameLabel = (TextView) rootView.findViewById(R.id.user_name_label);
        this.listView = (ListView) rootView.findViewById(R.id.list_view);

        this.signinButton.setVisibility(View.VISIBLE);

        this.signinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.SIGN_IN_SCREEN);
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
        this.userInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.PROFILE_SCREEN);
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void updateUserInfo(SignInInfo.Profile profile) {
        this.profile = profile;
        if (this.profile != null) {
            userNameLabel.setText(profile.name);
            Picasso ps = Picasso.with(mContext);
            ps.load(this.profile.getImageUrl())
                    .resize(640, 360)
                    .centerCrop()
                    .placeholder(R.drawable.no_avatar)
                    .into(userAvatarImage);
            userInfoButton.setVisibility(View.VISIBLE);
            signinButton.setVisibility(View.GONE);
        } else {
            userNameLabel.setText(mContext.getString(R.string.sign_in));
            userInfoButton.setVisibility(View.GONE);
            signinButton.setVisibility(View.VISIBLE);
        }
    }

    public void updateMenuItems(AppInfo.AppSetting settings, ArrayList<AppInfo.SideMenu> sideMenus) {
        this.settings = settings;
        this.screenData = sideMenus;
        //this.screenData.add(new AppInfo.SideMenu(AbstractFragment.SETTING_SCREEN, "Settings"));
        this.screenData.add(new AppInfo.SideMenu(AbstractFragment.SIGN_OUT_SCREEN, mContext.getString(R.string.sign_out)));

        this.signinButton.setTextColor(settings.getMenuTitleColor());
        this.mainLayout.setBackgroundColor(this.settings.getMenuBackgroundColor());
        this.leftMenuAdapter = new LeftMenuAdapter(mContext, R.layout.nav_content_item, screenData);
        this.listView.setAdapter(leftMenuAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putSerializable(Key.RequestObject, screenData.get(position));
                    onItemClickListener.onClick(position, params);
                }
            }
        });
    }
}