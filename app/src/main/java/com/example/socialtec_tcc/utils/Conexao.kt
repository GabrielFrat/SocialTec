package com.example.socialtec_tcc.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

object Conexao {

    fun oKConexao(ctx: Context): Boolean {
        var result = false
        (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                result = verificaConexao(this, this.activeNetwork)
            }else{
                val networks = this.allNetworks
                for(network in networks) {
                    if(verificaConexao(this, network)) {
                        result = true
                    }
                }
            }

        }

        return result
    }

    private fun verificaConexao(cm: ConnectivityManager, network: Network?): Boolean {
        cm.getNetworkCapabilities(network)?.also {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                return true
            }
        }
        return  false
    }

}