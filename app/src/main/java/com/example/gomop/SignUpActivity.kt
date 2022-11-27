package com.example.gomop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gomop.DataClassObject.MyLocation
import com.example.gomop.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

/*class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }
}*/

class SignUpActivity : AppCompatActivity() {

    private var _binding: ActivitySignUpBinding? = null
    private val binding get() = _binding!!

    var firestore: FirebaseFirestore? = null
    var uid : String? = null
    //private val binding = ActivitySignUpBinding.inflate(layoutInflater)


    private lateinit var auth: FirebaseAuth

    public fun getAuth() : FirebaseAuth{
        return this.auth
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Log.d("auth",auth.toString())
        uid = auth.currentUser?.uid
        Log.d("uid",uid.toString())

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
    override fun onStart() { //로그아웃 안한 경우 자동 로그인
        super.onStart()
        if (auth!=null) moveUserHomePage(auth.currentUser)
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                    moveUserHomePage(user)
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

                    //파이어베이스 Auth에 새로운 유저를 등록하는 일에 성공하면, 해당 유저의 정보를 fireBaseStorage에 저장한다.
                    val id : String= email.split("@").get(0)
                    Log.d("로그: id = ",id)

                    val user = auth.currentUser
                    uid = auth.currentUser?.uid
                    Log.d("로그 새로부여된uid : ",uid.toString())
                    Log.d("로그 새로 등록할 위치 : ",firestore?.collection("uid")?.document(uid!!).toString())
                    MyLocation.id = id
                    firestore?.collection("uid")?.document(uid!!)?.set(MyLocation)//도큐먼트 생성

                    var dat : HashMap<String, String?> = hashMapOf("nickname" to uid)
                    firestore?.collection("player")?.document(id)?.set(dat)

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
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

    fun moveUserHomePage(user: FirebaseUser?) {
        if (user != null) {
            var intent = Intent (this, UserHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}