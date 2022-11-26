package com.example.gomop

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.gomop.DataClassObject.MyLocation
import com.example.gomop.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import java.security.MessageDigest
import net.daum.mf.map.api.MapView;
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding :ActivityMainBinding
    private val TAG = "SOL_LOG"

    //FireBase관련
    private var auth : FirebaseAuth? = null     //FireBase Auth
    var firestore : FirebaseFirestore? = null
    // private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()     //FireBase RealTime
    // private val databaseReference: DatabaseReference = firebaseDatabase.reference       //FireBase RealTime
    private var uid : String? = null


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //FireBase환경세팅
        Log.d("로그 firebase","파이어베이스 환경세팅")
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        firebaseLogin()                         //파이어베이스 로그인
        uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("로그 firebase","$uid")








        //해쉬코드 발급용 코드
       /* try {
            val information = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA").apply {
                    update(signature.toByteArray())
                }
                val HASH_CODE = String(Base64.encode(md.digest(), 0))

                Log.d(TAG, "HASH_CODE -> $HASH_CODE")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception -> $e")
        }
*/
        val binding = ActivityMainBinding.inflate(layoutInflater)
        startActivity(Intent(this, SignUpActivity::class.java))


    }

    private fun addMarker() {
        val mapView = MapView(this)
        binding.clKakaoMapView.addView(mapView)



        //수원 화성의 위도, 경도
        val mapPoint = MapPoint.mapPointWithGeoCoord(MyLocation.lat, MyLocation.lon)

        //지도의 중심점을 수원 화성으로 설정, 확대 레벨 설정 (값이 작을수록 더 확대됨)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setZoomLevel(1, true)

        //마커 생성
        val marker = MapPOIItem()
        marker.itemName = "나의 최근 위치"
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        mapView.addPOIItem(marker)
    }

    private fun getLocationFromDB() {
        //FireBase에서 Location데이타 불러와 데이터스냅샷 형태로 저장후 잘라서 싱글톤객체 myLocation에 저장.
        var snapshotData: Map<String, Any>
        val dbData = firestore!!.collection("uid").document("${uid}")
        dbData.get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    snapshotData = doc.data as Map<String, Any>
                    Log.d("로그 MainActivity-LocationDataFromDB()","DB에서 데이타불러옴")

                    // Preference.score = "${snapshotData.get("score")}".toInt()
                    MyLocation.lon = "${snapshotData!!.get("lon")}".toDouble()
                    MyLocation.lat = "${snapshotData!!.get("lat")}".toDouble()
                    Log.d("로그 DB내 좌표 : ","${MyLocation.lon}"+"${MyLocation.lat}")
                    addMarker()

                } else {
                    Log.d("로그 에러 : LocationDataFromDB()", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("로그 에러: LocationDataFromDB()", "get failed with ", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun firebaseLogin() {
        //고정로그인(테스트용)
        auth?.signInWithEmailAndPassword("test1@test.com", "test123")?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("로그 파이어베이스로그인", "로그인 성공" + "${auth}")
                Log.d("로그 시간", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))

                getLocationFromDB()

            } else {
                Log.d("로그 파이어베이스로그인", "로그인 실패" + "${auth}")
            }
        }
    }


}