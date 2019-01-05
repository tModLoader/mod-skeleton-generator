package com.tmodloader.modgen.controller

import com.tmodloader.modgen.model.AppPreference
import com.tmodloader.modgen.model.GenerationModel
import com.tmodloader.modgen.service.GeneratorService
import com.tmodloader.modgen.service.ValidatorService
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import tornadofx.Controller

class RootController : Controller() {

    private val validatorService: ValidatorService by di()
    private val generatorService: GeneratorService by di()

    fun chooseDirectory(currentStage: Stage?) {
        val path = DirectoryChooser().showDialog(currentStage)?.absolutePath
        if (path != null) {
            AppPreference.OUTPUT_PATH.update(path)
        }
    }

    fun generateModSkeleton(generationModel: GenerationModel): String {
        if (!validatorService.validateGenerationModel(generationModel)) {
            return validatorService.validationError
        }

        if (!generatorService.generateModSkeleton(generationModel)) {
            return generatorService.generationException ?: "Unknown error occurred"
        }

        return "Mod skeleton generated!"
    }
}