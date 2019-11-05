apply(mapOf( "plugin" to "com.android.library"))


/*plugins {
    id("ExcludePlugin")
}*/

//apply(mapOf( "plugin" to "ExcludePlugin"))

class ExcludePlugin : Plugin<Project> {

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project

        project.afterEvaluate {

            createMakJarTask()
        }
    }

    private fun createMakJarTask() {
        project.task<Copy>("makeJar") {
            group = "Siy"
            description = "生成一个jar"
            from("${project.buildDir.absolutePath}\\intermediates\\packaged-classes\\release")
            into("${project.buildDir.absolutePath}\\outputs\\libs")
            include("classes.jar")
            rename("classes.jar", "BaiduLBS_Android_release.jar")
        }.dependsOn(project.tasks.getByName("build"))
    }
}