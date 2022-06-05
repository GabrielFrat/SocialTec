package com.example.socialtec_tcc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.socialtec_tcc.utils.Conexao

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        supportActionBar!!.hide()



        setContentView(R.layout.activity_splash_screen)

        if (Conexao.oKConexao(this@SplashScreen)) {
            val intent = Intent(this, Login::class.java)

            Handler().postDelayed({
                intent.change()
            }, 3000)
        }else {
            Handler().postDelayed({
                Toast.makeText(applicationContext, "Sem Conexão com a Internet", Toast.LENGTH_LONG).show()
            }, 3000)

        }
    }


    fun Intent.change(){
        startActivity(this)
        finish()
    }

}