package jp.tenposs.tenposs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import jp.tenposs.datamodel.Key;
import jp.tenposs.datamodel.ScreenDataStatus;
import jp.tenposs.utils.Crop;
import jp.tenposs.utils.CropUtil;
import jp.tenposs.view.CropImageView;
import jp.tenposs.view.HighlightView;
import jp.tenposs.view.RotateBitmap;

/**
 * Created by ambient on 9/16/16.
 */

public class FragmentSelectAvatar extends AbstractFragment {

    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;

    private final Handler mHandler = new Handler();

    private int mAspectX;
    private int mAspectY;

    // Output image
    private int mMaxX;
    private int mMaxY;
    private int mExifRotation;
    private boolean mSaveAsPng = true;

    private Uri mSourceUri;
    private Uri mSaveUri;

    private boolean mIsSaving;

    private int mSampleSize;
    private RotateBitmap mRotateBitmap;
    CropImageView mCropImageView;
    private HighlightView mCropView;


    @Override
    protected boolean customClose() {
        Fragment target = getTargetFragment();
        if (mSaveUri != null) {
            target.onActivityResult(CROP_IMAGE_INTENT, Activity.RESULT_OK, new Intent().putExtra(Key.UpdateProfileAvatar, mSaveUri));
        } else {
            target.onActivityResult(CROP_IMAGE_INTENT, Activity.RESULT_CANCELED, null);
        }
        return false;
    }

    @Override
    protected void customToolbarInit() {
        mToolbarSettings.toolbarTitle = getString(R.string.title_select_avatar_image);
        mToolbarSettings.toolbarLeftIcon = "flaticon-close";
        mToolbarSettings.toolbarRightIcon = "flaticon-check";
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
    }

