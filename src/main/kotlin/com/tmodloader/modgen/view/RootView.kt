package com.tmodloader.modgen.view

import com.tmodloader.modgen.controller.RootController
import com.tmodloader.modgen.model.AppPreference
import com.tmodloader.modgen.model.GenerationModel
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import tornadofx.*

class RootView : View() {

    var directoryButton: Button by singleAssign()
    var directoryField: TextField by singleAssign()
    var modNameField: TextField by singleAssign()
    var displayNameField: TextField by singleAssign()
    var modVersionField: TextField by singleAssign()
    var modAuthorField: TextField by singleAssign()
    var modLangVersionField: ComboBox<Int> by singleAssign()
    var gitIgnoreField: TextField by singleAssign()
    var generateModBtn: Button by singleAssign()
    var statusLabel: Label by singleAssign()
    var resetCacheButton: Button by singleAssign()

    private val controller: RootController by inject()

    override val root = borderpane {
        title = "tModLoader Offline Mod Skeleton Generator"

        top = hbox {
            directoryButton = button("Choose Directory")
            directoryField = textfield {
                hgrow = Priority.ALWAYS
                promptText = "Select directory path..."
                isEditable = false
                tooltip("The path where the mod skeleton will be generated" +
                        "\nIf you want to paste a path, use the choosing button and paste " +
                        "the path in the explorer window")
            }
        }

        center = form {
            hbox(20) {
                fieldset("Mod details") {
                    field("Mod name") {
                        modNameField = textfield()
                        tooltip("Specifies the internal name of the mod")
                    }
                    field("Display name") {
                        displayNameField = textfield()
                        tooltip("Sets the displayName field in build.txt")
                    }
                    field("Mod version") {
                        modVersionField = textfield()
                        tooltip("Sets the version field in build.txt")
                    }
                    field("Mod author") {
                        modAuthorField = textfield()
                        tooltip("Sets the author field in build.txt")
                    }
                }

                fieldset("Extras") {
                    field("Language version") {
                        modLangVersionField = combobox {
                            items = FXCollections.observableArrayList(4, 6)
                            selectionModel.selectFirst()
                        }
                        tooltip("Sets the languageVersion to the specified version in build.txt")
                    }
                    field("Build ignore") {
                        gitIgnoreField = textfield {
                            promptText = "*.csproj, *.sln, ..."
                        }
                        tooltip("Fills the buildIgnore field in build.txt (optional)")
                    }
                }
            }
        }

        bottom = hbox {
            alignment = Pos.BASELINE_LEFT
            spacing = 5.0
            padding = Insets(0.0, 5.0, 5.0, 5.0)

            statusLabel = label("Idle") {
                tooltip("The current status of the application")
            }

            pane {
                hgrow = Priority.ALWAYS
            }

            resetCacheButton = button("Reset cache") {
                tooltip("Reset the remembered preferences")
            }

            generateModBtn = button("Generate mod") {
                tooltip("Attempt to generate the mod skeleton")
            }
        }
    }

    init {
        directoryButton.action { controller.chooseDirectory(currentStage) }
        generateModBtn.action { handleGenerateModSkeleton() }
        resetCacheButton.action { AppPreference.clearAllPreferences() }

        AppPreference.OUTPUT_PATH.observable
                .subscribe {
                    directoryField.text = it
                }
    }

    private fun handleGenerateModSkeleton() {
        with(GenerationModel(
                modNameField.text,
                displayNameField.text,
                modVersionField.text,
                modAuthorField.text,
                modLangVersionField.selectionModel.selectedItem.toString(),
                directoryField.text,
                gitIgnoreField.text
        )) {
            statusLabel.text = controller.generateModSkeleton(this)
        }
    }
}