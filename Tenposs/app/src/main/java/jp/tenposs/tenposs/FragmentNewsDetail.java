package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.NewsInfo;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.view.AspectRatioImageView;

/**
 * Created by ambient on 8/24/16.
 */
public class FragmentNewsDetail
        extends
        AbstractFragment
        implements
        CommonAdapter.CommonDataSource,
        OnCommonItemClickListener {


    AspectRatioImageView newsImage;
    TextView newsCategoryLabel;
    TextView newsTitleLabel;
    TextView newsDateLabel;
    TextView newsDescriptionLabel;

    NewsInfo.News screenData;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = "";
        toolbarSettings.toolbarLeftIcon = "flaticon-back";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void reloadScreenData() {
        if (this.screenDataStatus != ScreenDataStatus.ScreenDataStatusUnload) {
            return;
        }
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
    }

    @Override
    protected void previewScreenData() {

        Picasso ps = Picasso.with(getContext());
        ps.load(screenData.getImageUrl())
                .resize(fullImageSize, fullImageSize)
                .centerCrop()
                .into(this.newsImage);

        this.newsTitleLabel.setText(screenData.title);
        this.newsCategoryLabel.setText(screenData.category);
        this.newsDateLabel.setText(this.screenData.getCreatedDate());
        this.newsDescriptionLabel.setText(this.screenData.description);

        toolbarSettings.toolbarTitle = screenData.title;

        setRefreshing(false);
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news_detail, null);
        this.newsImage = (AspectRatioImageView) mRoot.findViewById(R.id.news_image);
        this.newsCategoryLabel = (TextView) mRoot.findViewById(R.id.news_category_label);
        this.newsTitleLabel = (TextView) mRoot.findViewById(R.id.news_title_label);
        this.newsDateLabel = (TextView) mRoot.findViewById(R.id.news_date_label);
        this.newsDescriptionLabel = (TextView) mRoot.findViewById(R.id.news_description_label);


        return mRoot;
    }

    @Override
    protected void customResume() {
        /*if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
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
            this.screenData = (NewsInfo.News) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    public int getItemCount() {
        return this.screenDataItems.size();
    }

    @Override
    public RecyclerItemWrapper getItemData(int position) {
        return this.screenDataItems.get(position);
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {

    }
}

