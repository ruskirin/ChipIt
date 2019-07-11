package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipReference
import creations.rimov.com.chipit.extensions.gone
import creations.rimov.com.chipit.extensions.visible
import kotlinx.android.synthetic.main.app_toolbar_layout.view.*
import kotlinx.android.synthetic.main.toolbar_parent_spinner_chip_layout.view.*

class AppToolbarLayout(context: Context, attrs: AttributeSet) : Toolbar(context, attrs), AdapterView.OnItemSelectedListener {

    private lateinit var handler: ToolbarHandler

    private val webSpinner: Spinner by lazy {toolbarSpinner}
    private val adapter: ParentSpinnerAdapter by lazy {
        ParentSpinnerAdapter(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
    }

    init {
        View.inflate(context, R.layout.app_toolbar_layout, this)

        webSpinner.adapter = adapter
        webSpinner.onItemSelectedListener = this
    }

    fun setParents(parents: List<ChipReference>) {
        adapter.setParents(parents)
    }

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
                Log.i("Touch Event", "AppToolbar#onItemSelected(): selected id $id, position $position")

                handler.setSelectedChip(adapter.getItem(position) ?: return)
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
                .inflate(R.layout.toolbar_parent_spinner_chip_layout, parent, false)

//            view!!.toolbarSpinnerText?.text = parents[position].name

            return view!!
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView

            if(view == null) view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.toolbar_parent_spinner_chip_layout, parent, false)

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

    interface ToolbarHandler {

        fun setSelectedChip(chip: ChipReference)
    }
}