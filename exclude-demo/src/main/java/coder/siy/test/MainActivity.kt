package coder.siy.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coder.siy.test.download.DownloadUtils
import coder.siy.test.download.FileUtils
import com.example.library.BaiduLocationService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOC_REQ_CODE = 1234

        const val tips =
            """
    Code值	Code说明
    61	GPS定位结果，GPS定位成功
    62	无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位
    63	网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位
    66	离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
    68	网络连接失败时，查找本地离线定位时对应的返回结果
    161	网络定位结果，网络定位成功
    162	请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件
    167	服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位
    505	AK不存在或者非法，请按照说明文档重新申请AK
            """
    }

    private var locService: BaiduLocationService? = null

    private val abi = if (Build.VERSION.SDK_INT >= 21) {
        Build.SUPPORTED_ABIS[0]
    } else {
        Build.CPU_ABI
    }

    private val SO_URL = "https://raw.githubusercontent" +
            ".com/Siy-Wu/ExcludeAar/master/libraries/library/src/main/jniLibs/${abi}/liblocSDK7b.so"
    private val SO_NAME = "liblocSDK7b.so"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tipsTv.run {
            movementMethod = ScrollingMovementMethod.getInstance();
            text = tips
        }
        latitudeTv.text = abi

        longitudeTv.text = "点我定位"
        longitudeTv.click {
            downloadBaiduSo()
        }
    }

    private fun downloadBaiduSo() {
        val file = File(FileUtils.getRootPath(this), SO_NAME)
        if (file.exists()) {
            initLocService()
            return
        }
        val flow = DownloadUtils.downLoadFile(SO_URL, file)
        lifecycleScope.launchWhenCreated {
            launch {
                flow.onEach {
                    val progress = (it.bytesRead * 100) / it.contentLength
                    longitudeTv.text = "下载中-${progress}"
                }.onStart {
                    longitudeTv.text = "下载中-${0}"
                }.filter {
                    it.done
                }.catch {}.collect {
                    if (it.done) {
                        initLocService()
                    }
                }
            }
        }
    }


    private fun initLocService() {
        locService = BaiduLocationService(this@MainActivity)
        locService?.listener = { latitude/*获取纬度信息*/, longitude/*获取经度信息*/, _, locType/*错误码*/ ->
            latitudeTv.text = "纬度：$latitude"
            longitudeTv.text = "经度：$longitude"
            locTypeTv.text = "Code值：$locType"
        }
        startLoc()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOC_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLoc()
            }
        }
    }

    private fun startLoc() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //判断是否具有权限
            //请求权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOC_REQ_CODE);

        } else {
            locService?.start()
        }
    }

    override fun onStart() {
        super.onStart()
        startLoc()
    }

    override fun onStop() {
        super.onStop()
        locService?.stop()
    }
}
