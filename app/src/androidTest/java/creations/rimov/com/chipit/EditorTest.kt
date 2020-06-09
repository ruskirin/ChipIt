package creations.rimov.com.chipit

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import creations.rimov.com.chipit.activities.MainActivity
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@LargeTest
class EditorTest {

    @Test
    fun playVideo_syncSeekbar() {

        val device: UiDevice = UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
        val scenario: ActivityScenario<MainActivity> = launchActivity()

        val idleRes = CountingIdlingResource("idler").also {
            IdlingRegistry.getInstance().register(it)
        }

        onView(withId(R.id.mainFab)).perform(click())
        onView(withId(R.id.btnAddMatPrev)).perform(click())
        onView(withId(R.id.btnPromptAddVideo)).perform(click())
        onView(withId(R.id.btnPromptAddStorage)).perform(click())
        onView(withId(R.id.btnPromptAddStorage)).perform()

        device.findObject(
          UiSelector()
              .text("ChipIt_VID_20200608at211232.mp4"))
            .apply {click()}

        onView(withId(R.id.matPreviewVideoLayout)).check(matches(isDisplayed()))
        onView(allOf(
          withId(R.id.btnPlaybackPlay),
          withEffectiveVisibility(Visibility.VISIBLE))).perform(click())

        Looper.prepare()

        idleRes.increment()

        Looper.loop()
        object : Runnable {

            override fun run() {
                idleRes.decrement()

                Log.i("EditorTest", "Decrementing idler")

                Handler().let {
                    it.postDelayed(this, 3000)
                    it.looper.quit()
                }
            }
        }

        scenario.close()
    }
}