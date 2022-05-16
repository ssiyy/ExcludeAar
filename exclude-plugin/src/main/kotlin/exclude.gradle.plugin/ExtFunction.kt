package exclude.gradle.plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import kotlin.reflect.KClass


/**
 * Created by Siy on 2019/11/05.
 *
 * @author Siy
 */


val Project.`android`: LibraryExtension
    get() = (this as ExtensionAware).extensions.getByName("android") as LibraryExtension


/**
 * Creates a [Task] with the given [name] and [type], configures it with the given [configuration] action,
 * and adds it to this project tasks container.
 */
inline fun <reified type : Task> Project.task(
    name: String,
    noinline configuration: type.() -> Unit
) =
    task(name, type::class, configuration)

fun <T : Task> Project.task(name: String, type: KClass<T>, configuration: T.() -> Unit) =
    tasks.create(name, type.java, configuration)


/**
 * Adds a dependency to the 'implementation' configuration.
 *
 * @param dependencyNotation notation for the dependency to be added.
 * @return The dependency.
 *
 * @see [DependencyHandler.add]
 */
fun DependencyHandler.`implementation`(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)

/**
 * Kotlin extension function for [org.gradle.api.artifacts.dsl.DependencyHandler.project].
 *
 * @see org.gradle.api.artifacts.dsl.DependencyHandler.project
 */
inline fun DependencyHandler.`project`(vararg `notation`: Pair<String, Any?>): Dependency =
    `project`(mapOf(*`notation`))


/**
 * 可以用来链式执行
 */
fun Task.dependOn(task: Task): Task {
    this.dependsOn(task)
    return task
}


/**
 * 顺序执行链式调用
 */
fun <F : Task> Task.then(task: F): F {
    task.dependsOn(this)
    return task
}