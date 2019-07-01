package creations.rimov.com.chipit.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.Toolbar
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipReference
import kotlinx.android.synthetic.main.app_toolbar_layout.view.*
import kotlinx.android.synthetic.main.toolbar_parent_spinner_chip_layout.view.*

class AppToolbarLayout(context: Context, attrs: AttributeSet) : Toolbar(context, attrs) {

    private val webSpinner: Spinner by lazy {toolbarSpinner}
    private val adapter: ParentSpinnerAdapter by lazy {ParentSpinnerAdapter()}

    init {
        View.inflate(context, R.layout.app_toolbar_layout, this)

        webSpinner.adapter = adapter
    }

    fun setParents(parents: List<ChipReference>) {
        adapter.setParents(parents)
    }

    fun showSpinner() {
        webSpinner.visibility = View.VISIBLE
    }

    fun hideSpinner() {
        webSpinner.visibility = View.GONE
    }


    private class ParentSpinnerAdapter : BaseAdapter(), SpinnerAdapter {

        private lateinit var parents: List<ChipReference>


        fun setParents(parents: List<ChipReference>) {
            this.parents = parents

            notifyDataSetChanged()
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView

            if(view == null) view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.toolbar_parent_spinner_chip_layout, parent, false)

            view!!.toolbarSpinnerText?.text = parents[position].name

            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView

            if(view == null) view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.toolbar_parent_spinner_chip_layout, parent, false)

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
}