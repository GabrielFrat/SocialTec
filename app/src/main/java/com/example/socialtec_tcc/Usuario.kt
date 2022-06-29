package com.example.socialtec_tcc

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Usuario : Login() {
    private lateinit var database: DatabaseReference
    
    fun validarEmail(email: String, ctx: Context): Boolean {
        val result = email.contains("@fatec.sp.gov.br")

        if(result){
            return true
        } else {
            Toast.makeText(ctx, "Informe um E-Mail Institucional", Toast.LENGTH_LONG).show()
        }
        return false
    }

    fun validarSenha(senha: String, conf_senha: String, ctx: Context): Boolean{
        val tamSenha: Int = senha.length

        if(senha == conf_senha ){
            if(tamSenha in 8..16){
                 return true
            }else{
                Toast.makeText(ctx, "A senha deve conter de 8 à 16 caracteres!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(ctx, "As senhas não conferem!", Toast.LENGTH_SHORT).show()
        }
        return false
    }
    fun cadastrarUsuario(email_instituicional: String, nome: String, sobrenome: String,
                         fatec: String, curso: String, cargo: String, senha: String, conf_senha: String, ctx: Context){
        val confirmar: Boolean = validarEmail(email_instituicional, ctx)
        val auth = FirebaseAuth.getInstance()
        val confirmarSenha: Boolean = validarSenha(senha, conf_senha, ctx)



        val db = Firebase.firestore
        val firstAcess: String = "F"

        data class StudantInfo(val uid: String, val email: String, val nome: String, val sobrenome: String, val cargo: String, val fatec: String, val curso: String, val fAcess: String){}

        if(confirmar && confirmarSenha) {
                        auth.createUserWithEmailAndPassword(email_instituicional, senha)
                            .addOnCompleteListener() { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val uid: String = user?.uid ?: "Erro"
                                    val studantData = StudantInfo(uid, email_instituicional.trim(), nome.trim(), sobrenome, cargo, fatec, curso, firstAcess)
                                    db.collection("usuario").document(uid).set(studantData).addOnCompleteListener {
                                        Toast.makeText(ctx, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                                        user?.sendEmailVerification()?.addOnCompleteListener() { task ->
                                            if(task.isSuccessful) {
                                                Toast.makeText(ctx, "Verifique seu E-Mail!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(ctx, "Erro ao enviar o E-Mail", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } .addOnFailureListener {
                                        Toast.makeText(ctx, "Erro ao Cadastrar Usuário", Toast.LENGTH_SHORT).show()
                                    }

                                    //Toast.makeText(ctx, "Usuário Criado com Sucesso!", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "Usuário Criado com Sucesso")
                                } else {
                                    Toast.makeText(ctx, "Erro ao Cadastrar Usuário!", Toast.LENGTH_SHORT).show()
                                    Log.w(TAG, "Erro ao Cadastrar Login de Usuário", task.exception)
                                }
                            }
            }
        }
}