package com.example.socialtec_tcc

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_configuracoes.*
import kotlinx.android.synthetic.main.activity_configuracoes.picUsuario
import kotlinx.android.synthetic.main.activity_tela_principal.*
import java.io.File

class Configuracoes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_configuracoes)


        acessarImagem()

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid: String = user?.uid ?: "Erro"
        val db = Firebase.firestore

        btnAlterarSenha.setOnClickListener {
            val docRef = db.collection("usuario").document(uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val email: String = document.get("email") as String
                    auth.sendPasswordResetEmail(email).addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "E-Mail enviado", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Falha ao enviar o E-Mail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Log.d(TAG, "Erro")
                }
            } .addOnFailureListener {
                Log.d(TAG, "Erro ao pegar documento")
            }
        }

        btnDeslogar.setOnClickListener {
            Firebase.auth.signOut()
            val it = Intent(this, Login::class.java)
            startActivity(it)
            finish()
        }

        btnNovoEmail.setOnClickListener {
            val it = Intent(this, RedefinirEmail::class.java)
            startActivity(it)
        }

        btnExcluirUsuario.setOnClickListener {

        }

        btnSuporte.setOnClickListener {
            val email = "contato.socialtec@hotmail.com"

            val mIntent = Intent(Intent.ACTION_SEND)

            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"

            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            mIntent.putExtra(Intent.EXTRA_SUBJECT, "Assunto...")
            mIntent.putExtra(Intent.EXTRA_TEXT, "Corpo do E-mail...")

            try {
                startActivity(Intent.createChooser(mIntent, "Enviando e-mail"))
            } catch (e: Exception) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun acessarImagem() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid: String = user?.uid ?: "Erro"
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Espere um momento...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Log.d(TAG, "$uid do Usuário")

        val db = Firebase.firestore
        val docRef = db.collection("usuario").document(uid)
        docRef.get().addOnSuccessListener { document ->
            if(document != null){
                //Validar primeiro acesso, caso primeiro acesso criar perfil
                val nome: String = document.get("nome") as String
                val sobrenome: String = document.get("sobrenome") as String
                val uid: String = document.get("uid") as String
                val nomeContat: String = ("$nome $sobrenome")
                nomeUsuario.text = nomeContat
                uidUsuario.text = uid


            } else {
                Log.d(ContentValues.TAG, "erro")
            }
        }
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
}