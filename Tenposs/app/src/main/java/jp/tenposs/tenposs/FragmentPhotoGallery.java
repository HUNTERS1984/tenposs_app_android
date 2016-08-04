package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.tenposs.datamodel.AppSettings;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentPhotoGallery  extends  AbstractFragment{
    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings = new ToolbarSettings();
        toolbarSettings.toolbarTitle = "Photo Gallery";
        toolbarSettings.toolbarIcon = "ti-angle-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;

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
        View mRoot = inflater.inflate(R.layout.fragment_reserve, null);
        return mRoot;
    }

    @Override
    void loadSavedInstanceState(@Nullable Bundle savedInstanceState) {

    }
}
