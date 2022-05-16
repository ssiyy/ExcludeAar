package exclude.gradle.plugin

import com.android.tools.r8.internal.it
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

    /**
     * 需要过滤的类(需要全类名,不需要.class结尾)
     */
    var excludeClasses: List<String> = listOf()
        set(value) {
            field = value.map {
                it.replace('.', '\\').plus(".class")
            }
        }

    /**
     * 过滤的包名
     */
    fun excludeClasses(vararg packages: String) {
        this.excludePackages = packages.toList()
    }


    /**
     * 需要过滤的包名:['com.baidu']
     */
    var excludePackages: List<String> = listOf()
        set(value) {
            field = value.map {
                it.replace('.', '\\').plus("\\**")
            }
        }
}


/**
 * 过滤Aar包需要的Extension参数
 */
class AarExcludeParam(name: String) : JarExcludeParam(name) {
    /**
     * 过滤的so文件
     */
    var excludeSos: List<String> = listOf()
        set(value) {
            field = value.map {
                "**\\$it.so"
            }
        }
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
open class ExcludeExtension @JvmOverloads constructor(
    project: Project,
    var autoDependence: Boolean = true
) {
    val jarsParams = project.container(JarExcludeParam::class.java)
    val aarsParams = project.container(AarExcludeParam::class.java)

    fun jars(action: Action<NamedDomainObjectContainer<JarExcludeParam>>) {
        action.execute(jarsParams)
    }

    fun aars(action: Action<NamedDomainObjectContainer<AarExcludeParam>>) {
        action.execute(aarsParams)
    }
}