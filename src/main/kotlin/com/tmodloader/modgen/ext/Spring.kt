package com.tmodloader.modgen.ext

import com.tmodloader.modgen.KotlinApplication
import kotlin.reflect.KClass

class Spring {
    companion object {
        fun <T : Any> getBean(type : KClass<T>) : T = KotlinApplication.applicationContext.getBean(type.java)
        fun <T : Any> getBean(type : KClass<T>, name : String) : T = KotlinApplication.applicationContext.getBean(type.java, name)
    }
}