package jp.tenposs.tenposs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import jp.tenposs.communicator.TenpossCommunicator;
import jp.tenposs.communicator.UseCouponCommunicator;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Utils;
import jp.tenposs.view.AspectRatioImageView;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by ambient on 8/4/16.
 */
public class FragmentCouponDetail extends AbstractFragment {

    AspectRatioImageView mCouponImage;
    TextView mCouponIdLabel;
    TextView mCouponTypeLabel;
    TextView mCouponNameLabel;
    TextView mValidityLabel;
    TextView mHashTagLabel;
    Button mCopyHashTagButton;
    TextView mCouponDescriptionLabel;

    LinearLayout mTakeAdvantageOfCouponLayout;
    Button mTakeAdvantageOfCouponButton;
    LinearLayout mCouponCannotUseLayout;

    CouponInfo.Coupon mScreenData;


//    private FragmentCouponDetail() {
//
//    }
//
//    public static FragmentCouponDetail newInstance(Serializable extras) {
//        FragmentCouponDetail fragment = new FragmentCouponDetail();
//        Bundle b = new Bundle();
//        b.putSerializable(AbstractFragment.SCREEN_DATA, extras);
//        fragment.setArguments(b);
//        return fragment;
//    }

    @Override
    protected boolean customClose() {
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = "";
        mToolbarSettings.toolbarLeftIcon = "flaticon-back";
        mToolbarSettings.toolbarType = ToolbarSettings.LEFT_BACK_BUTTON;
    }

    @Override
    protected void clearScreenData() {

    }

    @Override
    protected void reloadScreenData() {

    }

    @Override
    protected void previewScreenData() {
        this.mScreenDataStatus = ScreenDataStatus.ScreenDataStatusLoaded;
        mToolbarSettings.toolbarTitle = mScreenData.getTitle();

        Picasso ps = Picasso.with(getContext());
        ps.load(mScreenData.getImageUrl()).into(mCouponImage);

        this.mCouponIdLabel.setText("ID" + Integer.toString(mScreenData.id));
        this.mCouponTypeLabel.setText(mScreenData.getCategory());
        this.mCouponNameLabel.setText(mScreenData.getTitle());
        this.mValidityLabel.setText(Utils.formatJapanDateTime(mScreenData.end_date, "yyyy-MM-dd", "yyyy年M月d日まで"));
        this.mCouponDescriptionLabel.setText(mScreenData.getDescription());
        this.mHashTagLabel.setText(this.mScreenData.getHashTag());

        Date current = new Date();
        Date endDate = this.mScreenData.getEndDate();

        boolean before = false;

        if (endDate != null) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(current);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);

            int currentDay = currentCalendar.get(Calendar.DATE);
            int currentMoth = currentCalendar.get(Calendar.MONTH);
            int currentYear = currentCalendar.get(Calendar.YEAR);

            int endDay = endCalendar.get(Calendar.DATE);
            int endMoth = endCalendar.get(Calendar.MONTH);
            int endYear = endCalendar.get(Calendar.YEAR);

