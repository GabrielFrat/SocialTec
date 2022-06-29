package com.example.socialtec_tcc

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cadastro.*

class Cadastro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        supportActionBar!!.hide()
        setContentView(R.layout.activity_cadastro)

        val res: Resources = resources
        val listFatec = res.getStringArray(R.array.fatecs)
        val listCursos = res.getStringArray(R.array.cursosFatec)
        val listCargo = res.getStringArray(R.array.cargos)
        //Implementação e código referente a interface
        val listFatecAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listFatec)
        val listCursosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listCursos)
        val listCargoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listCargo)
        spinner_fatec.adapter = listFatecAdapter
        spinner_curso.adapter = listCursosAdapter
        spinner_cargo.adapter = listCargoAdapter

        spinner_fatec.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(applicationContext, "Você selecionou ${listFatec[p2]}", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Toast.makeText(applicationContext, "Nada Selecionado", Toast.LENGTH_SHORT).show()
            }

        }

        spinner_curso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(applicationContext, "Você selecionou ${listCursos[p2]}", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Toast.makeText(applicationContext, "Nada Selecionado", Toast.LENGTH_SHORT).show()
            }

        }

        spinner_cargo.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(applicationContext, "Você selecionou ${listCargo[p2]}", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Toast.makeText(applicationContext, "Nada Selecionado", Toast.LENGTH_SHORT).show()
            }

        }
        //Implementação referente ao backend

        //Métodos Construtores
        val usuario: Usuario = Usuario()

        //Chamada das funções


        if (checkedCargo.isChecked){
                spinner_curso.isEnabled = false
        } else {
                spinner_cargo.isEnabled = false
        }



        if (!checkedTermos.isChecked) {
                btn_cadastrarUser.isEnabled = false
        }

        checkedTermos.setOnClickListener() {
            btn_cadastrarUser.isEnabled = checkedTermos.isChecked
        }

        checkedCargo.setOnClickListener() {
            if (checkedCargo.isChecked) {
                spinner_cargo.isEnabled = true
                spinner_curso.isEnabled = false
            } else {
                spinner_cargo.isEnabled = false
                spinner_curso.isEnabled = true
            }
        }

        btn_cadastrarUser.setOnClickListener {
            val email = findViewById<EditText>(R.id.txt_email_inst)
            val nome = findViewById<EditText>(R.id.nome_usuario)
            val sobrenome = findViewById<EditText>(R.id.sobrenome)
            val fatec_user = spinner_fatec.selectedItem.toString()
            var curso_usuario = spinner_curso.selectedItem.toString()
            val senha_usuario = findViewById<EditText>(R.id.senha)
            val confirma_senha = findViewById<EditText>(R.id.conf_senha)
            var cargo_usuario = spinner_cargo.selectedItem.toString()
            var context: Context = applicationContext


            if (checkedCargo.isChecked) {
                curso_usuario = "Null"
            } else {
                cargo_usuario = "Null"
            }


            if (email.text.toString().isNotEmpty() && nome.text.toString().isNotEmpty()
                && sobrenome.text.toString().isNotEmpty()
                && fatec_user.isNotEmpty()
                && curso_usuario.isNotEmpty()
                && cargo_usuario.isNotEmpty()
                && senha_usuario.text.toString().isNotEmpty()
                && confirma_senha.text.toString().isNotEmpty()){

                usuario.cadastrarUsuario(email.text.toString(), nome.text.toString(), sobrenome.text.toString(),
                    fatec_user.toString(),
                    curso_usuario.toString(),
                    cargo_usuario.toString(), senha_usuario.text.toString(), confirma_senha.text.toString(), context)

                txt_email_inst.text.clear()
                nome_usuario.text.clear()
                sobrenome.text.clear()
                senha.text.clear()
                conf_senha.text.clear()
                val it = Intent(this, Login::class.java)
                Firebase.auth.signOut()
                startActivity(it)
                finish()
            }else{
                Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}