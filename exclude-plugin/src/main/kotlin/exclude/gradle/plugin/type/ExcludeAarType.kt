package exclude.gradle.plugin.type

import exclude.gradle.plugin.AarExculdeParam
import exclude.gradle.plugin.ExcludeParamExtension
import exclude.gradle.plugin.task
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskAction
import java.io.File


/**
 * 用来过滤aar
 */
class ExcludeAarType(private val project: Project) {
    /**
     * 插件生存的文件都存放在这个目录下面
     */
    private val exPluginRootDir by lazy {
        val rootFile = File(project.buildDir, "ex_plugin")
        ensureFileExits(rootFile)
    }

    /**
     * 解压aar之后文件存放的目录
     */
    private val tempAarDir by lazy {
        val tempAar = File(project.buildDir, "temp_aar")
        ensureFileExits(tempAar)
    }

    /**
     * 解压jar之后文件存放的目录
     */
    private val tempJarDir by lazy {
        val tempJar = File(project.buildDir, "temp_jar")
        ensureFileExits(tempJar)
    }

    /**
     * 过滤aar之后文件存放的目录
     */
    private val tempExAarDir by lazy {
        val tempExAar = File(project.buildDir, "temp_ex_aar")
        ensureFileExits(tempExAar)
    }

    /**
     * 过滤jar之后文件存放的目录
     */
    private val tempExJarDir by lazy {
        val tempExJar = File(project.buildDir, "temp_ex_jar")
        ensureFileExits(tempExJar)
    }


    init {
        project.extensions.findByType(ExcludeParamExtension::class.java)?.aars?.all {

        }
    }


    private fun createDeleteDirs(extension: AarExculdeParam) {
        project.task<Delete>("aa") {
            delete(
                File(tempAarDir, extension.name!!),
                File(tempJarDir, extension.name!!),
                File(tempExAarDir, extension.name!!),
                File(tempExJarDir, extension.name!!)
            )
        }
    }

    /**
     * 文件存在就直接返回，如果不存在就创建
     */
    private fun ensureFileExits(file: File) = if (file.exists()) {
        file
    } else {
        if (file.mkdir()) {
            file
        } else {
            project.buildDir
        }
    }
}