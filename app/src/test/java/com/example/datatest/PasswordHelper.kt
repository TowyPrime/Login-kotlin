package com.example.datatest
import org.mindrot.jbcrypt.BCrypt
//Convertir password a datos cifrados
object PasswordHelper {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}
