package exclude.gradle.plugin.type

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * 用来过滤aar
 */
class ExcludeAarType : DefaultTask(){


    /**
     *在gradle执行阶段执行
     */
    @TaskAction
    fun doAction(){

    }
}