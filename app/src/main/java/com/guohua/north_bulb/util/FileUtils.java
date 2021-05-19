package com.guohua.north_bulb.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * @author Leo
 * @detail 文件操作工具类 包括文件存在判断 复制Assets中文件到SDCard 复制文件
 * @time 2015-11-16
 */
public final class FileUtils {

    /**
     * 文件是否存在
     *
     * @param str
     * @return
     */
    public static boolean isFileExist(String str) {
        File file = new File(getRootPath() + File.separator + str);
        return file.exists();
    }

    /**
     * 保存文件到sdcard
     *
     * @param is
     * @param to
     * @param fileName
     * @return
     */
    public static boolean saveFile2Sdcard(InputStream is, String to, String fileName) {
        String rootPath = getRootPath();

        File file = new File(rootPath + File.separator + to);
        if (!file.exists()) {
            file.mkdir();
        }

        OutputStream os = null;

        try {
            os = new FileOutputStream(rootPath + File.separator + to + File.separator + fileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 文件复制
     *
     * @param context  用于得到AssetManager来取得文件
     * @param fileName 要打开的assets文件夹下的文件名 可以带路径
     * @param to       要复制到哪里 必须带文件名
     * @return
     */
    public static boolean copyFileFromAssets2Sdcard(Context context, String fileName,
                                                    String to) {
        if (isFileExist(to)) {
            return true;
        }

        InputStream is = null;
        OutputStream os = null;

        String rootPath = getRootPath();
        if (TextUtils.equals(rootPath, "error")) {
            return false;
        }
        try {
            is = context.getClass().getClassLoader().getResourceAsStream(fileName);
            os = new FileOutputStream(rootPath + fileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 得到根目录 如果返回为error则不存在SDcard
     *
     * @return
     */
    public static String getRootPath() {
        String rootPath = "/sdcard";
        String state = Environment.getExternalStorageState();
        if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
            rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        }
        return rootPath;
    }

    /**
     * 复制文件
     *
     * @param from
     * @param to
     * @return
     */
    private boolean copyFile(String from, String to) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            in = fis.getChannel();
            out = fos.getChannel();
            // size = in.transferTo(0, in.size(), out);
            if (in.transferTo(0, in.size(), out) > 0) {
                return true;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }
}
