package coder.siy.test;

import android.os.Build;
import android.util.Log;

import java.io.File;

import coder.siy.test.download.FileUtils;
import me.ele.lancet.base.Origin;
import me.ele.lancet.base.annotations.NameRegex;
import me.ele.lancet.base.annotations.Proxy;
import me.ele.lancet.base.annotations.TargetClass;

/**
 * @author Siy
 * @since 2022/5/19
 */
public class HookClass {

    @Proxy("loadLibrary")
    @TargetClass("java.lang.System")
    @NameRegex("com.baidu.*")
    public static void hookSystemloadlibrary(String name) {
        File file = FileUtils.getRootPath(App.Companion.getINSTANCE());
        String path = file.getAbsolutePath() + File.separator + "lib" + name + ".so";
        System.load(path);
        Log.e("siy", path);
    }

    @NameRegex("coder.siy.test.download.*")
    @Proxy("i")
    @TargetClass("android.util.Log")
    public static int hooklogI(String tag, String msg) {
        msg = msg + "lancet";
        return (int) Origin.call();
    }
}
