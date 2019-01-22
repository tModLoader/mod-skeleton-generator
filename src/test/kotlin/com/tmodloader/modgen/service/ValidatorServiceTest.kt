package com.tmodloader.modgen.service

import com.tmodloader.modgen.model.GenerationModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.streams.asSequence

internal class ValidatorServiceTest {

    @MockK
    lateinit var model: GenerationModel

    @InjectMockKs
    var validatorService = ValidatorService()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should succeed with valid model`() {
        every { model.modName } returns "TestModName"
        every { model.modDisplayName } returns "Test Mod Name"
        every { model.modVersion } returns "0.0.0.1"
        every { model.modAuthor } returns "TestAuthor"
        every { model.modLanguageVersion } returns "6"
        every { model.modBuildPropertiesGitIgnore } returns ".stub"
        every { model.outputPath } returns "C:\\"

        assertEquals(validatorService.validateGenerationModel(model), true)
        verify(exactly = 1) { validatorService.validateOutputPath(model.outputPath) }
        verify(exactly = 1) { validatorService.validateModName(model.modName) }
        verify(exactly = 1) { validatorService.validateModDisplayName(model.modDisplayName) }
        verify(exactly = 1) { validatorService.validateModVersion(model.modVersion) }
        verify(exactly = 1) { validatorService.validateAuthor(model.modAuthor) }
        verify(exactly = 1) { validatorService.validateLanguageVersion(model.modLanguageVersion) }
        verifyOrder {
            validatorService.validateOutputPath(model.outputPath)
            validatorService.validateModName(model.modName)
            validatorService.validateModDisplayName(model.modDisplayName)
            validatorService.validateModVersion(model.modVersion)
            validatorService.validateAuthor(model.modAuthor)
            validatorService.validateLanguageVersion(model.modLanguageVersion)
        }
    }

    @Test
    fun `should succeed on root path`() {
        every { model.outputPath } returns "C:\\"
        assertEquals(validatorService.validateOutputPath(model.outputPath), true)
        verify(exactly = 1) { validatorService.validateOutputPath(model.outputPath) }
    }

    @Test
    fun `should fail on bad internal name`() {
        every { model.modName } returns "Failure Name"
        assertEquals(validatorService.validateModName(model.modName), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.NAME_HAS_SPECIAL_CHARS)
    }

    @Test
    fun `should fail on blank internal name`() {
        every { model.modName } returns ""
        assertEquals(validatorService.validateModName(model.modName), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.NAME_BLANK)
    }

    @Test
    fun `should fail on blank display name`() {
        every { model.modDisplayName } returns ""
        assertEquals(validatorService.validateModDisplayName(model.modDisplayName), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.DISPLAYNAME_BLANK)
    }

    @Test
    fun `should fail on blank version`() {
        every { model.modVersion } returns ""
        assertEquals(validatorService.validateModVersion(model.modVersion), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.VERSION_BLANK)
    }

    @Test
    fun `should fail on bad version format`() {
        every { model.modVersion } returns "0.0.0.0.1"
        assertEquals(validatorService.validateModVersion(model.modVersion), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.VERSION_BAD_FORMAT)
    }

    @Test
    fun `should fail on bad version chars`() {
        every { model.modVersion } returns "0.0.1-ALPHA"
        assertEquals(validatorService.validateModVersion(model.modVersion), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.VERSION_BAD_CHARS)
    }

    @Test
    fun `should fail on blank author`() {
        every { model.modAuthor } returns ""
        assertEquals(validatorService.validateAuthor(model.modAuthor), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.AUTHOR_BLANK)
    }

    @Test
    fun `should fail on author too long`() {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        every { model.modAuthor } returns java.util.Random().ints(256, 0, chars.length)
                .asSequence()
                .map(chars::get)
                .joinToString("")

        assertEquals(validatorService.validateAuthor(model.modAuthor), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.AUTHOR_TOO_LONG)
    }

    @Test
    fun `should fail on blank language version`() {
        every { model.modLanguageVersion } returns ""
        assertEquals(validatorService.validateLanguageVersion(model.modLanguageVersion), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.LANGUAGE_BLANK)
    }

    @Test
    fun `should fail on bad language version`() {
        every { model.modLanguageVersion } returns "5"
        assertEquals(validatorService.validateLanguageVersion(model.modLanguageVersion), false)
        assertEquals(validatorService.validationStatus, ValidationStatus.LANGUAGE_BAD_FORMAT)
    }

    @Test
    fun `should succeed on language version`() {
        every { model.modLanguageVersion } returns "4"
        assertEquals(validatorService.validateLanguageVersion(model.modLanguageVersion), true)
        every { model.modLanguageVersion } returns "6"
        assertEquals(validatorService.validateLanguageVersion(model.modLanguageVersion), true)
    }
}