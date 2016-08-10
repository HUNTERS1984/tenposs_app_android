package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import jp.tenposs.adapter.CommonAdapter;
import jp.tenposs.communicator.MenuInfoCommunicator;
import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.datamodel.AppInfo;
import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.listener.OnCommonItemClickListener;
import jp.tenposs.utils.ThemifyIcon;



/**
 * Created by ambient on 7/27/16.
 */
public class FragmentMenu extends AbstractFragment implements View.OnClickListener, CommonAdapter.CommonDataSource, OnCommonItemClickListener {

    ImageButton previousButton;
    TextView titleLabel;
    ImageButton nextButton;
    RecyclerView recyclerView;
    CommonAdapter recyclerAdapter;

    ArrayList<AppInfo.Response.ResponseData.Menu> screenData;
    int menuIndex;

    /**
     * Fragment Override
     */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SCREEN_DATA, this.screenData);
        outState.putInt(SCREEN_DATA_STATUS, this.screenDataStatus.ordinal());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        }else{
            Bundle argument = getArguments();
            if( argument != null && argument.containsKey(SCREEN_DATA)){
                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) argument.getSerializable(SCREEN_DATA);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            startup();

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //Do nothing

        } else {
            previewScreenData();
        }
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SCREEN_DATA)) {
                this.screenData = (ArrayList<AppInfo.Response.ResponseData.Menu>) savedInstanceState.getSerializable(SCREEN_DATA);
            }
        }
    }

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Menu";
        toolbarSettings.toolbarIcon = "ti-menu";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;

        toolbarSettings.settings = new AppSettings.Settings();
        toolbarSettings.settings.fontColor = "#00CECB";

        toolbarSettings.titleSettings = new AppSettings.Settings();
        toolbarSettings.titleSettings.fontColor = "#000000";
        toolbarSettings.titleSettings.fontSize = 20;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
//        this.swipeRefreshLayout.setRefreshing(false);
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        AppInfo.Response.ResponseData.Menu currentMenu = this.screenData.get(menuIndex);
        this.titleLabel.setText(currentMenu.name);

        updateNavigation();

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 6);//);
        this.recyclerAdapter = new CommonAdapter(getActivity(), this, this);
        manager.setSpanSizeLookup(new CommonAdapter.GridSpanSizeLookup(recyclerAdapter));
        this.recyclerView.setLayoutManager(manager);
        this.recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_menu, null);

        previousButton = (ImageButton) mRoot.findViewById(R.id.previous_button);
        titleLabel = (TextView) mRoot.findViewById(R.id.title_label);
        nextButton = (ImageButton) mRoot.findViewById(R.id.next_button);
        recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);

        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        return mRoot;
    }

    void startup() {
        if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusUnload) {
            //load needed data
            this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoading;
//            this.swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    FragmentHome.this.swipeRefreshLayout.setRefreshing(true);
//                    loadAppInfo();
            menuIndex = 0;
            loadMenuData();
//                }
//            });

        } else if (this.screenDataStatus == ScreenDataStatus.ScreenDataStatusLoading) {
            //just waiting

        } else {
            //reload screen, this case application return from background or from other activity
            previewScreenData();
        }
    }

    void loadMenuData() {
        AppInfo.Response.ResponseData.Menu menu = screenData.get(menuIndex);
        Bundle params = new Bundle();
        MenuInfoCommunicator communicator = new MenuInfoCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                previewScreenData();
            }
        });
        communicator.execute(params);
    }

    boolean hasPrevious() {
        return (menuIndex > 0);
    }

    boolean hasNext() {
        return (menuIndex < (this.screenData.size() - 1));
    }

    void previousMenu() {
        try {
            if (menuIndex > 0) {
                menuIndex--;
            }
        } catch (Exception ex) {

        }
        loadMenuData();
    }

    void nextMenu() {
        try {
            if (menuIndex < this.screenData.size() - 1) {
                menuIndex++;
            }
        } catch (Exception ex) {

        }
        loadMenuData();
    }

    void updateNavigation() {
        int previousButtonColor = Color.rgb(128, 128, 128);
        if (hasPrevious() == true) {
            previousButtonColor = Color.parseColor("#00CECB");
        }

        int nextButtonColor = Color.rgb(128, 128, 128);
        if (hasNext() == true) {
            nextButtonColor = Color.parseColor("#00CECB");
        }

        previousButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-left",
                40,
                Color.argb(0, 0, 0, 0),
                previousButtonColor
        ));

        nextButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-right",
                40,
                Color.argb(0, 0, 0, 0),
                nextButtonColor
        ));
    }

    @Override
    public void onClick(View v) {
        if (v == previousButton) {
            previousMenu();

        } else if (v == nextButton) {
            nextMenu();
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public Object getItemData(int position) {
        return null;
    }

    @Override
    public void onCommonItemClick(int position, Bundle extraData) {

    }
}
