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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.ThemifyIcon;


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
            menuIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                    //item.itemIcon,
                    //item.icon,
                    item.icon,
//                    "ti-menu-alt",
                    60,
                    Color.argb(0, 0, 0, 0),
                    settings.getMenuIconColor()
            ));
            return row;
        }
    }

    Context mContext;

    LinearLayout mainLayout;
    ImageView userAvatarImage;
    TextView userNameLabel;

    RelativeLayout userInfoLayout;
    Button userInfoButton;
    Button signinButton;
    Button signupButton;
    LinearLayout signInLayout;

    ListView listView;
    LeftMenuAdapter leftMenuAdapter;

    ArrayList<AppInfo.SideMenu> screenData;
    AppInfo.AppSetting settings;
    SignInInfo.Response userInfo;

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
        this.userInfoLayout = (RelativeLayout) rootView.findViewById(R.id.user_info_layout);
        this.userInfoButton = (Button) rootView.findViewById(R.id.user_info_button);
        this.signInLayout = (LinearLayout) rootView.findViewById(R.id.signin_layout);
        this.signinButton = (Button) rootView.findViewById(R.id.signin_button);
        this.signupButton = (Button) rootView.findViewById(R.id.signup_button);
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
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.SIGNIN_SCREEN);
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
        this.signupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.SIGNUP_SCREEN);
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

    public void updateUserInfo(SignInInfo.Response userInfo) {
        this.userInfo = userInfo;
        if (this.userInfo != null) {
            this.userInfoLayout.setVisibility(View.VISIBLE);
            this.signInLayout.setVisibility(View.GONE);
        } else {
            userInfoLayout.setVisibility(View.GONE);
            this.signInLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateMenuItems(AppInfo.AppSetting settings, ArrayList<AppInfo.SideMenu> sideMenus) {
        this.settings = settings;
        this.screenData = sideMenus;

        this.signinButton.setTextColor(settings.getMenuTitleColor());
        this.signupButton.setTextColor(settings.getMenuTitleColor());
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