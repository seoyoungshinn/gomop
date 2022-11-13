package com.example.gomop.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gomop.R
import com.example.gomop.DataClassObject.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class HomeFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid : String? = null
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        view.homefragment_recyclerview.adapter = HomeRecyclerViewAdapter()
        view.homefragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    inner class HomeRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()
        init {
            // var storageRef = storage?.reference?.child("uid")?.child(uid)?.child("images")
            //firestore?.collection("uid")?.document(uid)?.collection("images")?.orderBy("timestamp")
          //  firestore?.collection("images")?.orderBy("timestamp")
            firestore?.collection("uid")?.document(uid.toString())?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView
            //UserId
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![position].userId
            //Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl)
                .into(viewholder.detailviewitem_imageview_content)
            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain
            //likes
            viewholder.detailviewitem_favoritecounter_textview.text =
                "Likes " + contentDTOs!![position].favoriteCount
            //ProfileImage
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl)
                .into(viewholder.detailviewitem_profile_image)
            //This code is when the button is clicked
            viewholder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }
            //This code is when the page is loaded
            if(contentDTOs!![position].favorites.containsKey(uid)){
                //This is like status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.heart_black)
            }else{
                //This is unlike status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.heart_white)
            }


            viewholder.detailviewitem_profile_image.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)
                bundle.putString("userId",contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }
            viewholder.detailviewitem_comment_imageview.setOnClickListener{ v ->
                var intent = Intent(v.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }


        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if (contentDTO!!.favorites.containsKey(uid)) {
                    //When the button is clicked
                    contentDTO?.favoriteCount = contentDTO.favoriteCount?.minus(1)
                    contentDTO?.favorites.remove(uid)
                } else {
                    //When the button is no clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount?.plus(1)
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc,contentDTO)
            }
        }
    }
}