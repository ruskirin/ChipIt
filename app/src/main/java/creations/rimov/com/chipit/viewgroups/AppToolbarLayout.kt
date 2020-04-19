package creations.rimov.com.chipit.viewgroups

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.toolbar_spinner_chipparent.view.*

class AppToolbarLayout(context: Context, attrs: AttributeSet)
    : Toolbar(context, attrs), AdapterView.OnItemSelectedListener {

    private lateinit var handler: ToolbarHandler

    private val webSpinner: Spinner by lazy {toolbarSpinner}
    private val adapter: ParentSpinnerAdapter by lazy {
        ParentSpinnerAdapter(ResourcesCompat.getColor(resources, R.color.accent_material_dark, null))
    }
    //Keep track of previously selected Chip
    private var prevSelected: Long = -1L

    init {
        View.inflate(context, R.layout.main_toolbar, this)

        webSpinner.adapter = adapter
        webSpinner.onItemSelectedListener = this
    }

    fun setParents(parents: List<ChipReference>) {
        adapter.setParents(parents)

        webSpinner.setSelection(0) //Reset selection
    }

    fun getParentOfCurrent() = adapter.getItem(1)

    fun setHandler(handler: ToolbarHandler) {
        this.handler = handler
    }

    fun showSpinner() {
        webSpinner.visible()
    }

    fun hideSpinner() {
        webSpinner.gone()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        view?.let {
            if(position != 0) {
                Log.i("Touch Event", "AppToolbar#onItemSelected(): " +
                        "prev selected id $prevSelected, selected id $id, position $position")

                handler.setSelectedChip(adapter.getItem(position) ?: return)

                prevSelected = id
            }
        }
    }


    private class ParentSpinnerAdapter(private val selectColor: Int) : BaseAdapter(), SpinnerAdapter {

        private lateinit var parents: List<ChipReference>


        fun setParents(parents: List<ChipReference>) {
            this.parents = parents

            notifyDataSetChanged()
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView

            if(view == null) view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.toolbar_spinner_chipparent, parent, false)

            view!!.toolbarSpinnerText?.text = parents[0].name

            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView

            if(view == null) view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.toolbar_spinner_chipparent, parent, false)

            if(position == 0)
                view?.setBackgroundColor(selectColor)
            else
                view?.setBackgroundColor(0)

            view!!.toolbarSpinnerText?.text = parents[position].name

            return view
        }

        override fun getItem(position: Int) =
            if(::parents.isInitialized) parents[position]
            else null

        override fun getItemId(position: Int) =
            if(::parents.isInitialized) parents[position].id
            else -1L

        override fun getCount() =
            if(::parents.isInitialized) parents.size
            else 0
    }

    private var isToolbarVanish = false //Keep track of the status of the toolbar
    fun vanishToolbar(vanish: Boolean) {

        if(isToolbarVanish == vanish) return //Toolbar already in the desired state

        //TODO FUTURE: issue with transition to transparent
        if(vanish) {
            ObjectAnimator.ofArgb(this, "backgroundColor", Color.TRANSPARENT)
                .apply {
                    duration = 400
                    start()
            }

        } else {
            ObjectAnimator.ofArgb(this, "backgroundColor",
                                  ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null))
                .apply {
                    duration = 400
                    start()
            }
        }

        isToolbarVanish = vanish
    }

    interface ToolbarHandler {

        fun setSelectedChip(chip: ChipReference)
    }
}