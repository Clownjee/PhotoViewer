package com.ghizzoniandrea.photoviewer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ghizzoniandrea on 2017/3/15.
 */
public class RxImage {

    public static Observable<Uri> saveImageAndGetPath(final Activity context, final String url, final String title) {
        return new RxPermissions(context).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).flatMap(new Function<Boolean, ObservableSource<Uri>>() {
            @Override
            public ObservableSource<Uri> apply(Boolean aBoolean) throws Exception {
                return Observable.create(new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                        Bitmap bitmap = null;
                        try {
                            bitmap = Glide.with(context)
                                    .load(url)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                        } catch (Exception ex) {
                            e.onError(ex);
                        }
                        if (bitmap == null) {
                            e.onError(new Exception("无法下载到图片!"));
                        }
                        e.onNext(bitmap);
                        e.onComplete();
                    }
                }).flatMap(new Function<Bitmap, ObservableSource<Uri>>() {
                    @Override
                    public ObservableSource<Uri> apply(Bitmap bitmap) throws Exception {
                        File appDir = new File(Environment.getExternalStorageDirectory(), "Meizi");
                        if (!appDir.exists()) {
                            appDir.mkdir();
                        }
                        String fileName = title.replace('/', '-') + ".jpg";
                        File file = new File(appDir, fileName);
                        try {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            assert bitmap != null;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri uri = Uri.fromFile(file);
                        Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                        context.sendBroadcast(scannerIntent);
                        Uri contentURI = getImageContentUri(context, file.getAbsolutePath());
                        return Observable.just(contentURI);
                    }
                }).subscribeOn(Schedulers.io());
            }
        });
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Uri getImageContentUri(Context context, String absPath) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

}
