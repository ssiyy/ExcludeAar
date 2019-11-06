package exclude.gradle.plugin

import groovy.util.XmlSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import java.io.File


open class ExcludeAArJar {

    var inputPath: String? = null


    var outputPath: String? = null


    var exclude: Array<String>? = null
}


/**
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

    private val excludeAArJarExt by lazy {
        project.extensions.create("excludeAArJar", ExcludeAArJar::class.java)
    }

    /**
     * 存放输出介质
     */
    private val outputFile by lazy {
        File(excludeAArJarExt.outputPath)
    }

    /**
     * 解压aar包得到文件存放的目录
     */
    private val unZipAarFile by lazy {
        File(outputFile,"unzipaar")
    }


    /**
     * 解压jar包得到文件存放的目录
     */
    private val unZipJarFile by lazy {
        File(outputFile,"unzipjar")
    }


    override fun apply(project: Project) {
        this.project = project

        project.afterEvaluate {
            createSomeTasks(excludeAArJarExt)
        }
    }






    private fun createSomeTasks(fd: ExcludeAArJar) {
        project.task<Copy>("unZipAar"){

        }

    }

    private fun createMakJarTask() {

        /* project.task<Copy>("makeJar") {
             group = "Siy"
             description = "生成一个jar"
             from("${project.buildDir.absolutePath}\\intermediates\\packaged-classes\\release")
             into("${project.buildDir.absolutePath}\\outputs\\libs")
             include("classes.jar")
             rename("classes.jar", "BaiduLBS_Android_release.jar")
         }.dependsOn(project.tasks.getByName("build"))*/


        /*   val firstLetterUpper = getFirstLetterUpper(flavorName)

         project.task<Jar>("make${firstLetterUpper}Jar") {
             group = "Siy"
             description = "生成一个${flavorName}"
             //需要打包的资源所在的路径集和
             from("${project.buildDir.absolutePath}\\intermediates\\javac\\release")

             //去除路径集下部分的资源
             exclude("${getPackagePath()}\\BuildConfig.class",
                     "${getPackagePath()}\\BuildConfig\$*.class",
                     "**\\R.class","**\\R\$*.class")
             //只导入资源路径集下的部分资源
             include("\\**\\*.class")

             //整理输出的 Jar 文件后缀名
             extension = "jar"

             //最终的 Jar 文件名......如果没设置，默认为 [baseName]-[appendix]-[version]-[classifier].[extension]
             archiveName ="${project.name}_${project.android.defaultConfig.versionName}.${extension}"
         }.dependsOn(project.tasks.getByName("compile${firstLetterUpper}ReleaseJavaWithJavac"))*/

    }

    /**
     * 获取字符串的首字母大写
     * @param str
     * @return
     */
    private fun getFirstLetterUpper(str: String?) = if (str.isNullOrEmpty()) {
        ""
    } else {
        "${str[0].toUpperCase()}${str.substring(1)}"
    }


    /**
     * 获取工程的包名
     */
    private fun getPackageName(): String {
        val androidManifest = project.android.sourceSets.getByName("main").manifest.srcFile
        val xmlSlurper = XmlSlurper().parse(androidManifest)
        return xmlSlurper.getProperty("@package").toString()
    }


    /**
     * 获取工程包路径
     */
    private fun getPackagePath(): String {
        val packageName = getPackageName()
        return packageName.replace(".", "\\")
    }

}