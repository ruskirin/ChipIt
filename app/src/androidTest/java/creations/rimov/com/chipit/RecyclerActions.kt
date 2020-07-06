package creations.rimov.com.chipit

import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

object RecyclerActions {

    fun clickTopicToWeb(id: Int): ViewAction {

        return object : ViewAction {
            override fun getDescription(): String = "Click on RecyclerView item."

            override fun getConstraints(): Matcher<View> {

                return allOf(
                  withId(id),
                  withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))
            }

            override fun perform(uiController: UiController?, view: View?) {

                view?.findViewById<View>(id)?.apply {

                    //Generalized for OnClickListeners as well
                    if(isEnabled && isClickable && !performClick()) {
                        //Define click event
                        val event: MotionEvent = MotionEvent.obtain(
                          SystemClock.uptimeMillis(),
                          SystemClock.uptimeMillis(),
                          MotionEvent.ACTION_UP,
                          view.x,
                          view.y,
                          0)

                        if(!dispatchTouchEvent(event))
                            throw Exception("Not clicking!")
                    }
                }
            }
        }
    }
}