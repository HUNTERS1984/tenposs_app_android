package jp.tenposs.view;

import android.content.Context;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import jp.tenposs.tenposs.R;

/**
 * Created by ambient on 7/28/16.
 */
public class LeftMenuView extends FrameLayout {

    public class MenuItem {

        public MenuItem(long id, String title, String pathName) {
            this.id = id;
            this.title = title;
            this.pathName = pathName;
        }

        public long id;
        public String title;
        public String pathName;
    }

    Context mContext;

    public LeftMenuView(Context context) {
        super(context);
        this.mContext = context;
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

    OnMenuItemClickListener mListener;

    ImageView avatar;
    TextView userName;
    TextView userInfo;

    FrameLayout headerLogin;
    FrameLayout headerUser;
    Button headerLoginButton;

    RecyclerView menuList;

    void initView(Context context) {
//        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.addView(inflater.inflate(R.layout.left_menu_header, null));
        View.inflate(context, R.layout.nav_content_main, this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void performLeftMenuClick(int position) {
        if (position != -1 && this.menuList != null) {
            if (this.menuList.findViewHolderForAdapterPosition(position).itemView != null) {
                RecyclerView.ViewHolder holder = this.menuList.findViewHolderForAdapterPosition(position);
                this.menuList.findViewHolderForAdapterPosition(position).itemView.performClick();
            }
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuHeaderClick();

        void onMenuItemClick(int index, MenuItem item);
    }
}