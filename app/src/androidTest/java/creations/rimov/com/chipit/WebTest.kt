package creations.rimov.com.chipit

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import creations.rimov.com.chipit.ui.directory.DirectoryRecyclerAdapter
import creations.rimov.com.chipit.ui.web.adapters.viewholders.web.WebViewHolder
import creations.rimov.com.chipit.util.constants.EditorConsts
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@MediumTest
class WebTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var device: UiDevice

    @Before
    fun prepare() {
        scenario = launchActivity()
        device = UiDevice
            .getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun addCard_deleteCard() {

        navToWeb(0)
        addCard(EditorConsts.VIDEO)
        deleteCard()
        navUp()
    }

    @After
    fun clean() {
        scenario.close()
    }

    private fun navToWeb(pos: Int) {

        onView(withId(R.id.dirRecycler))
            .perform(
              RecyclerViewActions
                  .actionOnItemAtPosition<DirectoryRecyclerAdapter.DirectoryViewHolder>(
                    pos, click()))
            .perform(
              RecyclerViewActions
                  .actionOnItemAtPosition<DirectoryRecyclerAdapter.DirectoryViewHolder>(
                    pos, RecyclerActions.clickTopicToWeb(R.id.topicLayoutBtnCount)))
    }

    private fun navUp() {

        onView(withContentDescription("Navigate up"))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    private fun addCard(type: Int) {

        onView(withId(R.id.mainFab)).perform(click())
        onView(withId(R.id.btnAddMatPrev)).perform(click())

        when(type) {
            EditorConsts.IMAGE -> {
                onView(withId(R.id.btnPromptAddImage)).perform(click())
                onView(withId(R.id.btnPromptAddStorage)).perform(click())

                device.findObject(
                  UiSelector()
                      .className("androidx.recyclerview.widget.RecyclerView")
                      .childSelector(
                        UiSelector()
                            .className("android.widget.ImageView")
                            .enabled(true)
                            .instance(0)))
                    .click()
            }
            EditorConsts.VIDEO -> {
                onView(withId(R.id.btnPromptAddVideo)).perform(click())
                onView(withId(R.id.btnPromptAddStorage)).perform(click())

                device.findObject(
                  UiSelector()
                      .text("ChipIt_VID_20200608at211232.mp4"))
                    .click()
            }
        }

        onView(withId(R.id.matTitleLayout)).perform(click())
        onView(withId(R.id.promptText))
            .perform(ViewActions.typeText("Guinea Title"))
        onView(withId(R.id.btnPromptTextSave)).perform(click())
        onView(withId(R.id.mainFab)).perform(click())
    }

    private fun deleteCard() {

        onView(withId(R.id.webChildrenView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<WebViewHolder>(
              0, swipeLeft()))
        onView(withId(R.id.btnConfirmYes)).perform(click())
    }
}