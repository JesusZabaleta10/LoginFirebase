package com.example.loginfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            tv_nombre.text = "NOMBRE: " + acct.displayName
            tv_correo.text = "CORREO: " + acct.email
            tv_id.text = "ID: " + acct.id
        }

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
        auth = FirebaseAuth.getInstance()
    }

    fun salir(view: View){
        //FirebaseAuth.getInstance().signOut() // Cerrar correo electrónico
        mGoogleSignInClient.signOut() //Desconectar de Google
        auth.signOut() //Desconectar de Firebase
        startActivity(Intent(this,LoginActivity::class.java))
        Toast.makeText(this, "Sesion Cerrada", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this, "Gracias por usar nuestra app", Toast.LENGTH_SHORT).show()
        finish()
    }
}
