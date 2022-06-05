package com.example.socialtec_tcc

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_tela_principal.*
import java.io.File
import kotlin.system.exitProcess

class TelaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        supportActionBar!!.hide()
        setContentView(R.layout.activity_tela_principal)


        acessarUsuario()


        configuracoes.setOnClickListener {
            val it = Intent(this, Configuracoes::class.java)
            startActivity(it)
        }
    }

    fun acessarUsuario() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid: String = user?.uid ?: "Erro"
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Espere um momento...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Log.d(TAG, "$uid do Usuário")


        val storageRef = FirebaseStorage.getInstance().reference.child("image/$uid")
        val localfile = File.createTempFile("tempImage", "jpg")

        storageRef.getFile(localfile).addOnSuccessListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            picUsuario.setImageBitmap(bitmap)
            Log.d(TAG, "Pegou")
        }.addOnFailureListener {
            Log.d(TAG, "Erro ao acessar imagem")
            Toast.makeText(applicationContext, "Erro", Toast.LENGTH_SHORT).show()
        }
    }

    fun carregarLogin() {
        val it = Intent(this, Login::class.java)
        startActivity(it)
        finish()
    }
}