package exclude.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
import java.io.File

/**
 * 过滤Jar包需要的参数
 */
open class JarExculdeParam
/**
 * 构造函数必须有一个name:String
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
     * 需要过滤的包名:['com.baidu']
     */
    var excludePackages: Array<String> = arrayOf()
    val excludePackageRegex
        get() = excludePackages.map {
            it.replace('.', '\\').plus("\\**")
        }

    /**
     * 需要过滤的类(需要全类名,不需要.class结尾)
     */
    var excludeClasses: Array<String> = arrayOf()
    val excludeClassRegex
        get() = excludeClasses.map {
            it.replace('.', '\\').plus(".class")
        }

    /**
     * 给路径赋值
     */
    fun path(path: String) {
        this.path = path
    }

    /**
     * 过滤的包名
     */
    fun excludePackages(vararg packages: String) {
        this.excludePackages = packages as Array<String>
    }

    /**
     * 过滤的类名
     */
    fun excludeClasses(vararg classes: String) {
        this.excludeClasses = classes as Array<String>
    }

    override fun toString(): String {
        return "name = $name, path = $path, excludePackages=$excludePackages, excludeClasses=$excludeClasses"
    }
}

/**
 * 过滤aar包需要的参数
 */
class AarExculdeParam(name: String) : JarExculdeParam(name) {
    /**
     * 过滤的so文件
     */
    var excludeSos: Array<String> = arrayOf()
    val excludeSoRegex
        get() = excludeSos.map {
            "**\\${it}.so"
        }

