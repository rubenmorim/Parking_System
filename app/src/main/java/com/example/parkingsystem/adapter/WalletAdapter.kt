package com.example.parkingsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingsystem.R
import com.example.parkingsystem.model.Wallet


class WalletAdapter(val wallet: List<Wallet>): RecyclerView.Adapter<WalletViewHolder>() {
    var selecteditem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recyclerline_pagamento,
            parent, false)
        return WalletViewHolder(view)
    }

    override fun getItemCount(): Int {
        return wallet.size
    }



    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {


        val image:ImageView =  holder.itemView.findViewById<ImageView>(R.id.img1)

        if(selecteditem==position){
            image.setVisibility(View.VISIBLE);
        }
        else{
            image.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener {
            selecteditem =position;
            holder.itemView.findViewById<ImageView>(R.id.img1).setVisibility(View.VISIBLE)
            notifyDataSetChanged()
             }

        return holder.bind(wallet[position])
    }



}

class WalletViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
    private val tipoPagamento: TextView = itemView.findViewById(R.id.tipoPagamento)

    fun bind(wallet: Wallet) {
        val selecteditem =1;



        tipoPagamento.text = wallet.tipoPagamento
    }




}