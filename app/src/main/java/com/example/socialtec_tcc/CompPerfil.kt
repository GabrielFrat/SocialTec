package com.example.socialtec_tcc

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_comp_perfil.*
import java.net.URI
import java.text.SimpleDateFormat
import java.util.jar.Manifest

class CompPerfil : AppCompatActivity() {

    lateinit var imageUri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_comp_perfil)


        fotoPerfil.setOnClickListener() {
            pegarImagem()
        }


        btn_cadPerfil.setOnClickListener {
            cadastrarPerfil()
        }
    }

    fun pegarImagem() {
        if(cameraPermission() == PERMISSION_GRANTED && externalStoragePermission() == PERMISSION_GRANTED){
            selecionarImagem()
        }else{
            requestPermissions.launch(arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ))
        }
    }

    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        resultsMap ->
        var permissionGranted = false
        resultsMap.forEach {
            if(it.value == true) {
                permissionGranted = it.value
            }else{
                permissionGranted = false
                return@forEach
            }
            if(permissionGranted) {
                selecionarImagem()
            }else{
                Toast.makeText(this, "Sem Permissão", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun cameraPermission() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
    fun externalStoragePermission() = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)


    companion object {
        private val IMAGE_PICK_CODE = 1000
    }

    private fun selecionarImagem() {
        val it = Intent(Intent.ACTION_PICK)
        it.type = "image/*"
        startActivityForResult(it, IMAGE_PICK_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            fotoPerfil.setImageURI(data?.data)
            imageUri = data?.data!!
        }
    }

    private fun cadastrarPerfil() {
        val biografia = findViewById<EditText>(R.id.biografia)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Finalizando Cadastro...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid: String = user?.uid ?: "Erro"
        val storageReference = FirebaseStorage.getInstance().getReference("image/$uid")

        val db = Firebase.firestore
        val firstAcess = "T"


        data class perfil(val fotoPerfil: String, val usuario: String, val biografia: String)
        val perfilUsuario = perfil(uid, uid, biografia.text.toString())
        storageReference.putFile(imageUri).addOnSuccessListener {
            db.collection("perfil").document(uid).set(perfilUsuario).addOnSuccessListener {
                db.collection("usuario").document(uid).update("facess", firstAcess).addOnSuccessListener {
                    fotoPerfil.setImageURI(null)
                        Toast.makeText(applicationContext, "Perfil Cadastrado com Sucesso", Toast.LENGTH_SHORT).show()
                        if (progressDialog.isShowing) progressDialog.dismiss()

                        val it = Intent(this, TelaPrincipal::class.java)
                        startActivity(it)
                    finish()
                }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Erro ao Cadastrar Perfil", Toast.LENGTH_SHORT).show()
                    }
                }
        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(applicationContext, "Erro ao Cadastrar Perfil", Toast.LENGTH_SHORT).show()
        }
    }
}