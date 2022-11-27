package com.example.gomop.navigation
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gomop.DataClassObject.AlarmDTO
import com.example.gomop.R
import com.example.gomop.SignUpActivity
import com.example.gomop.DataClassObject.ContentDTO
import com.example.gomop.DataClassObject.FollowDTO
import com.example.gomop.UserHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment(){
    val fbdb = Firebase.firestore
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid :String? = null
    var auth : FirebaseAuth?= null
    var currentUserUid : String? = null

    companion object{
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        currentUserUid = auth?.currentUser?.uid

        var mainactivity = (activity as UserHomeActivity)






        //mainactivity?.toolbar_userEmail?.text = arguments?.getString(theNickName.toString())
        mainactivity?.toolbar_btn_back?.setOnClickListener {
            mainactivity.bottom_navigation.selectedItemId = R.id.action_home
        }
        mainactivity?.toolbar_title_image?.visibility = View.GONE
        mainactivity?.toolbar_userEmail?.visibility = View.VISIBLE
        mainactivity?.toolbar_btn_back.visibility = View.VISIBLE

        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity, 3)





        if(uid == currentUserUid){
            fragmentView?.account_iv_profile?.setOnClickListener {
                Log.d("로그 : 프로필 이미지 누름","")
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                activity?.startActivityForResult(photoPickerIntent,PICK_PROFILE_FROM_ALBUM) //10번으로 시작.. 뭔말이야이게..
            }
            //My page
            fbdb.collection("uid") //첫번째칸 컬렉션 (player 부분 필드데이터를 전부 읽음)
                .get()
                .addOnCompleteListener { task ->

                    var afound = false  //데이터 찾지 못했을때

                    if (task.isSuccessful) { //제대로 접근 했다면
                        //Log.d("1","uid읽기성공")
                        for (i in task.result!!) {
                            if (i.id == uid) { //입력한 데이터와 같은 이름이 있다면(player id 부분)
                                //Log.d("2","${uid} 문서읽기 완료")
                                val theNickName = i.data["id"] //필드 데이터
                                /*textv1.text =
                                    theNickName.toString()   //text1에 읽은 nicknmae 필드 데이터 입력*/
                                //Log.d("3",theNickName.toString())
                                mainactivity?.toolbar_userEmail?.text = theNickName.toString()


                            } //if (task.
                        } //for
                    }
                }
            fragmentView?.account_btn_follow_signout?.text = "LOGOUT"
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity,SignUpActivity::class.java))
                auth?.signOut()




            }
        }
        else{
            //Other User page

            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)

            //검색할 때 오류나서 주석처리했으
            // 이제 오류 안뜸 - 동주
            var mainactivity = (activity as UserHomeActivity)
            fbdb.collection("uid") //첫번째칸 컬렉션 (player 부분 필드데이터를 전부 읽음)
                .get()
                .addOnCompleteListener { task ->

                    var afound = false  //데이터 찾지 못했을때

                    if (task.isSuccessful) { //제대로 접근 했다면
                        //Log.d("1","uid읽기성공")
                        for (i in task.result!!) {
                            if (i.id == uid) { //입력한 데이터와 같은 이름이 있다면(player id 부분)
                                //Log.d("2","${uid} 문서읽기 완료")
                                val theNickName = i.data["id"] //필드 데이터
                                /*textv1.text =
                                    theNickName.toString()   //text1에 읽은 nicknmae 필드 데이터 입력*/
                                //Log.d("3",theNickName.toString())
                                mainactivity?.toolbar_userEmail?.text = theNickName.toString()


                            } //if (task.
                        } //for
                    }
                }
            //mainactivity?.toolbar_userEmail?.text = arguments?.getString("userID")
            mainactivity?.toolbar_btn_back?.setOnClickListener {
                mainactivity.bottom_navigation.selectedItemId = R.id.action_home
            }
            mainactivity?.toolbar_title_image?.visibility = View.GONE
            mainactivity?.toolbar_userEmail?.visibility = View.VISIBLE
            mainactivity?.toolbar_btn_back.visibility = View.VISIBLE
            fragmentView?.account_btn_follow_signout?.setOnClickListener{
                requestFollow()
            }


        }



        ProfileImage() //프사받아오기
        FollowerAndFollowing()



        return fragmentView
    }
    // 팔로워 팔로잉 카운트
    fun FollowerAndFollowing(){
        firestore?.collection("users")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
            if(followDTO?.followingCount != null ){
                fragmentView?.account_tv_following_count?.text = followDTO?.followingCount?.toString()
            }
            if(followDTO?.followerCount != null ){
                fragmentView?.account_tv_follower_count?.text = followDTO?.followerCount?.toString()
                if(followDTO?.followers?.containsKey(currentUserUid!!)){
                    fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow_cancel)
                    fragmentView?.account_btn_follow_signout?.background?.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.colorLightGray),PorterDuff.Mode.MULTIPLY)
                }else{
                    if(uid != currentUserUid){
                        fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
                        fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                    }
                }
            }
        }
    }
    fun requestFollow(){
        // 내 계정에 데이터 저장
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followingCount = 1
                followDTO!!.followings[uid!!] = true

                transaction.set(tsDocFollowing,followDTO)
                return@runTransaction
            }
            if(followDTO.followings?.containsKey(uid)){
                //It remove following third person when a third person follow me
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            }
            else{
                //It add follings third person when a third person do not follow me
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }

            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }
        // 제 삼자(?) 데이터
        var tsDocFollower = firestore?.collection("users")?.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }

            if(followDTO!!.followers.containsKey(currentUserUid)){
                //It cancel my follower when i follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            }else{
                //It add my foller when i don't follow a thir person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }

    }

    fun followerAlarm(destinationUid:String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }

    fun ProfileImage(){ //내 프로필의 프사 정보 받아오기 (데이터 베이스 내 profile에 저장되어있음)
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
          //firestore?.collection("uid")?.document(uid.toString())?.collection("images")?.document("profile")?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            if(documentSnapshot.data != null){
                var url = documentSnapshot?.data!!["image"] // image
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(fragmentView?.account_iv_profile!!)

            }
        }
    }


        inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

            init {
                //firestore?.collection("uid")?.document(uid.toString())?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestore ->
/*                firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                    //Some times, This code return null of querySnapshot when it signout
                    if(querySnapshot == null) return@addSnapshotListener
                    //Get data
                    for(snapshot in querySnapshot.documents){
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }*/

                //게시물 최신순으로 표기
                firestore?.collection("uid")?.document(uid.toString())?.collection("images")?.orderBy("timestamp",
                    Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                //firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                    //Some times, This code return null of querySnapshot when it signout

                    Log.d("로그 : snapshot",querySnapshot.toString())
                    Log.d("로그 : firebaseFirestore",firebaseFirestore.toString())
                    Log.d("로그 : querySnapshot.documents",querySnapshot?.documents.toString())

                    if(querySnapshot == null) return@addSnapshotListener
                    //Get data
                    for(snapshot in querySnapshot.documents){
                        Log.d("로그 : snapshot in querySnapshot.documents",snapshot.toString())
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }


/*                //팔로워, 팔로잉 정보 받아오기
                var snapshotData :Map<String,Any>
                var followings : Map<String,String>
                var followers : Map<String,String>
                var dtr  = firestore?.collection("uid")?.document(uid.toString())
                dtr?.get()?.addOnSuccessListener { doc->
                    if (doc!=null){
                        snapshotData = doc.data as Map<String, Any>
                        followers = snapshotData.get("followers") as Map<String, String>
                        followings = snapshotData.get("followings") as Map<String, String>
                        Log.d("로그 snapshotData: ",snapshotData.toString())
                        Log.d("로그 followers: ",followers.toString())
                        Log.d("로그 followings: ",followings.toString())
                    }
                    notifyDataSetChanged()
                }
                Log.d("로그 팔로워dtr: ",dtr.toString())*/

                //추가해야할작업 : 팔로잉/팔로워 수 받아서 프래그먼트에 표시
                //notifyDataSetChanged() 처리 (굳이안해도될꺼같긴한데) 일단 지금은 새로고침 해야 업데이트 되게 구현해놓았음

            }//end of init

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                var width = resources.displayMetrics.widthPixels / 3
                var imageview = ImageView(parent.context)
                imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
                return CustomViewHolder(imageview)
            }
            inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {
            }
            override fun getItemCount(): Int {
                return contentDTOs.size
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                var imageview = (holder as CustomViewHolder).imageview
                Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
            }
        }
    }