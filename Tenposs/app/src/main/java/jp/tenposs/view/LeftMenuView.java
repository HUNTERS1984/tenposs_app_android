package jp.tenposs.view;

import android.content.Context;
import android.graphics.Color;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.SignInInfo;
import jp.tenposs.datamodel.UserInfo;
import jp.tenposs.tenposs.AbstractFragment;
import jp.tenposs.tenposs.R;
import jp.tenposs.utils.FontIcon;
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
                if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                    row = mInflater.inflate(R.layout.nav_content_item, null);
                } else {
                    row = mInflater.inflate(R.layout.nav_content_item, null);
                }
            } else {
                row = convertView;
            }
            AppInfo.SideMenu item = getItem(position);
            ImageView menuIcon = (ImageView) row.findViewById(R.id.item_image);
            TextView menuTitle = (TextView) row.findViewById(R.id.item_label);

            menuTitle.setText(item.name);

            if (menuTitle != null) {
                try {
                    Utils.setTextAppearanceMenu(getContext(), menuTitle, mSettings.getMenuItemTitleFontSize());
                    //Typeface type = Utils.getTypeFaceForFont(mContext, mSettings.getMenuItemTitleFont());
                    //menuTitle.setTypeface(type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                menuTitle.setTextColor(Color.WHITE);
            } else {
                menuTitle.setTextColor(mSettings.getMenuItemTitleColor());
            }

            if (item.fontType != 0) {
                menuIcon.setImageBitmap(FontIcon.imageForFontIdentifier(getContext().getAssets(),
                        item.icon,
                        Utils.NavIconSize,
                        Color.argb(0, 0, 0, 0),
                        Color.argb(200, 214, 218, 222),
                        item.fontType
                ));
            } else {
                if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
                    menuIcon.setImageDrawable(MenuIcon.getInstance().getIconDrawableWithId(mContext, item.id));
                } else {
                    menuIcon.setImageBitmap(MenuIcon.getInstance().getIconBitmapWithId(item.id));
                }
            }

            return row;
        }
    }

    Context mContext;

    LinearLayout mMainLayout;
    CircleImageView mUserAvatarImage;
    TextView mUserNameLabel;
    TextView mUserEmailLabel;

    Button mUserInfoButton;
    Button mSignInButton;

    ListView mListView;
    LeftMenuAdapter mAdapter;

    ArrayList<AppInfo.SideMenu> mScreenData;
    AppInfo.AppSetting mSettings;
    UserInfo.User mUserProfile;

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
        this.mUserEmailLabel = (TextView) rootView.findViewById(R.id.email_label);
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
                    params.putInt(AbstractFragment.SCREEN_DATA, AbstractFragment.MY_PAGE_SCREEN);
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

    public void updateMenu(AppInfo.AppSetting settings, ArrayList<AppInfo.SideMenu> sideMenus, UserInfo.User userProfile) {
        this.mSettings = settings;
        this.mScreenData = (ArrayList<AppInfo.SideMenu>) sideMenus.clone();
        this.mUserProfile = userProfile;

        if (this.mUserProfile != null) {
            mUserNameLabel.setText(userProfile.getName());

            if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate && userProfile.getEmail().length() > 0) {
                this.mUserEmailLabel.setVisibility(VISIBLE);
                this.mUserEmailLabel.setText(userProfile.getEmail());
            } else {
                this.mUserEmailLabel.setVisibility(GONE);
            }
            reloadUserInfo(this.mUserProfile);

            this.mUserInfoButton.setVisibility(View.VISIBLE);
            this.mSignInButton.setVisibility(View.GONE);

//            addSignOutMenu();
        } else {
            this.mUserNameLabel.setText(mContext.getString(R.string.sign_in));
            this.mUserInfoButton.setVisibility(View.GONE);
            this.mUserEmailLabel.setVisibility(GONE);
            this.mSignInButton.setVisibility(View.VISIBLE);
            this.mUserAvatarImage.setImageBitmap(null);
            this.mUserAvatarImage.setImageResource(R.drawable.no_avatar_gray);
            removeSignedInItems();
//            removeSignOutMenu();
        }

        this.mSignInButton.setTextColor(settings.getMenuItemTitleColor());
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mMainLayout.setBackgroundResource(R.drawable.restaurant_menu_bg);
        } else {
            this.mMainLayout.setBackgroundColor(this.mSettings.getMenuBackgroundColor());
        }
//        if (this.mAdapter == null) {
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
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            this.mUserNameLabel.setTextColor(Color.WHITE);
            this.mSignInButton.setTextColor(Color.WHITE);
        } else {
            this.mUserNameLabel.setTextColor(mSettings.getMenuItemTitleColor());
            this.mSignInButton.setTextColor(mSettings.getMenuItemTitleColor());
        }
//        } else {
//            this.mAdapter.setData(this.mScreenData);
//            this.mAdapter.notifyDataSetChanged();
    }

    private void removeSignedInItems() {
        //for (AppInfo.SideMenu menu : this.mScreenData) {
        int index = 0;
        while (index < this.mScreenData.size()) {
            AppInfo.SideMenu menu = this.mScreenData.get(index);
            if (menu.id == AbstractFragment.SETTING_SCREEN || menu.id == AbstractFragment.CHAT_SCREEN) {
                this.mScreenData.remove(menu);
            } else {
                index++;
            }
        }
    }


//    private void addSignOutMenu() {
//        AppInfo.SideMenu signOutMenu = new AppInfo.SideMenu(AbstractFragment.SIGN_OUT_SCREEN, mContext.getString(R.string.sign_out), "ti-unlock");
//        signOutMenu.fontType = FontIcon.THEMIFY;
//        this.mScreenData.add(signOutMenu);
//    }

//    private void removeSignOutMenu() {
//        for (AppInfo.SideMenu menu : this.mScreenData) {
//            if (menu.id == AbstractFragment.SIGN_OUT_SCREEN) {
//                this.mScreenData.remove(menu);
//                return;
//            }
//        }
//    }

    public void reloadUserInfo(UserInfo.User userProfile) {
        this.mUserProfile = userProfile;
        Picasso ps = Picasso.with(mContext);
        String url = this.mUserProfile.profile.getImageUrl().toLowerCase(Locale.US);
        if (url.contains("http://") == true || url.contains("https://") == true) {
            ps.load(this.mUserProfile.profile.getImageUrl())
                    .placeholder(R.drawable.no_avatar_gray)
                    .into(mUserAvatarImage);
        } else {
            File f = new File(this.mUserProfile.profile.getImageUrl());
            ps.load(f)
                    .placeholder(R.drawable.no_avatar_gray)
                    .into(mUserAvatarImage);
        }
    }
}