package coder.siy.test

import kotlinx.coroutines.experimental.*
import org.junit.Test

/**
 * Created by Siy on 2018/2/8.
 *
 * @author Siy
 * @date 2018/2/8.
 */
class KotlinTest {

    @Test
    fun testAsync() = runBlocking {
        val result1 = async(CommonPool) {
            delay(5 * 1000)
            "CommonPool,线程：${Thread.currentThread().name}"
        }

        println("runBlocking,线程：${Thread.currentThread().name}")
        println(result1.await())
        println("runBlocking,线程：${Thread.currentThread().name}")

    }

    @Test
    fun testAsync_() {
        println("first,线程：${Thread.currentThread().name}")

        runBlocking {
            val result1 = async(CommonPool) {
                delay(5 * 1000)
                "CommonPool,线程：${Thread.currentThread().name}"
            }
            println("runBlocking,线程：${Thread.currentThread().name}")
            println(result1.await())
            println("runBlocking,线程：${Thread.currentThread().name}")
        }
        println("second,线程：${Thread.currentThread().name}")
    }

    @Test
    fun testLaunch() =
            runBlocking {
                println("first,线程：${Thread.currentThread().name}")

                launch(coroutineContext) {
                    val result1 = async(CommonPool) {
                        delay(5 * 1000)
                        "CommonPool,线程：${Thread.currentThread().name}"
                    }

                    println("launch,线程：${Thread.currentThread().name}")
                    println(result1.await())
                    println("launch,线程：${Thread.currentThread().name}")
                }
                println("second,线程：${Thread.currentThread().name}")

                delay(10 * 1000)
            }

    @Test
    fun testSiy(){
        var a = 1
        val f =  {

            a++
        }
        println("111:+"+f())
        println("222:+"+f())


        (1..5).forEach {

        }
    }
}