    @Override
    protected View onCustomCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_select_avatar, null);
        this.mCropImageView = (CropImageView) root.findViewById(R.id.crop_image_view);
        return root;
    }

    @Override
    protected void customResume() {

    }

    @Override
    void loadSavedInstanceState(@NonNull Bundle savedInstanceState) {
        this.mSourceUri = savedInstanceState.getParcelable(SCREEN_DATA);
        this.mSaveUri = savedInstanceState.getParcelable(Key.UpdateProfileAvatar);
    }

    @Override
    void customSaveInstanceState(Bundle outState) {

    }

    @Override
    void setRefreshing(boolean refreshing) {

    }

    @Override
    boolean canCloseByBackpressed() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mRightToolbarButton.setVisibility(View.INVISIBLE);
        this.mLeftToolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveUri = null;
                close();
            }
        });
        this.mRightToolbarButton.setOnClickListener(
                new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        onSaveClicked();
                    }
                }
        );

        loadInput(null, mSourceUri);
        startCrop();
    }

    private void loadInput(Bundle extras, Uri data) {
        if (extras != null) {
            mAspectX = extras.getInt(Crop.Extra.ASPECT_X);
            mAspectY = extras.getInt(Crop.Extra.ASPECT_Y);
            mMaxX = extras.getInt(Crop.Extra.MAX_X);
            mMaxY = extras.getInt(Crop.Extra.MAX_Y);
            mSaveAsPng = extras.getBoolean(Crop.Extra.AS_PNG, false);
            mSaveUri = extras.getParcelable(Key.UpdateProfileAvatar);
        }

        mSourceUri = data;
        if (mSourceUri != null) {
            mExifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this.getContext(), getActivity().getContentResolver(), mSourceUri));

            InputStream is = null;
            try {
                mSampleSize = calculateBitmapSampleSize(mSourceUri);
                is = getActivity().getContentResolver().openInputStream(mSourceUri);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = mSampleSize;
                mRotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), mExifRotation);
            } catch (IOException e) {
//                Log.e("Error reading image: " + e.getMessage(), e);
//                setResultException(e);
            } catch (OutOfMemoryError e) {
//                Log.e("OOM reading image: " + e.getMessage(), e);
//                setResultException(e);
            } finally {
                CropUtil.closeSilently(is);
            }
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getActivity().getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            CropUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private int getMaxTextureSize() {
        // The OpenGL texture size is the maximum size that can be drawn in an ImageView
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        this.mRightToolbarButton.setVisibility(View.VISIBLE);
        mCropImageView.setImageRotateBitmapResetBase(mRotateBitmap, true);
        CropUtil.startBackgroundJob(this.getContext(), null, getResources().getString(R.string.msg_please_wait),
                new Runnable() {
                    public void run() {
                        final CountDownLatch latch = new CountDownLatch(1);
                        mHandler.post(new Runnable() {
                            public void run() {
                                if (mCropImageView.getScale() == 1F) {
                                    mCropImageView.center();
                                    mRightToolbarButton.setVisibility(View.VISIBLE);

                                }
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        new Cropper().crop();
                    }
                }, mHandler
        );
    }

    public static FragmentSelectAvatar newInstance(Uri uriInput, Uri uriOutput) {
        FragmentSelectAvatar fragmentSelectAvatar = new FragmentSelectAvatar();
        Bundle b = new Bundle();
        b.putParcelable(SCREEN_DATA, uriInput);
        b.putParcelable(Key.UpdateProfileAvatar, uriOutput);
        fragmentSelectAvatar.setArguments(b);
        return fragmentSelectAvatar;
    }

    private class Cropper {

        private void makeDefault() {
            if (mRotateBitmap == null) {
                return;
            }

            HighlightView hv = new HighlightView(mCropImageView);
            final int width = mRotateBitmap.getWidth();
            final int height = mRotateBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // Make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            @SuppressWarnings("SuspiciousNameCombination")
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mCropImageView.getUnrotatedMatrix(), imageRect, cropRect, mAspectX != 0 && mAspectY != 0);
            mCropImageView.add(hv);
        }

        public void crop() {
            mHandler.post(new Runnable() {
                public void run() {
                    makeDefault();
                    mCropImageView.invalidate();
                    if (mCropImageView.highlightViews.size() == 1) {
                        mCropView = mCropImageView.highlightViews.get(0);
                        mCropView.setFocus(true);
                    }
                }
            });
        }
    }

    private void onSaveClicked() {
        if (mCropView == null || mIsSaving) {
            return;
        }
        mIsSaving = true;

        Bitmap croppedImage;
        Rect r = mCropView.getScaledCropRect(mSampleSize);
        int width = r.width();
        int height = r.height();

        int outWidth = width;
        int outHeight = height;
        if (mMaxX > 0 && mMaxY > 0 && (width > mMaxX || height > mMaxY)) {
            float ratio = (float) width / (float) height;
            if ((float) mMaxX / (float) mMaxY > ratio) {
                outHeight = mMaxY;
                outWidth = (int) ((float) mMaxY * ratio + .5f);
            } else {
                outWidth = mMaxX;
                outHeight = (int) ((float) mMaxX / ratio + .5f);
            }
        }

        try {
            croppedImage = decodeRegionCrop(r, outWidth, outHeight);
        } catch (IllegalArgumentException e) {
            return;
        }

        if (croppedImage != null) {
            mCropImageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, mExifRotation), true);
            mCropImageView.center();
            mCropImageView.highlightViews.clear();
        }
        saveImage(croppedImage);
    }

    private void saveImage(Bitmap croppedImage) {
        if (croppedImage != null) {
            final Bitmap b = croppedImage;
            CropUtil.startBackgroundJob(this.getContext(), null, getResources().getString(R.string.msg_saving),
                    new Runnable() {
                        public void run() {
                            saveOutput(b);
                        }
                    }, mHandler
            );
        } else {
        }
    }

    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
        // Release memory now
        clearImageView();

        InputStream is = null;
        Bitmap croppedImage = null;
        try {
            is = getActivity().getContentResolver().openInputStream(mSourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            final int width = decoder.getWidth();
            final int height = decoder.getHeight();

            if (mExifRotation != 0) {
                // Adjust crop area to account for image rotation
                Matrix matrix = new Matrix();
                matrix.setRotate(-mExifRotation);

                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));

                // Adjust to account for origin at 0,0
                adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
                if (croppedImage != null && (rect.width() > outWidth || rect.height() > outHeight)) {
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) outWidth / rect.width(), (float) outHeight / rect.height());
                    croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
                }
            } catch (IllegalArgumentException e) {
                // Rethrow with some extra information
                throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image ("
                        + width + "," + height + "," + mExifRotation + ")", e);
            }

        } catch (IOException e) {
        } catch (OutOfMemoryError e) {
        } finally {
            CropUtil.closeSilently(is);
        }
        return croppedImage;
    }

    private void clearImageView() {
        mCropImageView.clear();
        if (mRotateBitmap != null) {
            mRotateBitmap.recycle();
        }
        System.gc();
    }

    private void saveOutput(final Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getActivity().getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mSaveAsPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                            90,     // note: quality is ignored when using PNG
                            outputStream);
                }
            } catch (IOException e) {
//                setResultException(e);
//                Log.e("Cannot open file: " + mSaveUri, e);
            } finally {
                CropUtil.closeSilently(outputStream);
            }

            CropUtil.copyExifRotation(
                    CropUtil.getFromMediaUri(this.getContext(), getActivity().getContentResolver(), mSourceUri),
                    CropUtil.getFromMediaUri(this.getContext(), getActivity().getContentResolver(), mSaveUri)
            );

            //setResultUri(mSaveUri);
        }

        final Bitmap b = croppedImage;
        mHandler.post(new Runnable() {
            public void run() {
                mCropImageView.clear();
                b.recycle();
            }
        });

        close();
    }

}
