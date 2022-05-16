package exclude.gradle.plugin.type

import exclude.gradle.plugin.*
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
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
    protected val exPluginRootDir by lazy {
        val rootFile = File(project.buildDir, "ex_plugin")
        ensureFileExits(rootFile)
    }

    /**
     * 解压aar之后文件存放的目录
     */
    protected val outputDir by lazy {
        val tempAar = File(exPluginRootDir, "output")
        ensureFileExits(tempAar)
    }

    /**
     * 解压jar之后文件存放的目录
     */
    protected val tempJarDir by lazy {
        val tempJar = File(exPluginRootDir, "temp_jar")
        ensureFileExits(tempJar)
    }


    /**
     * 文件存在就直接返回，如果不存在就创建
     */
    protected fun ensureFileExits(file: File) = if (file.exists()) {
        file
    } else {
        if (file.mkdir()) {
            file
        } else {
            throw AssertionError("${file.path}创建失败")
        }
    }

    init {
        project.afterEvaluate {
            extension.jarsParams.all {
                System.err.println("jar name:" + it.name)

                val jarTask = createTaskChain(it)
                if (it.implementation) {
                    project.dependencies.run {
                        implementation(
                            project.files(
                                File(jarTask.destinationDir, jarTask.archiveName)
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * 创建任务链
     */
    private fun createTaskChain(extension: JarExcludeParam) =
        createUnZipJar(extension).from(project.zipTree(extension.path!!))
            .then(createExJar(extension))

    protected fun createUnZipJar(extension: JarExcludeParam) =
        project.task<Copy>("unZip_jar_${extension.name?.trim()}") {
            group = "excludePlugin"
            into(File(tempJarDir, extension.name!!))
        }

    protected fun createExJar(extension: JarExcludeParam) =
        project.task<Jar>("ex_jar_${extension.name?.trim()}") {
            group = "excludePlugin"

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


    /**
     * 创建一个删除缓存目录的task
     */
    protected fun createDeleteTask() =
        project.task<Delete>("clear_temp_dir") {
            group = "excludePlugin"
            delete(tempJarDir)
        }


}