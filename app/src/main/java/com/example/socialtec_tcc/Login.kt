package com.example.socialtec_tcc

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_login.*

open class Login : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        criar_conta.setOnClickListener {
            val intent = Intent(this, Cadastro::class.java)
            startActivity(intent)
        }

        val email = findViewById<EditText>(R.id.txt_Email)
        val senha = findViewById<EditText>(R.id.txt_Password)

//        if (auth.currentUser?.email != null){
//            nextActivity()
//        }
        val db = Firebase.firestore
        button.setOnClickListener {
            if (email.text.toString().isNotEmpty() && senha.text.toString().isNotEmpty()){
                    auth = FirebaseAuth.getInstance()
                        auth.signInWithEmailAndPassword(email.text.toString(), senha.text.toString())
                            .addOnCompleteListener() { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        if(user.isEmailVerified){
                                            Log.w(TAG, "Sucesso")
                                            val uid: String = user.uid
                                            val docRef = db.collection("usuario").document(uid)
                                            docRef.get().addOnSuccessListener { document ->
                                                if(document != null){
                                                    //Validar primeiro acesso, caso primeiro acesso criar perfil
                                                    val fAccess: String = document.get("facess") as String
                                                    Log.d(TAG, "status: $fAccess")
                                                    if(fAccess == "F"){
                                                       val it = Intent(this, CompPerfil::class.java)
                                                       startActivity(it)
                                                    }else{
                                                        nextActivity()
                                                    }
                                                } else {
                                                    Log.d(TAG, "erro")
                                                }
                                            }.addOnFailureListener {
                                                Log.d(TAG, "erro doc")
                                            }

                                        }else{
                                            Toast.makeText(applicationContext, "E-Mail não verificado", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "E-mail ou Senha inválidos!", Toast.LENGTH_SHORT).show()
                                }
                            }
            }else{
                Toast.makeText(applicationContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        txt_recSenha.setOnClickListener {
            val email = findViewById<EditText>(R.id.txt_Email)
            if (email.text.toString().isNotEmpty()) {
                auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "E-Mail enviado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Falha ao enviar o E-Mail", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Informe um E-Mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun nextActivity(){
        var it = Intent(this, TelaPrincipal::class.java)
        startActivity(it)
        finish()
    }
}