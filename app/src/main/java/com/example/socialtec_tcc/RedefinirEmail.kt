package com.example.socialtec_tcc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_redefinir_email.*

class RedefinirEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redefinir_email)


        btnRedefinir_Email.setOnClickListener {
            redefinirEmail()
        }
    }


    private fun redefinirEmail() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid: String = user?.uid ?: "Erro"

        val newEmail = findViewById<EditText>(R.id.txtView_NewEmail)
        val email: String = newEmail.text.toString()

        if(email.isNotEmpty()) {
            user?.updateEmail(email)?.continueWith { task ->
                if(task.isCanceled) {
                    Toast.makeText(applicationContext, "Alteração de E-Mail cancelada!", Toast.LENGTH_SHORT).show()
                    return@continueWith
                } else if(task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(applicationContext, "E-Mail alterado com sucesso!", Toast.LENGTH_SHORT).show()
                    user?.sendEmailVerification()?.addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(applicationContext, "E-Mail de Autenticação enviado!", Toast.LENGTH_SHORT).show()
                            Firebase.auth.signOut()
                            val it = Intent(this, Login::class.java)
                            startActivity(it)
                            finish()
                        }
                    } ?.addOnFailureListener {
                        Toast.makeText(applicationContext, "Erro ao Enviar E-Mail!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else {
            Toast.makeText(applicationContext, "Informe um E-Mail válido!", Toast.LENGTH_SHORT).show()
        }

    }
}