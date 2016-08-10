package jp.tenposs.tenposs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.tenposs.datamodel.AppSettings;
import jp.tenposs.utils.ThemifyIcon;

/**
 * Created by ambient on 7/27/16.
 */
public class FragmentNews extends AbstractFragment {

    ImageButton previousButton;
    TextView titleLabel;
    ImageButton nextButton;
    RecyclerView recyclerView;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "News";
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

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.fragment_news, null);

        previousButton = (ImageButton) mRoot.findViewById(R.id.previous_button);
        titleLabel = (TextView) mRoot.findViewById(R.id.title_label);
        nextButton = (ImageButton) mRoot.findViewById(R.id.next_button);
        recyclerView = (RecyclerView) mRoot.findViewById(R.id.recycler_view);

        previousButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-left",
                40,
                Color.argb(0, 0, 0, 0),
                toolbarSettings.settings.getColor()
        ));

        nextButton.setImageBitmap(ThemifyIcon.fromThemifyIcon(getContext().getAssets(),
                "ti-angle-right",
                40,
                Color.argb(0, 0, 0, 0),
                toolbarSettings.settings.getColor()
        ));
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }

}
