package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CoachCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("GritCoach", appName)
    }

    @Test
    fun `verify coach categories exist`() {
        val study = CoachCategory.STUDY
        assertEquals("Study & Learning", study.title)
        assertNotNull(study.systemPrompt)
    }

    @Test
    fun `verify onboarding transition screen state`() {
        val screen: com.example.ui.viewmodel.Screen = com.example.ui.viewmodel.Screen.OnboardingTransition
        assertNotNull(screen)
    }
}
