package coder.siy.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.library.BaiduLocationService
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Siy on 2018/5/16.
 *
 * @author Siy
 * @date 2018/5/16.
 */
class MainActivity : AppCompatActivity() {
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

    override fun onStart() {
        super.onStart()
        locService.start()
    }

    override fun onStop() {
        super.onStop()
        locService.stop()
    }
}