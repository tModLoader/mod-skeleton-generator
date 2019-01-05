package com.tmodloader.modgen.config

import com.tmodloader.modgen.KotlinApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.prefs.Preferences


@Configuration
class PreferencesConfig {
    @Bean(name = ["AppPreferences"])
    fun getPreferences(): Preferences {
        return Preferences.userRoot().node(KotlinApplication::class.java.name)
    }
}