package exclude.gradle.plugin

import exclude.gradle.plugin.type.ExcludeAarType
import exclude.gradle.plugin.type.ExcludeJarType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

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

    override fun apply(project: Project) {
        this.project = project
        project.extensions.create("excludePluginExt", ExcludeExtension::class.java, project)
        ExcludeAarType(project)
        ExcludeJarType(project)
        project.afterEvaluate {
            printlnExcludeMsg()

            createExPluginTasks()
        }
    }

    /**
     * 所有生产ex aar jar task
     */
    private fun createExPluginTasks() = project.task("excludePluginTask") {
        it.group = "excludePlugin"

        val tasks = mutableListOf<Task>()
        val aarTask = project.tasks.findByName("excludeAarPluginTask")
        if (aarTask != null) {
            tasks.add(aarTask)
        }
        val jarTask = project.tasks.findByName("excludeJarPluginTask")
        if (jarTask != null) {
            tasks.add(jarTask)
        }

        if (tasks.isNotEmpty()) {
            it.setDependsOn(tasks)
        }

        it.doFirst {
            println("exclude plugin 开始执行")
        }
    }


    /**
     * 打印信息
     */
    private fun printlnExcludeMsg() {
        val extParam = project.extensions.findByType(ExcludeExtension::class.java)!!
        project.task("printlnExcludeMsg") {
            it.group = "excludePlugin"
            it.description = "println exclude message"

            it.doLast {
                println("----------------------------aar-----------------------")
                extParam.aarsParams.all { aar ->
                    println("name:${aar.name}")
                    println("path:${aar.path}")
                    println("implementation:${aar.implementation}\n")
                    println("excludePackages:${
                        aar.excludePackages.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")
                    println("excludeClasses:${
                        aar.excludeClasses.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")
                    println("excludeSos:${
                        aar.excludeSos.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")

                    println("excludeSoAbis:${
                        aar.excludeSoAbis.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")
                }

                println("----------------------------jar-----------------------")
                extParam.jarsParams.all { jar ->
                    println("name:${jar.name}")
                    println("path:${jar.path}")
                    println("implementation:${jar.implementation}\n")
                    println("excludePackages:${
                        jar.excludePackages.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")
                    println("excludeClasses:${
                        jar.excludeClasses.fold("") { acc, item ->
                            "$acc\n$item"
                        }
                    }\n")
                }
            }
        }
    }
}