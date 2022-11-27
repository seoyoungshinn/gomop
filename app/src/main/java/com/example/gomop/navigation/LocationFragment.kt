package com.example.gomop.navigation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gomop.DataClassObject.FollowDTO
import com.example.gomop.DataClassObject.LocationDTO
import com.example.gomop.DataClassObject.MyLocation
import com.example.gomop.MyAdapter
import com.example.gomop.R
import com.example.gomop.databinding.FragmentLocationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val fbdb = Firebase.firestore //파이어베이스.파이어스토어 설정


    //FireBase관련
    private var auth : FirebaseAuth? = null     //FireBase Auth
    var firestore : FirebaseFirestore? = null
    // private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()     //FireBase RealTime
    // private val databaseReference: DatabaseReference = firebaseDatabase.reference       //FireBase RealTime
    private var uid : String? = null

    var followingCount : Int =0
    lateinit var followingArray : Array<String?>
    lateinit var locationDTOArray : Array<LocationDTO?>

    lateinit var mapView : MapView
    //lateinit var mapViewContainer: ViewGroup


    lateinit var binding: FragmentLocationBinding
    var currentUserUid : String? = null


    override fun onCreateView(inflater: LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?): View? {
        //var view = LayoutInflater.from(activity).inflate(R.layout.fragment_location,container,false)
      //  val binding = FragmentLocationBinding.inflate(inflater, container, false)

        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        // 2
        viewAdapter = MyAdapter()




        binding = FragmentLocationBinding.inflate(layoutInflater)
        val view = binding.root
        mapView = MapView(view.context)
        binding.clKakaoMapView.addView (mapView)

        //FireBase환경세팅
        Log.d("로그 firebase","파이어베이스 환경세팅")
        auth = Firebase.auth
        //firestore = FirebaseFirestore.getInstance()
        firebaseLogin()                         //파이어베이스 접근
        uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("로그 firebase","$uid")

 //       uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        currentUserUid = auth?.currentUser?.uid


        return view
    }  //End of onCreate()


    //팔로잉 uid 배열 생성
    fun getFollowers()  {
        //var followings :MutableMap<String,Boolean> = HashMap()
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
            if(followDTO?.followingCount != null ){
                followingCount =followDTO?.followingCount?.toInt()
                Log.d("로그","팔로잉 :"+followingCount+"명")

                //팔로워 크기만큼 배열 생성
                followingArray = Array(followingCount){null}
                Log.d("로그","팔로잉배열 :"+followingArray)

                makeLocationDTOArray(followDTO)
            }else {
                Log.d("로그","팔로워정보 가져오기 실패")
            }
        }
    }

    fun makeLocationDTOArray(followDTO : FollowDTO){
        //팔로잉 Uid Map
        var followings :MutableMap<String,Boolean> = followDTO.followings
        locationDTOArray = Array(followings.size){null}

        var followingUidArray : MutableSet<String> = followings.keys

        Log.d("로그", followingUidArray.toString())

        var i : Int = 0
        //LocationDTO 배열 생성
        for (uid in followingUidArray) {
            //locationDTOArray[i]?.uid  = followingUidArray[i]
            var requsetUid = uid
            firestore?.collection("uid")?.document(requsetUid)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var userInfo = documentSnapshot.toObject(LocationDTO::class.java)
                //locationDTOArray[i]?.uid = requsetUid
                locationDTOArray[i]  = userInfo
                Log.d("로그 in Frag, LocationDTOArray ",locationDTOArray[i].toString())
                i++
                if (userInfo != null) {
                    addOthersMarker(userInfo.lat, userInfo.lon, userInfo.id,userInfo.updateTime, mapView)
                }
            }
        }


    }

    private fun addOthersMarker(lat:Double, lon:Double, id:String,updateTime:String, mapView:MapView) {
        //        val mapView = MapView(this)
        //       binding.clKakaoMapView.addView(mapView)


        //내 위치 좌표를 받아온다
        val mapPoint = MapPoint.mapPointWithGeoCoord(lat!!, lon!!)

        //마커 생성
        val marker = MapPOIItem()
        marker.itemName = id +"님\n"+ updateTime
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.YellowPin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        mapView.addPOIItem(marker)

    }

    private fun addMyMarker() {
        //   val mapView = MapView(this)
//        binding.clKakaoMapView.addView(mapView)

        //내 위치 좌표를 받아온다
        val mapPoint = MapPoint.mapPointWithGeoCoord(MyLocation.lat, MyLocation.lon)

        //지도의 중심점을 내 위치로 설정, 확대 레벨 설정 (값이 작을수록 더 확대됨)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setZoomLevel(1, true)

        //마커 생성
        val marker = MapPOIItem()
        marker.itemName = "내 위치\n"+MyLocation.updateTime
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        if ( MyLocation.lat != 0.0)
            mapView.addPOIItem(marker)
    }



    private fun getLocationFromDB() {
        //FireBase에서 Location데이타 불러와 데이터스냅샷 형태로 저장후 잘라서 싱글톤객체 myLocation에 저장.
        var snapshotData: Map<String, Any>
        firestore = FirebaseFirestore.getInstance()
        Log.d("로그!!: ",firestore.toString())
        uid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("로그!!내uid: ",firestore.toString())
        val dbData = firestore!!.collection("uid").document("${uid}")
        dbData.get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    snapshotData = doc.data as Map<String, Any>
                    Log.d("로그 MainActivity-LocationDataFromDB()","DB에서 데이타불러옴")

                    // Preference.score = "${snapshotData.get("score")}".toInt()
                    MyLocation.lon = "${snapshotData!!.get("lon")}".toDouble()
                    MyLocation.lat = "${snapshotData!!.get("lat")}".toDouble()
                    MyLocation.updateTime ="${snapshotData!!.get("updateTime")}".toString()
                    Log.d("로그 DB내 좌표 : ","${MyLocation.lon}"+"${MyLocation.lat}")
                    addMyMarker()

                } else {
                    Log.d("로그 에러 : LocationDataFromDB()", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("로그 에러: LocationDataFromDB()", "get failed with ", exception)
            }
    }


    private fun firebaseLogin() {
/*        //고정로그인(테스트용)
        auth?.signInWithEmailAndPassword("test1@test.com", "test123")?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("로그 파이어베이스로그인", "로그인 성공" + "${auth}")
                Log.d("로그 시간", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))

                getLocationFromDB()
                getFollowers()
            } else {
                Log.d("로그 파이어베이스로그인", "로그인 실패" + "${auth}")
            }
        }*/

        getLocationFromDB()
        getFollowers()
    }
}