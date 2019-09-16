package com.example.loginfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
    }

    fun forgotPassword(view: View){
        startActivity(Intent(this, RecuperarPassActivity::class.java))
        Toast.makeText(this, "Recuperar contraseña", Toast.LENGTH_SHORT).show()
    }
    fun registrar(view: View){
        startActivity(Intent(this, RegistroActivity::class.java))
        Toast.makeText(this, "Registro", Toast.LENGTH_SHORT).show()
    }
    fun login(view: View){
        loginUser()
    }

    private fun loginUser(){
        val correo = et_correo.text.toString()
        val contraseña = et_contraseña.text.toString()

        if((correo == "") && (contraseña == "")) {
            Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
        }else if(correo == ""){
            Toast.makeText(this, "Ingrese correo electrónico", Toast.LENGTH_SHORT).show()
        }else if(contraseña == ""){
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
        }else{

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(correo,contraseña)
                .addOnCompleteListener(this){
                    task ->
                    if(task.isSuccessful){
                        action()
                        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    }else{
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Error en la identificación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun action(){
        var intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent,5678)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 5678 && resultCode == Activity.RESULT_CANCELED){
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
