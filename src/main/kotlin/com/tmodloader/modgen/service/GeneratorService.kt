package com.tmodloader.modgen.service

import com.tmodloader.modgen.KotlinApplication
import com.tmodloader.modgen.model.GenerationModel
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Paths

@Service
class GeneratorService {

    var generationException: String? = null

    companion object {

        object TEMPLATES {
            const val MOD_TEMPLATE = "mod.template"
            const val CSPROJ_TEMPLATE = "csproj.template"
            const val CSPROJ_USER_TEMPLATE = "csproj_user.template"
        }

        object FILES {
            const val BUILD = "build.txt"
            const val DESCRIPTION = "description.txt"
            const val GIT_IGNORE = ".gitignore"
            val MAIN: String
                get() : String {
                    return "${GENERATION.MODEL.modName}.cs"
                }
            val CSPROJ: String
                get() : String {
                    return "${GENERATION.MODEL.modName}.csproj"
                }
            val CSPROJ_USER: String
                get() : String {
                    return "${GENERATION.MODEL.modName}.csproj.user"
                }
        }

        object GENERATION {
            lateinit var PATH: String
            lateinit var MODEL: GenerationModel
        }
    }

    private fun getTemplate(resource: String): String {
        val url = KotlinApplication::class.java.classLoader.getResource(resource)
        if (url.protocol != "file") {
            // TODO unknown problem
            return ""
        }
        val file = File(url.toURI())
        if (!file.canRead()) {
            // TODO intermediate read problem, notify
            return ""
        }
        return file.readText()
    }

    private fun getFileWriter(path: String, more: String): PrintWriter {
        return PrintWriter(FileWriter(Paths.get(path, GENERATION.MODEL.modName, more).toString(), false))
    }

    fun writeBuildFile() {
        getFileWriter(GENERATION.PATH, FILES.BUILD).use {
            it.println("author = ${GENERATION.MODEL.modAuthor}")
            it.println("version = ${GENERATION.MODEL.modVersion}")
            it.println("displayName = ${GENERATION.MODEL.modDisplayName}")
            it.println("languageVersion = ${GENERATION.MODEL.modLanguageVersion}")
        }
    }

    fun writeDescriptionFile() {
        getFileWriter(GENERATION.PATH, FILES.DESCRIPTION).use {
            it.println("${GENERATION.MODEL.modDisplayName} is a pretty cool mod, it does.. this. Modify this file with a description of your mod.")
        }
    }

    fun writeMainModFile() {
        getFileWriter(GENERATION.PATH, FILES.MAIN).use {
            it.write(getTemplate(TEMPLATES.MOD_TEMPLATE)
                    .replace("[MOD_NAME]", GENERATION.MODEL.modName))
        }
    }

    fun writeCsprojFile() {
        getFileWriter(GENERATION.PATH, FILES.CSPROJ).use {
            it.write(getTemplate(TEMPLATES.CSPROJ_TEMPLATE)
                    .replace("[MOD_NAME]", GENERATION.MODEL.modName))
        }
    }

    fun writeCsprojUserFile() {
        getFileWriter(GENERATION.PATH, FILES.CSPROJ_USER).use {
            it.write(getTemplate(TEMPLATES.CSPROJ_USER_TEMPLATE))
        }
    }

    fun writeGitIgnoreFile() {
        getFileWriter(GENERATION.PATH, FILES.GIT_IGNORE).use {
            if (GENERATION.MODEL.modBuildPropertiesGitIgnore.isNotBlank()) {
                GENERATION.MODEL.modBuildPropertiesGitIgnore.split(Regex(""",\s*"""))
                        .forEach { line ->
                            it.println(line)
                        }
            } else {
                it.println("*.csproj")
                it.println("*.user")
                it.println("*.sln")
                it.println("bin/")
                it.println("obj/")
            }
        }
    }


    fun generateModSkeleton(generationModel: GenerationModel): Boolean {

        GENERATION.MODEL = generationModel
        GENERATION.PATH = generationModel.outputPath

        val path = Paths.get(generationModel.outputPath, generationModel.modName)
        File(path.toUri()).mkdir()

        writeBuildFile()
        writeDescriptionFile()
        writeMainModFile()
        writeCsprojFile()
        writeCsprojUserFile()
        writeGitIgnoreFile()

        return true
    }
}