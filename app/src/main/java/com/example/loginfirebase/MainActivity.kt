package com.example.loginfirebase

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun salir(view: View){
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,LoginActivity::class.java))
        Toast.makeText(this, "Sesion Cerrada", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, LoginActivity::class.java)
        Toast.makeText(this, "Gracias por usar nuestra app", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_CANCELED,intent)
        finish()
    }
}