            if (endYear > currentYear) {
                before = true;
            } else if (endYear == currentYear) {
                if (endMoth > currentMoth) {
                    before = true;
                } else if (endMoth == currentMoth) {
                    if (endDay > currentDay) {
                        before = true;
                    } else if (endDay > currentDay) {
                        before = true;
                    } else {
                        before = false;
                    }
                } else {
                    before = false;
                }
            } else {
                before = false;
            }
        }

        if (isSignedIn() == true && before) {
            this.mTakeAdvantageOfCouponLayout.setVisibility(View.VISIBLE);
            this.mCouponCannotUseLayout.setVisibility(View.GONE);
        } else {
            this.mTakeAdvantageOfCouponLayout.setVisibility(View.GONE);
            this.mCouponCannotUseLayout.setVisibility(View.VISIBLE);
        }

        updateToolbar();
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon_detail, null);
        this.mCouponImage = (AspectRatioImageView) root.findViewById(R.id.coupon_image);
        this.mCouponIdLabel = (TextView) root.findViewById(R.id.coupon_id_label);
        this.mCouponTypeLabel = (TextView) root.findViewById(R.id.coupon_type_label);
        this.mCouponNameLabel = (TextView) root.findViewById(R.id.coupon_name_label);
        this.mValidityLabel = (TextView) root.findViewById(R.id.validity_label);
        this.mHashTagLabel = (TextView) root.findViewById(R.id.hash_tag_label);
        this.mCopyHashTagButton = (Button) root.findViewById(R.id.copy_hash_tag_button);
        this.mCouponDescriptionLabel = (TextView) root.findViewById(R.id.coupon_description_label);
        this.mTakeAdvantageOfCouponLayout = (LinearLayout) root.findViewById(R.id.take_advantage_of_coupon_layout);
        this.mTakeAdvantageOfCouponButton = (Button) root.findViewById(R.id.take_advantage_of_coupon_button);
        this.mCouponCannotUseLayout = (LinearLayout) root.findViewById(R.id.coupon_cannot_use_layout);

        this.mCopyHashTagButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", mScreenData.getHashTag());
                clipboard.setPrimaryClip(clip);

                PopupHashTagCopied popupHashTagCopied = new PopupHashTagCopied(getContext());
                popupHashTagCopied.show();
            }
        });

        this.mTakeAdvantageOfCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUseCoupon popupUseCoupon = new PopupUseCoupon(getContext());
                popupUseCoupon.setData(mScreenData);
                popupUseCoupon.show();
            }
        });
        return root;
    }

    @Override
    protected void customResume() {
        previewScreenData();
    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SCREEN_DATA)) {
            this.mScreenData = (CouponInfo.Coupon) savedInstanceState.getSerializable(SCREEN_DATA);
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

    void requestUseCoupon() {
        Utils.showProgress(getContext(), getString(R.string.msg_requesting));
        Bundle params = new Bundle();
        //TODO:params.
        new UseCouponCommunicator(new TenpossCommunicator.TenpossCommunicatorListener() {
            @Override
            public void completed(TenpossCommunicator request, Bundle responseParams) {
                //Utils.hideProgress();
//                if (isAdded() == false) {
//                    return;
//                }
//                int result = responseParams.getInt(Key.ResponseResult);
//                if (result == TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal()) {
//                    int resultApi = responseParams.getInt(Key.ResponseResultApi);
//                    if (resultApi == CommonResponse.ResultSuccess) {
//                        FragmentCoupon.this.mScreenData = (CouponInfo.Response) responseParams.getSerializable(Key.ResponseObject);
//                        previewScreenData();
//                    } else if (resultApi == CommonResponse.ResultErrorTokenExpire) {
//                        refreshToken(new TenpossCallback() {
//                            @Override
//                            public void onSuccess(Bundle params) {
//                                loadCouponsInfo();
//                            }
//
//                            @Override
//                            public void onFailed(Bundle params) {
//                                //Logout, then do something
//                                mActivityListener.logoutBecauseExpired();
//                            }
//                        });
//                    } else {
//                        String strMessage = responseParams.getString(Key.ResponseMessage);
//                        errorWithMessage(responseParams, strMessage, null);
//                    }
//                } else {
//                    String strMessage = responseParams.getString(Key.ResponseMessage);
//                    errorWithMessage(responseParams, strMessage, null);
//                }
            }
        }).execute(params);
    }

    void couponAccepted() {
        //PopupCouponAccepted popupCouponAccepted = new PopupCouponAccepted(this.mContext);
        //popupCouponAccepted.show();
    }

    void couponRejected() {
        //PopupCouponRejected popupCouponRejected = new PopupCouponRejected(this.mContext);
        //popupCouponRejected.show();
    }
}
