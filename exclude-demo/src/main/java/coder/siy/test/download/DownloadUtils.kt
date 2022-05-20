package coder.siy.test.download

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.*
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.util.concurrent.TimeUnit


/**
 *
 * @author  Siy
 * @since  2022/5/19
 */
object DownloadUtils {

    private fun downloadService(listener: DownloadProgressListener): DownloadService {
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(55, TimeUnit.SECONDS)
            .addNetworkInterceptor(DownloadProgressInterceptor(listener))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://coder.siy.test")
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(DownloadService::class.java)
    }

    /**
     * 现在文件
     */
    fun downLoadFile(url: String, filePath:File) = callbackFlow<DownLoad> {
        downloadService(object : DownloadProgressListener {
            var oldPress = -1L
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val progress = (bytesRead * 100) / contentLength
                if (!done) {
                    //done完成
                    if (progress > oldPress ) {
                        oldPress = progress
                        offer(DownLoad(bytesRead, contentLength, done, filePath))
                    }
                }
            }
        }).download(url).run {
            withContext(Dispatchers.IO) {
                filePath.outputStream().use {
                    it.write(byteStream().readBytes())
                }
                send(DownLoad(contentLength(), contentLength(), true, filePath))
            }
        }

        awaitClose {
            //do nothing
        }
    }


}

interface DownloadService {

    @Streaming
    @GET
    suspend fun download(@Url url: String): ResponseBody
}

interface DownloadProgressListener {
    /**
     * 下载实时更新
     *
     * @param bytesRead 下载了多少
     * @param contentLength 一共多少
     * @param done 是否下载完成
     */
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

class DownloadProgressInterceptor(private val listener: DownloadProgressListener) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRes = chain.proceed(chain.request())

        return originalRes.newBuilder()
            .body(DownloadProgressResponseBody(originalRes.body(), listener))
            .build()
    }
}

class DownloadProgressResponseBody(
    private val responseBody: ResponseBody?,
    private val progressListener: DownloadProgressListener?
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody?.contentType()
    }

    override fun contentLength(): Long {
        return responseBody?.contentLength() ?: -1
    }

    override fun source(): BufferedSource? {
        if (bufferedSource == null && responseBody != null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource
    }

    private fun source(source: Source): Source {

        return object : ForwardingSource(source) {
            var totalBytesRead = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressListener?.let {
                    progressListener.update(totalBytesRead, responseBody?.contentLength() ?: -1, bytesRead == -1L)
                }
                return bytesRead
            }
        }
    }
}

data class DownLoad(val bytesRead: Long, val contentLength: Long, val done: Boolean, val filePath: File)