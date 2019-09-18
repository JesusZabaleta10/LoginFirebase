package com.example.loginfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    // Login con google //
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    // Login con google //

    // Login con correo //
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    // Login con correo //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Login con google //
        val signIn = findViewById<View>(R.id.signInBtn) as SignInButton
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInBtn.setOnClickListener{
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        // Login con google //

        // Login con correo //
        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        // Login con correo //
    }

    //---------------------- Login con google ----------------------------------------//

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                updateUI(null)

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            Toast.makeText(baseContext, "Sesión iniciada",Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(baseContext, "Error en la autenticación",Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------- Login con google ----------------------------------------//

    //---------------------------------- Login con correo ----------------------------------------//
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
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun action(){
        var intent = Intent(this, MainActivity::class.java)
        startActivityForResult(intent,5678)
        finish()
    }
    //---------------------------------- Login con correo ----------------------------------------//

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null ){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}