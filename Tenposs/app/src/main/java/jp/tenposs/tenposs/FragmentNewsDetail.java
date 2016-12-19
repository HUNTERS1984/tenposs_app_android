package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.adapter.RecyclerDataSource;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.AppData;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.AspectRatioImageView;

/**
 * Created by ambient on 8/24/16.
 */
public class FragmentNewsDetail
        extends
        AbstractFragment
        implements
        RecyclerDataSource,
        OnCommonItemClickListener {


    AspectRatioImageView mNewsImage;
    TextView mNewsCategoryLabel;
    TextView mNewsTitleLabel;
    TextView mNewsDateLabel;
    TextView mNewsDescriptionLabel;

    NewsInfo.News mScreenData;

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = "";
        if (AppData.sharedInstance().getTemplate() == AppData.TemplateId.RestaurantTemplate) {
            mToolbarSettings.toolbarLeftIcon = "flaticon-close";
        } else {
            mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        }

        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {
        if (this.mScreenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        Picasso ps = Picasso.with(getContext());
        ps.load(mScreenData.getImageUrl())
                .into(this.mNewsImage);

        this.mNewsTitleLabel.setText(mScreenData.getTitle());
        this.mNewsCategoryLabel.setText(mScreenData.getCategory());
        this.mNewsDateLabel.setText(Utils.formatDateTime(this.mScreenData.getLastModifyDate(), "yyyy-MM-dd", "yyyy.MM.dd"));
        this.mNewsDescriptionLabel.setText(this.mScreenData.getDescription());

        mToolbarSettings.toolbarTitle = mScreenData.getTitle();

        setRefreshing(false);
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news_detail, null);
        this.mNewsImage = (AspectRatioImageView) mRoot.findViewById(R.id.news_image);
        this.mNewsCategoryLabel = (TextView) mRoot.findViewById(R.id.news_category_label);
        this.mNewsTitleLabel = (TextView) mRoot.findViewById(R.id.news_title_label);
        this.mNewsDateLabel = (TextView) mRoot.findViewById(R.id.news_date_label);
        this.mNewsDescriptionLabel = (TextView) mRoot.findViewById(R.id.news_description_label);


        return mRoot;
    }

    @Override
    protected void customResume() {
        /*if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        } else if (this.mScreenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting
        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }*/
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (NewsInfo.News) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return true;
    }

    @Override
    public int getItemCount() {
        return this.mScreenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return this.mScreenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {

    }
}

