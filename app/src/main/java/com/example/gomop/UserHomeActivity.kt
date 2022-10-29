package com.example.gomop

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.gomop.databinding.ActivityUserHomeBinding
import com.example.gomop.navigation.AlarmFragment
import com.example.gomop.navigation.HomeFragment
import com.example.gomop.navigation.SearchFragment
import com.example.gomop.navigation.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class UserHomeActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding :ActivityUserHomeBinding

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



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_home->{
                var detailViewFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
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
                var userFragment = UserFragment()
               /* var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                bundle.putString("destinationUid",uid)
                userFragment.arguments = bundle
                */
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }

}