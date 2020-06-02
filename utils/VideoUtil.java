package com.liudukun.dkchat.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class VideoUtil {

    public static MediaMetadataRetriever getMediaMetadataRetriever(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media;
    }

    public static Bitmap getVideoThumb(MediaMetadataRetriever media) {
        return media.getFrameAtTime();
    }

    public static Long getVideoTotalTime(MediaMetadataRetriever media) {
        String timeString = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Long time = Long.valueOf(timeString);
        return time;
    }

    public static Long getVideoWidth(MediaMetadataRetriever media) {
        String timeString = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        Long time = Long.valueOf(timeString);
        return time;
    }

    public static Long getVideoHeight(MediaMetadataRetriever media) {
        String timeString = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        Long time = Long.valueOf(timeString);
        return time;
    }

    public static String bitmap2File(Bitmap bitmap, String path) {
        File f = new File(path);
        if (f.exists()) f.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            return "";
        }
        return f.getAbsolutePath();
    }

    public static void saveAsCacheFile(String path, VideoInfoCallback cb) {
        if (!path.isEmpty()) {
            MediaMetadataRetriever retriever = VideoUtil.getMediaMetadataRetriever(path);
            Bitmap bitmap = VideoUtil.getVideoThumb(retriever);

            String snapshot_path =  StringUtil.getRandomPathForMedia();
            String video_path =  StringUtil.getRandomPathForMedia();
            snapshot_path = VideoUtil.bitmap2File(bitmap, snapshot_path);

            fileCopy(path,video_path);

            cb.outputVideoInfo(snapshot_path,video_path);
        }
    }

    public static void fileCopy(String sourcePath, String targetPath) {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                in.close();
                outStream.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public interface VideoInfoCallback{
        void outputVideoInfo(String snapshotPath,String videoPath);
    }
}
