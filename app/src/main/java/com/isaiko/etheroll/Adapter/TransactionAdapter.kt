package com.isaiko.etheroll.Adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_transaction.view.*

class TransactionAdapter: RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class TransactionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val etherValue = itemView.tv_value
        val time = itemView.tv_time
        val txId = itemView.tv_transaction_id
        val from = itemView.tv_from
        val to = itemView.tv_to
        init {

        }
    }

    interface OnItemClickListener{
        fun onItemClick()
    }
}