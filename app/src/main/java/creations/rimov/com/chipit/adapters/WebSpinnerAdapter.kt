package creations.rimov.com.chipit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import creations.rimov.com.chipit.R
import creations.rimov.com.chipit.database.objects.ChipReference
import kotlinx.android.synthetic.main.web_detail_spinner_chip_layout.view.*

class WebSpinnerAdapter : BaseAdapter(), SpinnerAdapter {

    private lateinit var parents: List<ChipReference>


    fun setParents(parents: List<ChipReference>) {
        this.parents = parents

        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if(view == null) view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.web_detail_spinner_chip_layout, parent, false)

        view!!.toolbarSpinnerText?.text = parents[position].name

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if(view == null) view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.web_detail_spinner_chip_layout, parent, false)

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