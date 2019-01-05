package com.tmodloader.modgen.service

import com.tmodloader.modgen.model.GenerationModel
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files

@Service
class ValidatorService {

    lateinit var validationError: String

    fun validateGenerationModel(generationModel: GenerationModel): Boolean {
        if (!validateOutputPath(generationModel.outputPath)) return false
        if (!validateModName(generationModel.modName)) return false
        if (!validateModDisplayName(generationModel.modDisplayName)) return false
        if (!validateModVersion(generationModel.modVersion)) return false
        if (!validateAuthor(generationModel.modAuthor)) return false
        if (!validateLanguageVersion(generationModel.modLanguageVersion)) return false
        return true
    }

    private fun validateOutputPath(path : String) : Boolean {
        if (path.isBlank()) {
            validationError = "Output path may not be blank"
            return false
        }

        var filePath = File(path)
        if (!filePath.isDirectory) {
            filePath = filePath.parentFile
        }

        if (!filePath.exists()){
            if (!Files.isWritable(filePath.toPath())) {
                validationError = "Output path does not exist and is also not writeable"
                return false
            }
        }
        else if (!Files.isReadable(filePath.toPath())) {
            validationError = "Output path is nog readable"
            return false
        }

        return true
    }

    private fun validateModName(name: String): Boolean {
        if (name.isBlank()) {
            validationError = "Mod name may not be blank"
            return false
        }
        val regex = Regex("^[a-zA-Z]+\$", RegexOption.IGNORE_CASE)
        if (!regex.matches(name)) {
            validationError = "Mod name may not contain special characters"
            return false
        }
        return true
    }

    private fun validateModDisplayName(name: String): Boolean {
        if (name.isBlank()) {
            validationError = "Mod display name may not be blank"
            return false
        }
        return true
    }

    private fun validateModVersion(version: String): Boolean {
        if (version.isBlank()) {
            validationError = "Mod version may not be blank"
            return false
        }
        val split = version.split('.', limit = 4)
        if (split.size > 4) {
            validationError = "Mod version must follow MAJOR.MINOR.BUILD.REVISION format"
            return false
        } else {
            split.forEach {
                if (!StringUtils.isNumeric(it)) {
                    validationError = "Mod version must only consist of numbers"
                    return false
                }
            }
        }
        return true
    }

    private fun validateAuthor(author : String) : Boolean {
        if (author.isBlank()) {
            validationError = "Mod author may not be blank"
            return false
        }
        if (author.length > 255) {
            validationError = "Mod author length is too long. May not exceed 255 characters"
            return false
        }
        return true
    }

    private fun validateLanguageVersion(languageVersion : String) : Boolean {
        if (languageVersion.isBlank()) {
            validationError = "Language version may not be blank"
            return false
        }
        if (!StringUtils.isNumeric(languageVersion) || languageVersion.length > 1
            || languageVersion != "4" && languageVersion != "6") {
            validationError = "Language version must be either 4 or 6"
            return false
        }
        return true
    }
}