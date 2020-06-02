package com.liudukun.dkchat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Size;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.liudukun.dkchat.DKApplication;

import com.liudukun.dkchat.R;
import com.liudukun.dkchat.model.DKUser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TimerTask;


public class ImageUtil {
    public static final String HandleImageThreadName = "HandleImageThreadName";
    public static Drawable Default_Face_TintAndCircle_Drawable;
    public static Drawable Default_Face_Group_TintAndCircle_Drawable;
    public static Drawable Default_Image_Drawable;
    public static Bitmap Default_Face_TintAndCircle_Bitmap;
    public static Bitmap Default_Face_Group_TintAndCircle_Bitmap;
    public static Bitmap Default_Image_Bitmap;


    public static ImageUtil instance ;

    public static ImageUtil initInstance() {
        if (instance == null){
            instance = new ImageUtil();
        }
        return instance;
    }

    ImageUtil(){
        DKApplication context = DKApplication.sharedInstance();

        Default_Face_TintAndCircle_Drawable = tintCircleImage(context,R.drawable.default_face,R.color.color_VI);
        Default_Face_Group_TintAndCircle_Drawable = tintCircleImage(context,R.drawable.default_face_group,R.color.color_VI);


        Default_Face_TintAndCircle_Bitmap = ImageUtil.drawableToBitmap(Default_Face_TintAndCircle_Drawable);
        Default_Face_Group_TintAndCircle_Bitmap = ImageUtil.drawableToBitmap(Default_Face_Group_TintAndCircle_Drawable);


        Default_Image_Drawable = context.getResources().getDrawable(R.drawable.default_image);
        Default_Image_Bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.default_image);

    }

    static public void getDefaultImage(){

    }

    static public void glideSetAvatar(DKUser user,ImageView avatarView){
        if (user == null){
            final Drawable drawable = ImageUtil.Default_Face_TintAndCircle_Drawable;
            avatarView.setImageDrawable(drawable);
        }else{
            glideSetAvatar(user.avatar,avatarView);
        }
    }


    static public void glideSetAvatar(String avatar,ImageView avatarView){
        if (!StringUtil.isEmpty(avatar)) {
            Glide.with(avatarView.getContext()).load(StringUtil.thumbnailAvatarPath(avatar)).circleCrop().addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    final Drawable drawable = ImageUtil.Default_Face_TintAndCircle_Drawable;
                    avatarView.setImageDrawable(drawable);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    avatarView.setImageDrawable(resource);
                    return false;
                }
            }).into(avatarView);
        }else{
            final Drawable drawable = ImageUtil.Default_Face_TintAndCircle_Drawable;
            avatarView.setImageDrawable(drawable);
        }
    }

    public static Drawable tintImage(Context context, int pic_res, int color_res) {
        Drawable drawable = (Drawable) ContextCompat.getDrawable(context, pic_res);
        return tintImage(context,drawable,color_res);
    }

    public static Drawable tintImage(Context context, Drawable drawable,  int color_res) {
        Drawable wrap = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrap, ContextCompat.getColor(context, color_res));
        return wrap;
    }

    public static Drawable tintCircleImage(Context context, int pic_res, int color_res) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), pic_res);
        Bitmap roundBitmap = toRoundBitmap(bitmap);
        Drawable drawable = bitmapToDrawable(roundBitmap, context);
        Drawable wrap = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrap, ContextCompat.getColor(context, color_res));
        return wrap;
    }

    public static Drawable tintCircleImage(Context context, Drawable drawable, int color_res) {
        Bitmap bitmap = drawableToBitmap(drawable);
        return tintCircleImage(context,bitmap,color_res);
    }


    public static Drawable tintCircleImage(Context context, Bitmap bitmap, int color_res) {
        Bitmap roundBitmap = toRoundBitmap(bitmap);
        Drawable drawable1 = bitmapToDrawable(roundBitmap, context);
        Drawable wrap = DrawableCompat.wrap(drawable1).mutate();
        DrawableCompat.setTint(wrap, ContextCompat.getColor(context, color_res));
        return wrap;
    }


    public static Bitmap setRoundCornerBitmap(Bitmap bitmap, float roundPx) {
        int width = bitmap.getWidth();
        int heigh = bitmap.getHeight();
        // 创建输出bitmap对象
        Bitmap outmap = Bitmap.createBitmap(width, heigh,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outmap);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, heigh);
        final RectF rectf = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectf, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return outmap;
    }


    // 1.从资源中获取Bitmap
    public void useBitmap(Context context, ImageView imageView, int drawableId) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                drawableId);
        imageView.setImageBitmap(bitmap);

    }

    // 2.Bitmap ---> byte[]
    public byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    // 3.byte[] ---->bitmap
    public Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    // 4.Bitmap 缩放方法
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int heigh) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scalewidth = (float) width / w;
        float scaleheigh = (float) heigh / h;
        matrix.postScale(scalewidth, scaleheigh);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newBmp;

    }

    // 5. Drawable----> Bitmap
    public static Bitmap drawableToBitmap(Drawable drawable) {

        // 获取 drawable 长宽
        int width = drawable.getIntrinsicWidth();
        int heigh = drawable.getIntrinsicHeight();

        drawable.setBounds(0, 0, width, heigh);

        // 获取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 创建bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, heigh, config);
        // 创建bitmap画布
        Canvas canvas = new Canvas(bitmap);
        // 将drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    // 8. bitmap ---Drawable
    public static Drawable bitmapToDrawable(Bitmap bitmap, Context context) {
        BitmapDrawable drawbale = new BitmapDrawable(context.getResources(),
                bitmap);
        return drawbale;
    }

    // 9. drawable进行缩放 ---> bitmap 然后比对bitmap进行缩放
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 调用5 中 drawable转换成bitmap
        Bitmap oldbmp = drawableToBitmap(drawable);

        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * @param outFile 图片的目录路径
     * @param bitmap
     * @return
     */
    public static File storeBitmap(File outFile, Bitmap bitmap) {
        // 检测是否达到存放文件的上限
        if (!outFile.exists() || outFile.isDirectory()) {
            outFile.getParentFile().mkdirs();
        }
        FileOutputStream fOut = null;
        try {
            outFile.deleteOnExit();
            outFile.createNewFile();
            fOut = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (IOException e1) {
            outFile.deleteOnExit();
        } finally {
            if (null != fOut) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    outFile.deleteOnExit();
                }
            }
        }
        return outFile;
    }

    public static Bitmap getBitmapFormPath(Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream input = DKApplication.sharedInstance().getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            //图片分辨率以480x800为标准
            float hh = 800f;//这里设置高度为800f
            float ww = 480f;//这里设置宽度为480f
            int degree = getBitmapDegree(uri);
            if (degree == 90 || degree == 270) {
                hh = 480;
                ww = 800;
            }
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (originalWidth / ww);
            } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (originalHeight / hh);
            }
            if (be <= 0)
                be = 1;
            //比例压缩
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;//设置缩放比例
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = DKApplication.sharedInstance().getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);

            input.close();
            compressImage(bitmap);
            bitmap = rotateBitmapByDegree(bitmap, degree);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;//再进行质量压缩
    }

    public static Bitmap getBitmapFormPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getBitmapFormPath(Uri.fromFile(new File(path)));
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 读取图片的旋转的角度
     */
    public static int getBitmapDegree(Uri uri) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(FileUtil.getPathFromUri(uri));
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 读取图片的旋转的角度
     */
    public static int getBitmapDegree(String fileName) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(fileName);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static int[] getImageSize(String path) {
        int size[] = new int[2];
        try {
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, onlyBoundsOptions);
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            //size[0] = originalWidth;
            //size[1] = originalHeight;

            int degree = getBitmapDegree(path);
            if (degree == 0) {
                size[0] = originalWidth;
                size[1] = originalHeight;
            } else {
                //图片分辨率以480x800为标准
                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                if (degree == 90 || degree == 270) {
                    hh = 480;
                    ww = 800;
                }
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (originalWidth / ww);
                } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (originalHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = be;//设置缩放比例
                bitmapOptions.inDither = true;//optional
                bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
                Bitmap bitmap = BitmapFactory.decodeFile(path, bitmapOptions);
                bitmap = rotateBitmapByDegree(bitmap, degree);
                size[0] = bitmap.getWidth();
                size[1] = bitmap.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;

    }

    public static int[] getImageSize(Uri uri) {
        int size[] = new int[2];
        try {
            InputStream is = DKApplication.sharedInstance().getContentResolver()
                    .openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, onlyBoundsOptions);
            int originalWidth = onlyBoundsOptions.outWidth;
            int originalHeight = onlyBoundsOptions.outHeight;
            //size[0] = originalWidth;
            //size[1] = originalHeight;


            int degree = getBitmapDegree(uri);
            if (degree == 0) {
                size[0] = originalWidth;
                size[1] = originalHeight;
            } else {

                //图片分辨率以480x800为标准
                float hh = 800f;//这里设置高度为800f
                float ww = 480f;//这里设置宽度为480f
                if (degree == 90 || degree == 270) {
                    hh = 480;
                    ww = 800;
                }
                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (originalWidth / ww);
                } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (originalHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = be;//设置缩放比例
                bitmapOptions.inDither = true;//optional
                bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
                is = DKApplication.sharedInstance().getContentResolver()
                        .openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
                bitmap = rotateBitmapByDegree(bitmap, degree);
                size[0] = bitmap.getWidth();
                size[1] = bitmap.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;

    }

    public static CopyImageInfo copyImage(String path, String dir) {
        CopyImageInfo info = new CopyImageInfo();
        if (null == path) {
            return null;
        }
        try {
            int index = path.lastIndexOf(".");
            String fileType = "";
            if (index >= 0) {
                fileType = path.substring(index + 1);
            }
            String newFileName = dir + File.separator + System.currentTimeMillis() + "." + fileType;
            InputStream is = new FileInputStream(new File(path));
            int degree = getBitmapDegree(path);
            File file = new File(newFileName);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, onlyBoundsOptions);
            info.setWidth(onlyBoundsOptions.outWidth);
            info.setHeight(onlyBoundsOptions.outHeight);

            //没有旋转，直接copy
            if (degree == 0) {
                is = new FileInputStream(new File(path));
                OutputStream os = new FileOutputStream(file);
                byte bt[] = new byte[1024 * 10];
                int c;
                while ((c = is.read(bt)) > 0) {
                    os.write(bt, 0, c);
                }
                is.close();
                os.close();
            } else {
                int ww = 400;
                int hh = 800;
                if (degree == 90 || degree == 270) {
                    ww = 800;
                    hh = 400;
                }
                int be = 1;//be=1表示不缩放
                if (info.width > info.height && info.width > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = info.width / ww;
                } else if (info.width < info.height && info.height > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = info.height / hh;
                }
                if (be <= 0) {
                    be = 1;
                }
                //比例压缩
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = be;//设置缩放比例
                bitmapOptions.inDither = true;//optional
                bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
                is = new FileInputStream(new File(path));
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
                bitmap = rotateBitmapByDegree(bitmap, degree);
                info.setWidth(bitmap.getWidth());
                info.setHeight(bitmap.getHeight());
                storeBitmap(file, bitmap);
            }
            info.setPath(newFileName);
            return info;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式
        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output;
    }

    public static class CopyImageInfo {
        String path;
        int width;
        int height;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
