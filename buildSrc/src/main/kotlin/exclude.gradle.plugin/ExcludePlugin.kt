package exclude.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

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

    override fun apply(project: Project) {
        this.project = project


        println("wo zhi shi ce shi yi xia")
    }

}