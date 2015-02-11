package com.jfo.common.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.libs.defer.Defer;
import com.libs.defer.Defer.Promise;
import com.libs.defer.DeferHelper.MyDefer;
import com.libs.utils.LogUtils;
import com.libs.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AvatarPicker {
    private static final int JOB_CHOOSE_PHOTO_SRC = 1;
    private static final int JOB_OPEN_CAMERA = 2;
    private static final int JOB_OPEN_GALLERY = 3;

    private static final String PHOTO_DIR = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
    private static final int AVATAR_WIDTH = 100;
    private static final int AVATAR_HEIGHT = 100;

    private FragmentActivity mContext;
    private Defer mDefer;
    private String mPhotoFileName;

    public AvatarPicker(FragmentActivity context) {
        mContext = context;
        File dir = new File(PHOTO_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public Promise pick() {
        mDefer = new MyDefer(mContext);
        addWrapperFragment();
        return mDefer.promise();
    }

    private void deny(final Object... args) {
        removeWrapperFragment();
        mDefer.reject(args);
    }

    private void accept(final Object... args) {
        removeWrapperFragment();
        mDefer.resolve(args);
    }

    private void addWrapperFragment() {
        final FragmentManager fm = mContext.getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.add(new WrapperFragment(), "AvatarPicker");
        ft.commit();
    }

    private void removeWrapperFragment() {
        final FragmentManager fm = mContext.getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentByTag("AvatarPicker"));
        ft.commit();
    }

    private void choosePhotoSrc(Fragment fragment) {
        Intent intent = new Intent(mContext, MenuActivity.class);
        String[] args = new String[] { "拍照", "图库" };
        intent.putExtra(MenuActivity.EXTRA_MENU_ITEMS, args);
        fragment.startActivityForResult(intent, JOB_CHOOSE_PHOTO_SRC);
    }

    private void openCamera(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(PHOTO_DIR + File.separator + getPhotoFileName())));
        try {
            fragment.startActivityForResult(intent, JOB_OPEN_CAMERA);
        } catch (ActivityNotFoundException e) {
            Utils.showMessage(mContext, "没有支持这一操作的应用");
        }
    }

    private void openGallery(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // fillIntentCrop(intent);
        try {
            fragment.startActivityForResult(intent, JOB_OPEN_CAMERA);
        } catch (ActivityNotFoundException e) {
            Utils.showMessage(mContext, "未找到图片选择程序");
        }
    }

    private void openGallery(Fragment fragment, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && uri != null) {
            String url = AvatarPickerFuncs.getPath(fragment.getActivity(), uri);
            uri = Uri.fromFile(new File(url));
        }

        if (uri == null) {
            MediaScannerConnection.scanFile(mContext, new String[] { PHOTO_DIR }, null, null);
            File photoImage = new File(PHOTO_DIR + File.separator + mPhotoFileName);
            if (photoImage.exists()) {
                uri = Uri.fromFile(photoImage);
            }
        }

        if (uri == null) {
            return;
        }

        intent.setDataAndType(uri, "image/*");
        fillIntentCrop(intent);
        try {
            fragment.startActivityForResult(intent, JOB_OPEN_GALLERY);
        } catch (ActivityNotFoundException e) {
            Utils.showMessage(mContext, "未找到图片选择程序");
        }
    }

    private void onPhotoTaken(Intent data) {
        if (data == null) {
            deny();
            return;
        }
        Bitmap bm = data.getParcelableExtra("data");
        if (bm == null) {
            deny();
            return;
        }
        Bitmap sclaledPortrait = Bitmap.createScaledBitmap(bm, AVATAR_WIDTH, AVATAR_WIDTH, false);
        accept(sclaledPortrait);
    }

    private String getPhotoFileName() {
        if (mPhotoFileName == null) {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
            mPhotoFileName = dateFormat.format(date) + ".jpg";
        }
        return mPhotoFileName;
    }

    private void fillIntentCrop(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", AVATAR_WIDTH);
        intent.putExtra("outputY", AVATAR_HEIGHT);
        intent.putExtra("return-data", true);
    }

    private class WrapperFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            choosePhotoSrc(this);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            LogUtils.d("requestCode:" + requestCode + ",resultCode:" + resultCode);
            if (requestCode == JOB_CHOOSE_PHOTO_SRC) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    int id = data.getIntExtra(MenuActivity.KEY_MENU_ID, -1);
                    if (id == 0) {
                        openCamera(this);
                    } else if (id == 1) {
                        openGallery(this);
                    }
                }
            } else if (requestCode == JOB_OPEN_CAMERA) {
                openGallery(this, data == null ? null : data.getData());
            } else if (requestCode == JOB_OPEN_GALLERY) {
                onPhotoTaken(data);
            }
        }

    }

}
