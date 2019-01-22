package com.tmodloader.modgen.service

import com.tmodloader.modgen.model.GenerationModel
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files

enum class ValidationStatus(
        val description: String
) {
    PATH_BLANK("Output path may not be blank"),
    PATH_NON_WRITABLE("Output path does not exist and is also not writeable"),
    PATH_NON_READABLE("Output path is not readable"),
    NAME_BLANK("Mod name may not be blank"),
    NAME_HAS_SPECIAL_CHARS("Mod name may not contain special characters"),
    DISPLAYNAME_BLANK("Mod display name may not be blank"),
    VERSION_BLANK("Mod version may not be blank"),
    VERSION_BAD_FORMAT("Mod version must follow MAJOR.MINOR.BUILD.REVISION format"),
    VERSION_BAD_CHARS("Mod version must only consist of numbers"),
    AUTHOR_BLANK("Mod author may not be blank"),
    AUTHOR_TOO_LONG("Mod author length is too long. May not exceed 255 characters"),
    LANGUAGE_BLANK("Language version may not be blank"),
    LANGUAGE_BAD_FORMAT("Language version must be either 4 or 6"),
    UNKNOWN("Unknown error occurred"),
    SUCCESS("Mod skeleton generated!")
}

@Service
class ValidatorService {

    lateinit var validationStatus: ValidationStatus

    fun validateGenerationModel(generationModel: GenerationModel): Boolean {
        if (!validateOutputPath(generationModel.outputPath)) return false
        if (!validateModName(generationModel.modName)) return false
        if (!validateModDisplayName(generationModel.modDisplayName)) return false
        if (!validateModVersion(generationModel.modVersion)) return false
        if (!validateAuthor(generationModel.modAuthor)) return false
        if (!validateLanguageVersion(generationModel.modLanguageVersion)) return false
        return true
    }

    fun validateOutputPath(path: String): Boolean {
        if (path.isBlank()) {
            validationStatus = ValidationStatus.PATH_BLANK
            return false
        }

        var filePath = File(path)
        if (!filePath.isDirectory && !filePath.isAbsolute) {
            filePath = filePath.parentFile
        }

        if (!filePath.exists()) {
            if (!Files.isWritable(filePath.toPath())) {
                validationStatus = ValidationStatus.PATH_NON_WRITABLE
                return false
            }
        } else if (!Files.isReadable(filePath.toPath())) {
            validationStatus = ValidationStatus.PATH_NON_READABLE
            return false
        }

        return true
    }

    fun validateModName(name: String): Boolean {
        if (name.isBlank()) {
            validationStatus = ValidationStatus.NAME_BLANK
            return false
        }
        val regex = Regex("^[a-zA-Z]+\$", RegexOption.IGNORE_CASE)
        if (!regex.matches(name)) {
            validationStatus = ValidationStatus.NAME_HAS_SPECIAL_CHARS
            return false
        }
        return true
    }

    fun validateModDisplayName(name: String): Boolean {
        if (name.isBlank()) {
            validationStatus = ValidationStatus.DISPLAYNAME_BLANK
            return false
        }
        return true
    }

    fun validateModVersion(version: String): Boolean {
        if (version.isBlank()) {
            validationStatus = ValidationStatus.VERSION_BLANK
            return false
        }
        val split = version.split('.')
        if (split.size > 4) {
            validationStatus = ValidationStatus.VERSION_BAD_FORMAT
            return false
        } else {
            split.forEach {
                if (!StringUtils.isNumeric(it)) {
                    validationStatus = ValidationStatus.VERSION_BAD_CHARS
                    return false
                }
            }
        }
        return true
    }

    fun validateAuthor(author: String): Boolean {
        if (author.isBlank()) {
            validationStatus = ValidationStatus.AUTHOR_BLANK
            return false
        }
        if (author.length > 255) {
            validationStatus = ValidationStatus.AUTHOR_TOO_LONG
            return false
        }
        return true
    }

    fun validateLanguageVersion(languageVersion: String): Boolean {
        if (languageVersion.isBlank()) {
            validationStatus = ValidationStatus.LANGUAGE_BLANK
            return false
        }
        if (!StringUtils.isNumeric(languageVersion) || languageVersion.length > 1
                || languageVersion != "4" && languageVersion != "6") {
            validationStatus = ValidationStatus.LANGUAGE_BAD_FORMAT
            return false
        }
        return true
    }
}