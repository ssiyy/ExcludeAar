package exclude.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project


/**
 * 详细文档
 *
 * https://blog.csdn.net/chennai1101/article/details/103279116
 */


/**
 * 过滤Jar包需要Extension参数
 */
open class JarExcludeParam
/**
 * 构造函数必须要一个name:String
 */
    (name: String) {
    /**
     * 文件名称
     */
    var name: String? = name

    /**
     * 文件路径
     */
    var path: String? = null
}


/**
 * 过滤Aar包需要的Extension参数
 */
class AarExcludeParam(name:String) : JarExcludeParam(name){

}

/**
 *
 *groovy:
 *
 *  excludePluginExt{
 *
 *      aars{
 *          testArr {
 *            path "build/libs/baiduLBS"
 *            excludePackages  'com.baidu'
 *            excludeClasses  'com.baidu.LocBaidu'
 *            excludeSos  'liblocSDK7b'
 *          }
 *        }
 *
 *      jars{
 *          testJar{
 *            path "build/libs/baiduLBS"
 *            excludePackages 'com.baidu'
 *            excludeClasses 'com.baidu.LocBaidu'
 *          }
 *       }
 *    }
 *
 *
 * kotlin:
 *
 * configure<ExcludeParamExtension> {
 *
 *      aars{
 *          create("testArr") {
 *              path(build/libs/baiduLBS")
 *              excludePackages('com.baidu')
 *              excludeClasses('com.baidu.LocBaidu)
 *              excludeSos('liblocSDK7b')
 *          }
 *      }
 *
 *     jars{
 *             create("testJar") {
 *              path(build/libs/baiduLBS")
 *              excludePackages('com.baidu')
 *              excludeClasses('com.baidu.LocBaidu)
 *          }
 *     }
 * }
 *
 */
class ExcludeExtension @JvmOverloads constructor(
    project:Project,
    var autoDependence:Boolean = true
){
    val jarParam = project.container(JarExcludeParam::class.java)
    val aarParam = project.container(AarExcludeParam::class.java)

    fun  xx(action: Action<NamedDomainObjectContainer<JarExcludeParam>>){
        action.execute(jarParam)
    }

    fun yy(action: Action<NamedDomainObjectContainer<AarExcludeParam>>){
        action.execute(aarParam)
    }
}