package com.example.gomop.navigation
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gomop.SignUpActivity
import com.example.gomop.MainActivity
import com.example.gomop.R
import com.example.gomop.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.fragment_user.view.*
class UserFragment : Fragment(){
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

        if(uid == currentUserUid){
            //My page
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity,SignUpActivity::class.java))
                auth?.signOut()
            }
        }
        else{
            //Other User page
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            var mainactivity = (activity as MainActivity)
            mainactivity?.toolbar_userEmail?.text = arguments?.getString("userId")
            mainactivity?.toolbar_btn_back?.setOnClickListener {
                mainactivity.bottom_navigation.selectedItemId = R.id.action_home
            }
            mainactivity?.toolbar_title_image?.visibility = View.GONE
            mainactivity?.toolbar_userEmail?.visibility = View.VISIBLE
            mainactivity?.toolbar_btn_back.visibility = View.VISIBLE
        }


        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
        return fragmentView
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


                firestore?.collection("uid")?.document(uid.toString())?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                //firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                    //Some times, This code return null of querySnapshot when it signout

                    //게시물 내림차순 구현해야함 ㅜㅜ
/*                    파이어스토어면 웟분 말씀대로하시구, 파이어베이스면 내림차순은 없구요
                    게시물 데이터노드에 타임스탬프값을 같이 저장해주신후에 orderbychild("timestamp")로
                            하면 시간값에따라 오름차순 정렬이되구요. 이것을 리사이클러뷰에 받아오신뒤
                            리사이클러뷰 함수중에 stackFromEnd와 reverselayout함수를 이용해서 역순으로
                            뿌려주시는 방법으로 가능해요*/


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
            }
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