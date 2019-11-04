package com.example.library

import android.content.Context
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption

/**
 *
 * <pre>
 * 返回值	返回值说明
 * 61	GPS定位结果，GPS定位成功
 * 62	无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位
 * 63	网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位
 * 66	离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
 * 68	网络连接失败时，查找本地离线定位时对应的返回结果
 * 161	网络定位结果，网络定位成功
 * 162	请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件
 * 167	服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位
 * 505	AK不存在或者非法，请按照说明文档重新申请AK
 * </pre>
 * Created by Siy on 2018/5/17.
 *
 * @author Siy
 * @date 2018/5/17.
 */
class BaiduLocationService(context: Context) {
    private val mLocationClient: LocationClient = LocationClient(context)

    var listener: ((Double?, Double?, Float?, Int?) -> Unit)? = null

    init {
        initOption()
        mLocationClient.registerLocationListener { location ->
            listener?.invoke(location?.latitude, location?.longitude, location?.radius, location?.locType)
        }
    }

    fun start() {
        if (!mLocationClient.isStarted) {
            mLocationClient.start()
        }

    }

    fun stop() {
        if (mLocationClient.isStarted) {
            mLocationClient.stop()
        }
    }

    private fun initOption() {
        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        option.coorType = "bd09ll"
        option.scanSpan = 1000
        option.openGps = true
        option.isLocationNotify = true
        option.isIgnoreKillProcess = false
        option.isIgnoreCacheException = false
        option.wifiCacheTimeOut = 5 * 60 * 1000
        option.enableSimulateGps = false
        mLocationClient.locOption = option
    }
}