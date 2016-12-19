package jp.tenposs.tenposs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

import jp.tenposs.datamodel.StaffInfo;
import jp.tenposs.listener.BSSelectionListener;

/**
 * Created by ambient on 10/18/16.
 */

public class CommonBottomSheetStaffSelection extends BottomSheetDialogFragment {
    private static final String SCREEN_DATA = "SCREEN_DATA";
    ListView mListView;
    Button mCancelButton;

    StaffInfo.Response mScreenData;
    BSSelectionListener listener;

    static CommonBottomSheetStaffSelection newInstance(Serializable staffs, BSSelectionListener listener) {
        CommonBottomSheetStaffSelection f = new CommonBottomSheetStaffSelection();
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

        ArrayList<String> values = new ArrayList<>();
        for (StaffInfo.Staff staff : this.mScreenData.data.staffs) {
            values.add(staff.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, values);
        this.mListView.setAdapter(adapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonBottomSheetStaffSelection.this.dismiss();
                if (CommonBottomSheetStaffSelection.this.listener != null) {
                    CommonBottomSheetStaffSelection.this.listener.onItemSelect(position, CommonBottomSheetStaffSelection.this.mScreenData.data.staffs.get(position));
                }
            }
        });

        this.mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonBottomSheetStaffSelection.this.dismiss();
                if (CommonBottomSheetStaffSelection.this.listener != null) {
                    CommonBottomSheetStaffSelection.this.listener.onCancel();
                }
            }
        });

//        final BottomSheetBehavior behavior = BottomSheetBehavior.from(root);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//            }
//        });

        return root;
    }
}

