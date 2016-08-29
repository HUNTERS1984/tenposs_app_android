package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ambient on 8/17/16.
 */
public class FragmentEditProfile extends AbstractFragment {
    ImageView userAvatar;
    EditText idEdit;
    EditText userNameEdit;
    EditText emailEdit;

    TextView unknownText1;
    ImageView unknownImage1;
    Button unknownButton1;

    TextView unknownText2;
    ImageView unknownImage2;
    Button unknownButton2;

    ImageView facebookIcon;
    Button facebookButton;

    ImageView twitterIcon;
    Button twitterButton;

    ImageView instargramIcon;
    Button instargramButton;

    @Override
    protected void customClose() {

    }

    @Override
    protected void customToolbarInit() {
        toolbarSettings.toolbarTitle = getString(R.string.edit_profile);
        toolbarSettings.toolbarIcon = "ti-arrow-left";
        toolbarSettings.toolbarType = ToolbarSettings.LEFT_MENU_BUTTON;
    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {

    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            //this.screenData = (TopInfo.Response.ResponseData) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void setRefreshing(boolean refreshing) {

    }
}
