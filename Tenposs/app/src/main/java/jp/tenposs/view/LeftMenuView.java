package jp.tenposs.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import jp.tenposs.utils.Utils;


/**
 * Created by ambient on 7/28/16.
 */
public class LeftMenuView extends FrameLayout {

    int fullImageSize;

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

            menuTitle.setText(item.name);

            if (menuTitle != null) {
                try {
                    Utils.setTextApperance(getContext(), menuTitle, mSettings.getMenuItemTitleFontSize());
                    Typeface type = Utils.getTypeFaceForFont(mContext, mSettings.getMenuItemTitleFont());
                    menuTitle.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            menuTitle.setTextColor(mSettings.getMenuItemTitleColor());
//            menuIcon.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
//                    item.icon,
//                    60,
//                    Color.argb(0, 0, 0, 0),
//                    mSettings.getMenuIconColor()
//            ));
            menuIcon.setImageBitmap(MenuIcon.getInstance().getIconBitmapWithId(item.id));
            return row;
        }
    }

    Context mContext;

    LinearLayout mMainLayout;
    CircleImageView mUserAvatarImage;
    TextView mUserNameLabel;

    Button mUserInfoButton;
    Button mSignInButton;

    ListView mListView;
    LeftMenuAdapter mAdapter;

    ArrayList<AppInfo.SideMenu> mScreenData;
    AppInfo.AppSetting mSettings;
    SignInInfo.User mUserProfile;

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
        this.mMainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);
        this.mUserInfoButton = (Button) rootView.findViewById(R.id.user_info_button);
        this.mSignInButton = (Button) rootView.findViewById(R.id.sign_in_button);
        this.mUserAvatarImage = (CircleImageView) rootView.findViewById(R.id.user_avatar_image);
        this.mUserNameLabel = (TextView) rootView.findViewById(R.id.user_name_label);
        this.mListView = (ListView) rootView.findViewById(R.id.list_view);

        this.mSignInButton.setVisibility(View.VISIBLE);

        this.mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.SIGN_IN_SCREEN);
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
        this.mUserInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    Bundle params = new Bundle();
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.PROFILE_SCREEN);
                    onItemClickListener.onClick(-1, params);
                }
            }
        });
        this.fullImageSize = context.getResources().getInteger(R.integer.full_image_size);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void updateMenu(AppInfo.AppSetting settings, ArrayList<AppInfo.SideMenu> sideMenus, SignInInfo.User userProfile) {
        this.mSettings = settings;
        this.mScreenData = (ArrayList<AppInfo.SideMenu>) sideMenus.clone();
        this.mUserProfile = userProfile;

        if (this.mUserProfile != null) {
            mUserNameLabel.setText(userProfile.profile.name);
            Picasso ps = Picasso.with(mContext);
            ps.load(this.mUserProfile.profile.getImageUrl())
                    .resize(fullImageSize, 640)
                    .centerCrop()
                    .placeholder(R.drawable.no_avatar)
                    .into(mUserAvatarImage);
            mUserInfoButton.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.GONE);
            this.mScreenData.add(new AppInfo.SideMenu(AbstractFragment.SIGN_OUT_SCREEN, mContext.getString(R.string.sign_out), "ti-unlock"));
        } else {
            mUserNameLabel.setText(mContext.getString(R.string.sign_in));
            mUserInfoButton.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mUserAvatarImage.setImageBitmap(null);
            mUserAvatarImage.setImageResource(R.drawable.no_avatar);
        }

        this.mSignInButton.setTextColor(settings.getMenuItemTitleColor());
        this.mMainLayout.setBackgroundColor(this.mSettings.getMenuBackgroundColor());
        if (this.mAdapter == null) {
            this.mAdapter = new LeftMenuAdapter(mContext, R.layout.nav_content_item, mScreenData);
            this.mListView.setAdapter(mAdapter);
            this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (onItemClickListener != null) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Bundle params = new Bundle();
                                params.putSerializable(Key.RequestObject, mScreenData.get(position));
                                onItemClickListener.onClick(position, params);
                            }
                        }, 200);
                    }
                }
            });
        } else {
            this.mAdapter.notifyDataSetChanged();
        }
    }
}