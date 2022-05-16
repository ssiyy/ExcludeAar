package exclude.gradle.plugin.type

import com.android.tools.r8.internal.ex
import com.android.tools.r8.internal.it
import exclude.gradle.plugin.*
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Zip
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
        val tempAar = File(exPluginRootDir, "temp_aar")
        ensureFileExits(tempAar)
    }

    /**
     * 解压jar之后文件存放的目录
     */
    private val tempJarDir by lazy {
        val tempJar = File(exPluginRootDir, "temp_jar")
        ensureFileExits(tempJar)
    }

    /**
     * 过滤aar之后文件存放的目录
     */
    private val tempExAarDir by lazy {
        val tempExAar = File(exPluginRootDir, "temp_ex_aar")
        ensureFileExits(tempExAar)
    }

    /**
     * 过滤jar之后文件存放的目录
     */
    private val tempExJarDir by lazy {
        val tempExJar = File(exPluginRootDir, "temp_ex_jar")
        ensureFileExits(tempExJar)
    }

    /**
     * 过滤完成之后aar包存放的目录
     */
    private val exAarFile by lazy {
        val exAarFile = File(exPluginRootDir, "ex_aar_file")
        ensureFileExits(exAarFile)
    }

    /**
     * 过滤完成之后jar包存放的目录
     */
    private val exJarFile by lazy {
        val exJarFile = File(exPluginRootDir, "ex_jar_file")
        ensureFileExits(exJarFile)
    }


    init {
        val extension = project.extensions.findByType(ExcludeExtension::class.java)

        project.afterEvaluate { _ ->
            // each(Closure action)、all(Closure action)，但是一般我们都会用 all(...) 来进行容器的迭代。
            // all(...) 迭代方法的特别之处是，不管是容器内已存在的元素，还是后续任何时刻加进去的元素，都会进行遍历。
            extension?.aarsParams?.all {
                System.err.println("aar name:" + it.name)
              val aarTask =   createTaskChain(it)

            if (extension.autoDependence) {
                project.dependencies.run {
                    implementation(
                        project.files(
                           File(aarTask.destinationDir,aarTask.archiveName)
                        )
                    )
                }
            }}
        }
    }

    /**
     * 创建任务链
     */
    private fun createTaskChain(extension: AarExcludeParam) =
        createDeleteDirs(extension)
            .then(createUnZipAar(extension))
            .then(createUnZipJar(extension))
            .then(createDeleteJars(extension))
            .then(createExJar(extension))
            .then(createExAar(extension))


    /**
     * 1、解压Aar
     * 2、解压aar中的jar
     * 3、删除已经被解压的jar
     */
    private fun createUnZipAar(extension: AarExcludeParam) =
        project.task<Copy>("unZip_aar_${extension.name?.trim()}") {
            group = "excludePlugin"

            val unZipAarFile = File(tempAarDir, extension.name!!)
            //解压之后aar的存放目录
            from(project.zipTree(File(extension.path!!)))
            into(unZipAarFile)

            doLast {
                val jarFiles = mutableListOf<File>()

                if (unZipAarFile.exists()) {
                    unZipAarFile.walk().filter {
                        it.extension == "jar"
                    }.run {
                        jarFiles.addAll(this)
                    }
                }


                (project.tasks.getByName("unZip_jar_${extension.name?.trim()}") as? AbstractCopyTask)?.from(
                    jarFiles.map {
                        project.zipTree(it)
                    })

                (project.tasks.getByName("delete_jars_${extension.name?.trim()}") as? Delete)?.delete(
                    jarFiles
                )


                System.err.println("${name}我执行了：")

            }

        }


    private fun createUnZipJar(extension: AarExcludeParam) =
        project.task<Copy>("unZip_jar_${extension.name?.trim()}") {
            group = "excludePlugin"
            into(File(tempJarDir, extension.name!!))

            doLast {
                System.err.println("${name}我执行了：")
            }
        }

    /**
     * 创建删除Jar的任务
     */
    private fun createDeleteJars(extension: AarExcludeParam) =
        project.task<Delete>("delete_jars_${extension.name?.trim()}") {
            group = "excludePlugin"

            doLast {
                System.err.println("${name}我执行了：" + this.delete)
            }
        }


    private fun createExJar(extension: AarExcludeParam) =
        project.task<Jar>("ex_jar_${extension.name?.trim()}") {
            group = "excludePlugin"

            //文件名
            baseName = "classes"
            //输出的目录
            destinationDir = File(tempAarDir, extension.name!!)
            //需要打包的资源所在的路径集和
            from(File(tempJarDir, extension.name!!))
            //需要排除的类
            exclude(extension.excludeClasses)
            //需要排除的包名
            exclude(extension.excludePackages)
            setExtension("jar")

            doLast {
                System.err.println("${name}我执行了：")
            }
        }

    private fun createExAar(extension: AarExcludeParam) =
        project.task<Zip>("ex_aar_${extension.name?.trim()}") {

            group = "excludePlugin"
            description =
                "${extension.name} exclude ${extension.excludePackages},${extension.excludeClasses},${extension.excludeSos}"

            baseName = "ex_${extension.name}"
            setExtension("aar")
            from(File(tempAarDir, extension.name!!))

            //需要排除的so
            exclude(extension.excludeSos)
            destinationDir = exAarFile

            doLast {
                System.err.println("${name}我执行了：")
            }
        }


    /**
     * 创建一个删除缓存目录的task
     */
    private fun createDeleteDirs(extension: AarExcludeParam) =
        project.task<Delete>("delete_dirs_${extension.name?.trim()}") {
            group = "excludePlugin"
            delete(
                File(tempAarDir, extension.name!!),
                File(tempJarDir, extension.name!!),
                File(tempExAarDir, extension.name!!),
                File(tempExJarDir, extension.name!!)
            )

            doLast {
                System.err.println("${name}我执行了：")
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