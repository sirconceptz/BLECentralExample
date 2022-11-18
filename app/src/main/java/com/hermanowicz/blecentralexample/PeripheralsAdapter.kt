package com.hermanowicz.blecentralexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juul.kable.Advertisement

class PeripheralsAdapter(private val dataSet: HashMap<String, Advertisement>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<PeripheralsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    private val dataList = ArrayList(dataSet.values)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.name)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.peripheral_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if(position < dataList.size) {
            val device = dataList[position]
            viewHolder.textView.text = "${device.address} (${device.rssi}dB)"
            viewHolder.itemView.setOnClickListener { onClickListener.onClick(device) }
        }
    }

    override fun getItemCount() = dataSet.size

    class OnClickListener(val clickListener: (advertisement: Advertisement) -> Unit) {
        fun onClick(advertisement: Advertisement) = clickListener(advertisement)
    }
}