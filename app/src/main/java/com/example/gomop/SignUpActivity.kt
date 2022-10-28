package com.example.gomop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gomop.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/*class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }
}*/

class SignUpActivity : AppCompatActivity() {

    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    //private val binding = ActivitySignUpBinding.inflate(layoutInflater)


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        Log.d("auth",auth.toString())

        binding.btnSignUp.setOnClickListener {  //회원가입
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            // Validate...
            Log.d("id/pwd : ",email+password)
            createUser(email, password)
        }

        binding.btnSignIn.setOnClickListener { //로그인
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            // Validate...
            Log.d("id/pwd : ",email+password)
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            binding.txtResult.text = "Email: ${user.email}\nUid: ${user.uid}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}