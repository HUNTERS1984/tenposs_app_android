package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.tenposs.adapter.RecyclerItemType;
import jp.tenposs.adapter.RecyclerItemWrapper;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.AspectRatioImageView;

/**
 * Created by ambient on 7/27/16.
 */
public class FragmentStaffDetail extends AbstractFragment implements View.OnClickListener {

    AspectRatioImageView staffImage;
    TextView staffCategoryLabel;
    TextView staffTitleLabel;
    Button unknownStaffButon;
    Button staffDescriptionButton;
    Button staffProfileButton;
    TextView staffDescriptionLabel;
    Button moreButton;

    boolean showDescription = true;

    StaffInfo.Staff screenData;

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

    }

    @Override
    protected void previewScreenData() {
        Bundle extras;
        screenDataItems = new ArrayList<>();

        Picasso ps = Picasso.with(getContext());
        ps.load(screenData.getImageUrl())
                .resize(320, 320)
                .centerCrop()
                .into(this.staffImage);

        this.staffTitleLabel.setText(screenData.name);
        //this.staffCategoryLabel.setText(screenData.category);
        //this.staffDescriptionLabel.setText(this.screenData.description);


        showDescriptionOrProfile();

        //title
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductTitle, spanCount, extras));

        //purchase
        extras = new Bundle();
        extras.putInt(RecyclerItemWrapper.ITEM_SCREEN_ID, AbstractFragment.PURCHASE_SCREEN);
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        extras.putString(RecyclerItemWrapper.ITEM_TITLE, getString(R.string.purchase));
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeFooter, spanCount, extras));

        //Description
        extras = new Bundle();
        extras.putSerializable(RecyclerItemWrapper.ITEM_OBJECT, screenData);
        screenDataItems.add(new RecyclerItemWrapper(RecyclerItemType.RecyclerItemTypeProductDescription, spanCount, extras));
        this.screenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;

        toolbarSettings.toolbarTitle = screenData.name;
        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_staff_detail, null);
        this.staffImage = (AspectRatioImageView) root.findViewById(R.id.staff_image);
        this.staffCategoryLabel = (TextView) root.findViewById(R.id.staff_category_label);
        this.staffTitleLabel = (TextView) root.findViewById(R.id.staff_title_label);
        this.unknownStaffButon = (Button) root.findViewById(R.id.unknown_staff_button);
        this.staffDescriptionButton = (Button) root.findViewById(R.id.staff_description_button);
        this.staffProfileButton = (Button) root.findViewById(R.id.staff_profile_button);
        this.staffDescriptionLabel = (TextView) root.findViewById(R.id.staff_description_label);
        this.moreButton = (Button) root.findViewById(R.id.more_button);

        this.staffDescriptionButton.setOnClickListener(this);
        this.staffProfileButton.setOnClickListener(this);
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.screenData = (StaffInfo.Staff) savedInstanceState.getSerializable(SCREEN_DATA);
        }
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    void showDescriptionOrProfile() {
        if (showDescription == true) {
            staffDescriptionButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            staffDescriptionButton.setBackgroundResource(R.drawable.bg_tab_button);

            staffProfileButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            staffProfileButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);
        } else {
            staffProfileButton.setTextColor(Utils.getColorInt(getContext(), R.color.category_text_color));
            staffProfileButton.setBackgroundResource(R.drawable.bg_tab_button);

            staffDescriptionButton.setTextColor(Utils.getColorInt(getContext(), R.color.description_text_color));
            staffDescriptionButton.setBackgroundResource(R.drawable.bg_tab_button_inactive);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == staffDescriptionButton) {
            showDescription = true;
        } else if (v == staffProfileButton) {
            showDescription = false;
        }
        showDescriptionOrProfile();
    }
}
