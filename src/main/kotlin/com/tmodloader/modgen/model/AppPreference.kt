package com.tmodloader.modgen.model

import com.tmodloader.modgen.ext.Spring
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.prefs.Preferences
import kotlin.reflect.full.isSubclassOf

sealed class AppPreference {

    private var preferences: Preferences = Spring.getBean(Preferences::class, "AppPreferences")

    companion object {
        fun clearAllPreferences() {
            getAllPreferences().forEach {
                it.update(it.default)
            }
        }

        fun initAllPreferences() {
            getAllPreferences().forEach {
                //pushes the initial value onto the subject
                //default if empty (notifies subscribers for the first time)
                it.update(it.get())
            }
        }

        private fun getAllPreferences(): Iterable<AppPreference> {
            return AppPreference::class.nestedClasses
                    .filter { it.isFinal && it.isSubclassOf(AppPreference::class) }
                    .map { it.objectInstance as AppPreference }
        }

    }

    object OUTPUT_PATH : AppPreference() {
        override val key = "OutputPath"
        override var default = ""
    }

    fun update(value: String) {
        preferences.put(key, value)
        subject.onNext(value)
    }

    fun get(): String {
        return preferences.get(key, default)
    }

    abstract val key: String
    open var default: String = ""
    private var subject: BehaviorSubject<String> = BehaviorSubject.create()
    val observable: Observable<String> = subject.hide()
}