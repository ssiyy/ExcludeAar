package coder.siy.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.library.BaiduLocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOC_REQ_CODE = 1234
    }

    private lateinit var locService: BaiduLocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locService = BaiduLocationService(this)
        locService.listener = { latitude/*获取纬度信息*/, longitude/*获取经度信息*/, _, locType/*错误码*/ ->
            latitudeTv.text = "纬度：$latitude"
            longitudeTv.text = "经度：$longitude"
            locTypeTv.text = "错误码：$locType"
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //判断是否具有权限
            //请求权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOC_REQ_CODE);

        } else {
            locService.start()
        }
    }

    override fun onStart() {
        super.onStart()
        startLoc()
    }

    override fun onStop() {
        super.onStop()
        locService.stop()
    }
}
