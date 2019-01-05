package com.tmodloader.modgen

import com.tmodloader.modgen.ext.Spring
import com.tmodloader.modgen.model.AppPreference
import com.tmodloader.modgen.view.RootView
import javafx.scene.image.Image
import javafx.stage.Stage
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.singleAssign
import kotlin.reflect.KClass

@SpringBootApplication
class KotlinApplication : App(RootView::class) {

    override val primaryView = RootView::class

    companion object {
        var applicationContext: ApplicationContext by singleAssign()
    }

    override fun init() {
        super.init()

        val spring = SpringApplication(KotlinApplication::class.java)
        spring.webApplicationType = WebApplicationType.NONE
        applicationContext = spring.run()

        /*
            The following code facilitates DI by Spring
         */
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T = Spring.getBean(type)
            override fun <T : Any> getInstance(type: KClass<T>, name: String): T = Spring.getBean(type, name)
        }

        AppPreference.initAllPreferences()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.icons += Image(javaClass.classLoader.getResource("icon.png").toExternalForm())
        stage.isResizable = false
    }
}