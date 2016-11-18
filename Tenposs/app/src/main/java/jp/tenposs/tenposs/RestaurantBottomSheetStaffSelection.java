package jp.tenposs.tenposs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.BSSelectionListener;
import jp.tenposs.view.CircleImageView;

/**
 * Created by ambient on 10/18/16.
 */

public class RestaurantBottomSheetStaffSelection extends BottomSheetDialogFragment {
    private static final String SCREEN_DATA = "SCREEN_DATA";
    ListView mListView;
    Button mCancelButton;

    StaffInfo.Response mScreenData;
    BSSelectionListener listener;

    static RestaurantBottomSheetStaffSelection newInstance(Serializable staffs, BSSelectionListener listener) {
        RestaurantBottomSheetStaffSelection f = new RestaurantBottomSheetStaffSelection();
        Bundle args = new Bundle();
        args.putSerializable(SCREEN_DATA, staffs);
        f.setArguments(args);
        f.listener = listener;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenData = (StaffInfo.Response) getArguments().getSerializable(SCREEN_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.common_bottonsheet_staff_selection, container, false);

        this.mListView = (ListView) root.findViewById(R.id.list_view);
        this.mCancelButton = (Button) root.findViewById(R.id.cancel_button);

        this.mListView.setAdapter(new CustomListAdapter(this.getContext(), mScreenData.data.staffs));
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RestaurantBottomSheetStaffSelection.this.dismiss();
                if (RestaurantBottomSheetStaffSelection.this.listener != null) {
                    RestaurantBottomSheetStaffSelection.this.listener.onItemSelect(position, RestaurantBottomSheetStaffSelection.this.mScreenData.data.staffs.get(position));
                }
            }
        });

        this.mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantBottomSheetStaffSelection.this.dismiss();
                if (RestaurantBottomSheetStaffSelection.this.listener != null) {
                    RestaurantBottomSheetStaffSelection.this.listener.onCancel();
                }
            }
        });
        return root;
    }

    public class CustomListAdapter extends BaseAdapter {
        private ArrayList<StaffInfo.Staff> mData;
        private LayoutInflater layoutInflater;
        private Context mContext;

        public CustomListAdapter(Context aContext, ArrayList<StaffInfo.Staff> listData) {
            this.mContext = aContext;
            this.mData = listData;
            layoutInflater = LayoutInflater.from(aContext);
        }

        @Override
        public int getCount() {
            return mData.size();

        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);

        }

        @Override

        public long getItemId(int position) {

            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.restaurant_item_list_staff, null);
                holder = new ViewHolder();
                holder.itemImage = (CircleImageView) convertView.findViewById(R.id.item_image);
                holder.itemDescription = (TextView) convertView.findViewById(R.id.item_description_label);
                holder.itemBrand = (TextView) convertView.findViewById(R.id.item_brand_label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StaffInfo.Staff staff = this.mData.get(position);

            Picasso ps = Picasso.with(mContext);
            ps.load(staff.getImageUrl())
                    .placeholder(R.drawable.drop)
                    .into(holder.itemImage);

            holder.itemDescription.setText(staff.name);
            holder.itemBrand.setText(staff.introduction);

            return convertView;
        }

        class ViewHolder {
            CircleImageView itemImage;
            TextView itemDescription;
            TextView itemBrand;
        }
    }
}

