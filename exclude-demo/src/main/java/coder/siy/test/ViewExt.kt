package coder.siy.test

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.widget.TintContextWrapper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


/**
 *
 * @author  Siy
 * @since  2022/5/19
 */


/**
 * 类似于RxJava throttleFirst功能
 *
 * 过滤第一条信息
 *
 * @param periodMillis 过滤的时间段
 */
fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
    return flow {
        var lastTime = 0L
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime >= periodMillis) {
                lastTime = currentTime
                emit(value)
            }
        }
    }
}

val Context.act: Activity?
    get() {
        if (this is Activity) {
            return this
        }

        if (this is ContextWrapper) {
            if (baseContext is Activity) {
                return baseContext as Activity
            }

            if (baseContext is TintContextWrapper) {
                val ctx = (baseContext as TintContextWrapper).baseContext
                if (ctx is Activity) {
                    return ctx
                }
            }
        }

        return null
    }


/**
 * 一个防止暴力点击的View扩展，用协程实现可以随生命周期自动解除绑
 *
 * @param click 点击操作
 */
fun View.click(
    scope: CoroutineScope = if (context is LifecycleOwner) {
        (context as LifecycleOwner).lifecycleScope
    } else {
        val activity = context.act
        if (activity is LifecycleOwner) {
            activity.lifecycleScope
        } else {
            GlobalScope
        }
    }, click: ((View) -> Unit)
) {
    callbackFlow<View> {
        setOnClickListener {
            try {
                offer(it)
            } catch (e: Exception) {
            }
        }

        awaitClose {
            setOnClickListener(null)
        }
    }.throttleFirst(1000L)
        .catch {
            //异常捕捉，不做处理
        }.onEach {
            click(it)
        }.launchIn(scope)
}