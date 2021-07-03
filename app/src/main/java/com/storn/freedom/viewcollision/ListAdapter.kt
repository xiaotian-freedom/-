package com.storn.freedom.viewcollision

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @Description:
 * @Author: TST
 * @CreateDate: 2021/7/3$ 2:18 下午$
 * @UpdateUser:
 * @UpdateDate: 2021/7/3$ 2:18 下午$
 * @UpdateRemark:
 * @Version: 1.0
 */
class ListAdapter(private val context: Context, private val list: MutableList<String>) : RecyclerView.Adapter<ListAdapter.ListHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.tv.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.findViewById(R.id.textView)
    }
}