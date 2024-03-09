package com.agenda.beritaacara.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agenda.beritaacara.R
import com.agenda.beritaacara.data.ListPesertaData

class PesertaAdapter(
    private val listenerPesertaAdapter : ListnerePesertaAdapter,
    private val list : ArrayList<ListPesertaData>
) : RecyclerView.Adapter<PesertaAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_peserta, parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.tvNama.text = item.nama
        holder.btnHapus.setOnClickListener {
            listenerPesertaAdapter.onDelete(item.id)
        }
    }

    class MyViewHolder(itemView : View ) : RecyclerView.ViewHolder(itemView) {
        val tvNama : TextView = itemView.findViewById(R.id.tv_nama_peserta)
        val btnHapus : TextView = itemView.findViewById(R.id.btn_hapus_peserta)
    }

    interface ListnerePesertaAdapter {
        fun onDelete(id: Int)
    }
}