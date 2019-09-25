package com.example.loginfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.facebook.login.LoginManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.remote.FacebookSignInHandler
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
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

            val nombre = acct.displayName
            val correo = acct.email
            val id = acct.id

            tv_nombre.text = "NOMBRE: " + nombre
            tv_correo.text = "CORREO: " + correo
            tv_id.text = "ID: " + id

            auth = FirebaseAuth.getInstance()

            val user: FirebaseUser? = auth.currentUser

            val user2 = User(nombre.toString(), correo.toString(), id.toString())

            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Usuarios de Gmail")

            myRef.child(user?.uid.toString()).setValue(user2)
        }

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    fun salir(view: View){

        // Login con Facebook //
        //auth.signOut() //Desconectar de Firebase
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        // Login con Facebook //

        mGoogleSignInClient.signOut() //Desconectar de Google
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
