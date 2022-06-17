package coder.siy.test;

import android.util.Log;

/**
 *
 * 用来验证一些猜想
 *
 * @author Siy
 * @since 2022/6/1
 */
public class Test {

    /**
     * 用来验证是否在内部内中创建了静态的类
     *
     *
     * 验证实在内部内中添加了一个静态内部类
     *
     *
     *
     *
     */
    public void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("test", "我要被hook");
            }
        }).start();
    }
}
