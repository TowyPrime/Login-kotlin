package com.example.datatest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        btnRegister.setOnClickListener{
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val hashedPassword = PasswordHelper.hashPassword(password)
                    val existingUser = userDao.getUserByUsername(username)
                    if (existingUser == null) {
                        userDao.insertUser(User(username = username, password = hashedPassword))
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@MainActivity,"Usuario o contraseña incorrectos",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener{
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = userDao.getUserByUsername(username)
                    if (user != null && PasswordHelper.verifyPassword(password, user.password)) {
                        runOnUiThread {
                            //Abrimos la vista del deshboard si las credenciales son correctas
                            Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, DeshboardActivity::class.java)
                            startActivity(intent)
                            //Evitamos que puedan regresar al login
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@MainActivity, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }

        }

    }
}