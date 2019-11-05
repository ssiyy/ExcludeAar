package exclude.gradle.plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.Task
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
inline fun <reified type : Task> Project.task(name: String, noinline configuration: type.() -> Unit) =
        task(name, type::class, configuration)

fun <T : Task> Project.task(name: String, type: KClass<T>, configuration: T.() -> Unit) =
        tasks.create(name, type.java, configuration)