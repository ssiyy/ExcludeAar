package coder.siy.test;

import android.util.Log;

import me.ele.lancet.base.Origin;
import me.ele.lancet.base.annotations.NameRegex;
import me.ele.lancet.base.annotations.Proxy;
import me.ele.lancet.base.annotations.TargetClass;

/**
 * @author Siy
 * @since 2022/6/1
 */
public class ByHookJava {

    /**
     * hook 系统的Log.i()放
     *
     */
    @NameRegex("coder.siy.test.*")
    @Proxy("e")
    @TargetClass("android.util.Log")
    public static int hookSysLoge(String tag,String abc) {
        tag = "siy";
        abc = "测试hook系统log.e";
       return (int)Origin.call();
    }
}


