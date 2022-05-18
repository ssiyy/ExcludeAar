package exclude.gradle.plugin.type

import exclude.gradle.plugin.*
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Jar
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/5/16
 */
open class ExcludeJarType(private val project: Project) {
    val extension: ExcludeExtension = project.extensions.findByType(ExcludeExtension::class.java)!!

    /**
     * 插件生存的文件都存放在这个目录下面
     */
    protected val exPluginRootDir: File
        get() {
            val rootFile = File(project.buildDir, "ex_plugin")
            return ensureFileExits(rootFile)
        }

    /**
     * 解压aar之后文件存放的目录
     */
    protected val outputDir: File
        get() {
            val tempAar = File(exPluginRootDir, "output")
            return ensureFileExits(tempAar)
        }

    /**
     * 解压jar之后文件存放的目录
     */
    protected val tempJarDir: File
        get() {
            val tempJar = File(exPluginRootDir, "temp_jar")
            return ensureFileExits(tempJar)
        }


    /**
     * 文件存在就直接返回，如果不存在就创建
     */
    protected fun ensureFileExits(file: File) = if (file.exists()) {
        file
    } else {
        if (file.mkdirs()) {
            file
        } else {
            throw AssertionError("${file.path}创建失败")
        }
    }

    init {
        project.afterEvaluate {
            extension.jarsParams.all {
                createTaskChain(it)
                implementation(it)
            }
        }
    }

    private fun implementation(extension: JarExcludeParam) {
        if (extension.implementation) {
            val task =
                (project.tasks.getByName("ex_jar_${extension.name?.trim()}") as? AbstractArchiveTask)
            val jarFile = File(task?.destinationDir,task!!.archiveName)
            if (jarFile.exists()) {
                project.dependencies.run {
                    implementation(
                        project.files(jarFile)
                    )
                }
            }
        }
    }

    /**
     * 创建任务链
     */
    private fun createTaskChain(extension: JarExcludeParam) =
        createUnZipJar(extension).apply {
            from(project.zipTree(extension.path!!))
        }.then(createExJar(extension).apply {
            group = "excludePlugin"
        })

    protected fun createUnZipJar(extension: JarExcludeParam) =
        project.task<Copy>("unZip_jar_${extension.name?.trim()}") {
            into(File(tempJarDir, extension.name!!))
        }

    protected fun createExJar(extension: JarExcludeParam) =
        project.task<Jar>("ex_jar_${extension.name?.trim()}") {
            //文件名
            baseName = "classes"
            //输出的目录
            destinationDir = File(outputDir, extension.name!!)
            //需要打包的资源所在的路径集和
            from(File(tempJarDir, extension.name!!))
            //需要排除的类
            exclude(extension.excludeClasses)
            //需要排除的包名
            exclude(extension.excludePackages)
            setExtension("jar")
        }
}