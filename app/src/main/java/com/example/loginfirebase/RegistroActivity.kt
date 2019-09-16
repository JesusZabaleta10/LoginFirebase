package com.example.loginfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registro.*

class RegistroActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        progressBar = findViewById(R.id.progressBar)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbReference = database.reference.child("Usuario")
    }

    fun registrar(view: View){
        createNewAccount()
    }

    private fun createNewAccount(){
        val nombre = et_nombre.text.toString()
        val apellido = et_apellido.text.toString()
        val correo = et_correo.text.toString()
        val contraseña = et_contraseña.text.toString()

        if((nombre == "") || (apellido == "") || (correo == "") || (contraseña == "")){
            Toast.makeText(this, "Hay campos sin llenar", Toast.LENGTH_SHORT).show()
        }else if((contraseña.length < 6)){
            Toast.makeText(this, "La contraseña no tiene los 6 caracteres mínimos", Toast.LENGTH_LONG).show()
        }else{

            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(correo,contraseña)
                .addOnCompleteListener(this){
                    task ->
                    if(task.isComplete){
                        val user:FirebaseUser?=auth.currentUser

                        val userDB = dbReference.child(nombre + " " + apellido)
                        verifyEmail(user)

                        userDB.child("Nombre").setValue(nombre)
                        userDB.child("Apellido").setValue(apellido)
                        userDB.child("Correo").setValue(correo)
                        userDB.child("Contraseña").setValue(contraseña)
                        action()
                        Toast.makeText(this, "Te has registrado con éxito", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun action(){
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                    task ->
                if(task.isComplete){
                    Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