    fun excludeSos(vararg sos: String) {
        this.excludeSos = sos as Array<String>
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
 *            excludeClasses  'com.baidu.LocBaidu
 *            excludeSos  'liblocSDK7b'
 *          }
 *        }
 *
 *      jars{
 *          testJar{
 *            path "build/libs/baiduLBS"
 *            excludePackages 'com.baidu'
 *            excludeClasses 'com.baidu.LocBaidu
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
open class ExcludeParamExtension @JvmOverloads constructor(project: Project, var autoDependencies: Boolean = true) {
    val aars = project.container(AarExculdeParam::class.java)
    val jars = project.container(JarExculdeParam::class.java)

    fun aars(action: Action<NamedDomainObjectContainer<AarExculdeParam>>) {
        action.execute(aars)
    }

    fun jars(action: Action<NamedDomainObjectContainer<JarExculdeParam>>) {
        action.execute(jars)
    }
}


/**
 *
 * https://blog.csdn.net/XSF50717/article/details/89857669
 *
 * https://blog.csdn.net/universsky2015/article/details/83593307
 *
 * https://blog.csdn.net/brycegao321/article/details/82754014
 *
 * https://docs.gradle.org/current/userguide/custom_plugins.html#example_a_build_for_a_custom_plugin
 *
 *
 * 自定义过滤冲突类/包插件类
 *
 * Created by Siy on 2019/11/04
 *
 * @author Siy
 */
class ExcludePlugin : Plugin<Project> {

    private lateinit var project: Project

    private lateinit var extParam: ExcludeParamExtension

    /**
     * 过滤的根目录
     */
    private val excludePlguinRootDir by lazy {
        File(project.buildDir, "excludePlguin")
    }

    /**
     * 解压aar文件存放的目录
     */
    private val unZipAarFile by lazy {
        File(excludePlguinRootDir, "unzipaar")
    }

    private val unZipJarFile by lazy {
        File(excludePlguinRootDir, "unzipjar")
    }

    /**
     * 过滤之后生成的aar包的路径
     */
    private val excludeAarFile by lazy {
        File(excludePlguinRootDir, "excludeaar")
    }

    /**
     * 过滤之后生成jar包的路径
     */
    private val excludeJarFile by lazy {
        File(excludePlguinRootDir, "excludeJar")
    }

    override fun apply(project: Project) {
        this.project = project
        extParam = project.extensions.create("excludePluginExt", ExcludeParamExtension::class.java, project)

        project.afterEvaluate {
            printlnExcludeMsg()
            createExclueAarTask(extParam)
            createExcludeJarTask(extParam)
        }
    }

    private fun createExclueAarTask(extension: ExcludeParamExtension) {
        // each(Closure action)、all(Closure action)，但是一般我们都会用 all(...) 来进行容器的迭代。
        // all(...) 迭代方法的特别之处是，不管是容器内已存在的元素，还是后续任何时刻加进去的元素，都会进行遍历。
        extension.aars.all {
            judeExtParam(it)
            val deleteDirsTask = createDeleteDirs(it)
            val unZipAarTask = createdUnZipAarTask(it)
            val unZipJarTask = createUnZipJarTask(it)
            val deleteJarTask = createDeleteJars(it)
            val zipJarTask = createZipJar(it)
            val zipAarTask = createZipAar(it)

            unZipAarTask.dependsOn(deleteDirsTask)
            unZipJarTask.dependsOn(unZipAarTask)
            deleteJarTask.dependsOn(unZipJarTask)
            zipJarTask.dependsOn(deleteJarTask)
            zipAarTask.dependsOn(zipJarTask)

            if (extension.autoDependencies) {
                project.dependencies.run {
                    implementation(project.files(File(zipAarTask.destinationDir, zipAarTask.archiveName)))
                }
            }
        }
    }

    private fun createExcludeJarTask(extension: ExcludeParamExtension) {
        extension.jars.all {
            judeExtParam(it)
            val deleteDirsTask = createDeleteDirs(it)
            val unZipJarTask = createUnZipJarTask(it)
            val zipJarTask = createZipJar(it)

            unZipJarTask.dependsOn(deleteDirsTask)
            zipJarTask.dependsOn(unZipJarTask)

            (project.tasks.getByName("unzipJar_${it.name?.trim()}") as? AbstractCopyTask)?.from(project.zipTree(it.path))

            if (extension.autoDependencies) {
                project.dependencies.run {
                    implementation(project.files(File(zipJarTask.destinationDir, zipJarTask.archiveName)))
                }
            }
        }
    }

    /**
     * 校验参数
     *
     * @param param
     */
    private fun judeExtParam(param: JarExculdeParam) {
        if (!param.name.isNullOrEmpty()) {
            require(!param.path.isNullOrEmpty()) { "path is necessary" }
        }
    }

    /**
     * 创建解压aar包的任务
     *
     * @param extParam aar参数
     */
    private fun createdUnZipAarTask(extParam: AarExculdeParam) =
            project.task<Copy>("unZipAar_${extParam.name?.trim()}") {
                //解压aar后存放文件的目录
                val unZipAarPath = File(unZipAarFile, extParam.name)
                from(project.zipTree(File(extParam.path)))
                into(unZipAarPath)

                doLast {
                    val jarFiles = mutableSetOf<File>()

                    //如果解压的aar包存在就找到它下面的所有jar
                    if (unZipAarPath.exists()) {
                        unZipAarPath.walk().filter {
                            it.extension == "jar"
                        }.run {
                            jarFiles.addAll(this)
                        }
                    }

                    (project.tasks.getByName("unzipJar_${extParam.name?.trim()}") as? AbstractCopyTask)?.from(
                            jarFiles.map {
                                project.zipTree(it)
                            })

                    (project.tasks.getByName("deleteJars_${extParam.name?.trim()}") as? Delete)?.delete(jarFiles)
                }
            }

    /**
     * 创建解压jar包的任务
     *
     * 注意：from 参数 动态 设置
     *
     * @param extParam
     */
    private fun createUnZipJarTask(extParam: JarExculdeParam) =
            project.task<Copy>("unzipJar_${extParam.name?.trim()}") {
                into(File(unZipJarFile, extParam.name))
            }


    /**
     * 创建删除Jar的任务
     */
    private fun createDeleteJars(extParam: JarExculdeParam) =
            project.task<Delete>("deleteJars_${extParam.name?.trim()}") {

            }

    /**
     * 创建删除目录的任务
     */
    private fun createDeleteDirs(extParam: JarExculdeParam) =
            project.task<Delete>("deleteDirs_${extParam.name?.trim()}") {
                if (extParam is AarExculdeParam) {
                    delete(File(unZipAarFile, extParam.name), File(unZipJarFile, extParam.name), File(excludeAarFile, "${getExcludeAarName(extParam)}.aar"))
                } else {
                    delete(File(unZipJarFile, extParam.name), File(excludeJarFile, "${getExcludeJarName(extParam)}.jar"))
                }
            }


    /**
     * 创建压缩jar的任务
     *
     * @param extParam 过滤参数
     *
     * 如果 extParsm is AarExculdeParam 任务名为 zipJar_extParam.name
     * 如果 extParam is JarExculdeParam 任务名为 excludeJar_extParam.name
     *
     */
    private fun createZipJar(extParam: JarExculdeParam) =
            project.task<Jar>(if (extParam !is AarExculdeParam) "excludeJar_${extParam.name?.trim()}" else "zipJar_${extParam.name?.trim()}") {
                if (extParam !is AarExculdeParam) {
                    group = "excludePlugin"
                    description = "${extParam.name} exclude ${extParam.excludePackages} and ${extParam.excludeClasses}"
                    baseName = getExcludeJarName(extParam)
                    destinationDir = excludeJarFile
                } else {
                    //文件名
                    baseName = "classes"
                    destinationDir = File(unZipAarFile, extParam.name)
                }

                //需要打包的资源所在的路径集和
                from(File(unZipJarFile, extParam.name))
                //去除路径集下部分的资源
                exclude(extParam.excludeClassRegex)
                exclude(extParam.excludePackageRegex)
                //整理输出的 Jar 文件后缀名
                extension = "jar"
            }


    /**
     * 创建压缩aar的任务
     *
     * @param extParam 过滤参数
     *
     *
     */
    private fun createZipAar(extParam: AarExculdeParam) =
            project.task<Zip>("excludeAar_${extParam.name?.trim()}") {
                group = "excludePlugin"
                description = "${extParam.name} exclude ${extParam.excludePackages},${extParam.excludeClasses},${extParam.excludeSoRegex}"

                baseName = getExcludeAarName(extParam)
                extension = "aar"
                from(File(unZipAarFile, extParam.name))
                exclude(extParam.excludeSoRegex)
                destinationDir = excludeAarFile
            }


    /**
     * 获取过滤之后的aar包名称
     */
    private fun getExcludeAarName(aar: AarExculdeParam) = "exclude_${aar.name}"

    /**
     * 获取过滤之后的jar包名称
     */
    private fun getExcludeJarName(jar: JarExculdeParam) = "exclude_${jar.name}"

    /**
     * 打印信息
     */
    private fun printlnExcludeMsg() {
        project.task("printlnExcludeMsg") {
            it.group = "excludePlugin"
            it.description = "println exclude message"

            it.doLast {
                println("----------------------------aar-----------------------")
                extParam.aars.all { aar ->
                    println("name:${aar.name}")
                    println("path:${aar.path}\n")
                    println("excludePackages:${aar.excludePackages.fold("") { acc, item ->
                        "$acc\n$item"
                    }
                    }\n")
                    println("excludeClasses:${aar.excludeClasses.fold("") { acc, item ->
                        "$acc\n$item"
                    }
                    }\n")
                    println("excludeSos:${aar.excludeSos.fold("") { acc, item ->
                        "$acc\n$item"
                    }
                    }\n")
                }

                println("----------------------------jar-----------------------")
                extParam.jars.all { jar ->
                    println("name:${jar.name}")
                    println("path:${jar.path}\n")
                    println("excludePackages:${jar.excludePackages.fold("") { acc, item ->
                        "$acc\n$item"
                    }
                    }\n")
                    println("excludeClasses:${jar.excludeClasses.fold("") { acc, item ->
                        "$acc\n$item"
                    }
                    }\n")
                }
            }
        }
    }
}