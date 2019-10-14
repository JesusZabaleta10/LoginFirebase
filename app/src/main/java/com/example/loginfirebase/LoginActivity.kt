package com.example.loginfirebase

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {

    // Login con Facebook //
    private lateinit var callbackManager: CallbackManager
    // Login con Facebook //

    // Login con google //
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    var bandera = 0
    // Login con google //

    // Login con correo //
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    // Login con correo //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        getKeyHash() //Obtener el KeyHash para facebook

        // Login con Facebook //
        callbackManager = CallbackManager.Factory.create()

        login_button.setReadPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                //Toast.makeText(baseContext, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(baseContext, "Cancelado", Toast.LENGTH_SHORT).show()
                auth.signOut()
                LoginManager.getInstance().logOut()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(baseContext, "Error de autenticación", Toast.LENGTH_SHORT).show()
                auth.signOut()
                LoginManager.getInstance().logOut()
            }
        })
        // Login con Facebook //

        // Login con google //
        val signIn = findViewById<View>(R.id.signInBtn) as SignInButton
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        signInBtn.setOnClickListener {
            bandera = 1
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        // Login con google //

        // Login con correo //
        progressBar = findViewById(R.id.progressBar)
        auth = FirebaseAuth.getInstance()
        // Login con correo //
    }

    //----------------------- Login con Facebook ---------------------------------//
    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)

                    val nombre = user?.displayName
                    val correo = user?.email
                    val id = user?.uid.toString()

                    auth = FirebaseAuth.getInstance()

                    val user2 = User(nombre.toString(), correo.toString(), id)

                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Usuarios de Facebook")

                    myRef.child(user?.uid.toString()).setValue(user2)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    //------------------------------- Login con Facebook -------------------------------------------//

    //---------------------- Login con google ----------------------------------------//

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // FACEBOOK //
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
        // FACEBOOK //

        if (bandera == 1) {
            if ((requestCode == RC_SIGN_IN)) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    updateUI(null)

                }
            }
        } else {
            bandera = 0
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
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(baseContext, "Sesión iniciada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(baseContext, "Error en la autenticación", Toast.LENGTH_SHORT).show()
        }
    }
    //---------------------- Login con google ----------------------------------------//

    //---------------------------------- Login con correo ----------------------------------------//
    fun forgotPassword(view: View) {
        startActivity(Intent(this, RecuperarPassActivity::class.java))
        Toast.makeText(this, "Recuperar contraseña", Toast.LENGTH_SHORT).show()
    }

    fun registrar(view: View) {
        startActivity(Intent(this, RegistroActivity::class.java))
        Toast.makeText(this, "Registro", Toast.LENGTH_SHORT).show()
    }

    fun login(view: View) {
        loginUser()
    }

    private fun loginUser() {
        val correo = et_correo.text.toString()
        val contraseña = et_contraseña.text.toString()

        if ((correo == "") && (contraseña == "")) {
            Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
        } else if (correo == "") {
            Toast.makeText(this, "Ingrese correo electrónico", Toast.LENGTH_SHORT).show()
        } else if (contraseña == "") {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
        } else {

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        action()
                        Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show()
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun action() {
        startActivity(Intent(this, MainActivity::class.java))
        //startActivityForResult(intent, 5678)
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
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    //Obtener el KeyHash para facebook
    fun getKeyHash(){
        try {
            val info = packageManager.getPackageInfo(
                "com.example.loginfirebase", // TODO Change the package name
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }
    }

}

