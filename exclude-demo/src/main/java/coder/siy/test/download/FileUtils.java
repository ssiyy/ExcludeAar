package coder.siy.test.download;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author Siy
 * @since 2022/5/20
 */
public class FileUtils {

    public static final String ROOT_DIR_NAME = "solibs";

    public static File getRootPath(Context context) {
        File dir = context.getFilesDir();
        File file = new File(dir.getAbsolutePath() + File.separator + ROOT_DIR_NAME);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
}
