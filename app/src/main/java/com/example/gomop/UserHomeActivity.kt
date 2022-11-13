package com.example.gomop

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gomop.navigation.*
import com.example.gomop.databinding.ActivityUserHomeBinding
import com.example.gomop.navigation.AlarmFragment
import com.example.gomop.navigation.HomeFragment
import com.example.gomop.navigation.SearchFragment
import com.example.gomop.navigation.UserFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_user_home.*

class UserHomeActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityUserHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottom_navigation=binding.bottomNavigation
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user!!.email!!
        val uid = user!!.uid
        Log.d("eeeeeeeeeeeeeeeeeeeeeeeemail",email)
        Log.d("Ueeeeeeeeeeeeeeeeeeeeeeeeeeeeeid",uid)


        binding.toolbarUserEmail.text = "Email: ${email}  Uid: ${uid}"

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == UserFragment.PICK_PROFILE_FROM_ALBUM && resultCode == RESULT_OK){
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefalut()
        when(item.itemId){
            R.id.action_home->{
                var HomeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,HomeFragment).commit()
                return true
            }
            R.id.action_search->{
                var searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,searchFragment).commit()
                return true
            }
            R.id.action_add_photo->{
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(
                        Intent(
                            this,
                            AddPhotoActivity::class.java
                        )
                    )
                }

                return true
            }
            R.id.action_favorite_alarm->{
                var alarmFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                return true
            }
            R.id.action_account->{
                var UserFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid",uid)
                UserFragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.main_content,UserFragment).commit()
                return true
            }
        }
        return false
    }
    fun setToolbarDefalut(){
        toolbar_userEmail.visibility = View.GONE
        toolbar_btn_back.visibility = View.GONE
        toolbar_title_image.visibility = View.VISIBLE
    }

}