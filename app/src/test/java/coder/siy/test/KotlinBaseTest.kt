package coder.siy.test

import org.junit.Test
import kotlin.coroutines.experimental.*

/**
 * Created by Siy on 2018/2/9.
 *
 * @author Siy
 * @date 2018/2/9.
 */
class KotlinBaseTest {

    @Test
    fun testCorcontious(){
        println("before coroutine${Thread.currentThread().name}")
        //启动我们的协程
        asyncCalcMd5("test.zip") {
            println("in coroutine. Before suspend.")
            //暂停我们的线程，并开始执行一段耗时操作
            val result: String = suspendCoroutine {
                continuation ->
                println("in suspend block.${Thread.currentThread().name}")
               continuation.resume(calcMd5(continuation.context[FilePath]!!.path))
                println("after resume.")
            }
            println("in coroutine. After suspend. result = $result")
        }
        println("after coroutine")
    }


    private  fun asyncCalcMd5(path: String, block: suspend () -> Unit) {
        val continuation = object : Continuation<Unit> {
            override val context: CoroutineContext
                get() = FilePath(path)

            override fun resume(value: Unit) {
                println("resume: $value")
            }

            override fun resumeWithException(exception: Throwable) {
                println(exception.toString())
            }
        }
        block.startCoroutine(continuation)
    }


    private fun calcMd5(path: String): String {
        println("calc md5 for $path.")
        //暂时用这个模拟耗时
        Thread.sleep(1000)
        //假设这就是我们计算得到的 MD5 值
        return System.currentTimeMillis().toString()
    }

    /**
     * 上下文，用来存放我们需要的信息，可以灵活的自定义
     */
    class FilePath(val path: String) : AbstractCoroutineContextElement(FilePath) {
        companion object Key : CoroutineContext.Key<FilePath>
    }

}