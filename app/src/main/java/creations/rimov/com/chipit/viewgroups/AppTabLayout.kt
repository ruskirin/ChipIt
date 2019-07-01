package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout
import creations.rimov.com.chipit.R

class AppTabLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.app_tab_layout, this)

        TODO("Set up the click listeners and navigation to appropriate fragments. Then establish a proper starting point" +
                "for the Web. Then rework the Editor to prompt indiv fields instead of the full chip display.")
    }
}