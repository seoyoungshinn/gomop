package com.example.gomop.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gomop.R
import com.example.gomop.DataClassObject.AlarmDTO
import com.example.gomop.DataClassObject.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentActivity : AppCompatActivity() {
    val fbdb = Firebase.firestore
    var uid :String? = null
    var contentUid :String ?= null
    var destinationUid : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        uid = FirebaseAuth.getInstance().currentUser?.uid
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        contentUid = intent.getStringExtra("contentUid")
        destinationUid = intent.getStringExtra("destinationUid")

        comment_recycleview.adapter = CommentRecyclerviewAdapter()
        comment_recycleview.layoutManager = LinearLayoutManager(this)


        comment_btn_send?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = comment_edit_message.text.toString()
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance().collection("images").document(contentUid!!).collection("comments").document().set(comment)
            commentAlarm(destinationUid!!,comment_edit_message.text.toString())
            comment_edit_message.setText("")
        }
    }
    fun commentAlarm(destinationUid : String, message : String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
        alarmDTO.kind = 1
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.message = message
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
    }
    inner class CommentRecyclerviewAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
        init{
            FirebaseFirestore.getInstance()
                .collection("images")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null) return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)

            return CustomViewHolder(view)
        }
        private inner class CustomViewHolder(view :View): RecyclerView.ViewHolder(view)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView

            /*fbdb.collection("uid") //???????????? ????????? (player ?????? ?????????????????? ?????? ??????)
                .get()
                .addOnCompleteListener { task ->
                    var afound = false  //????????? ?????? ????????????
                    if (task.isSuccessful) { //????????? ?????? ?????????
                        for (i in task.result!!) {
                            if (i.id == uid) { //????????? ???????????? ?????? ????????? ?????????(player id ??????)
                                val theNickName = i.data["id"] //?????? ?????????
                                val str_0 = theNickName.toString()
                                view.commentviewitem_textview_profile.text = str_0
                            } //if (task.
                        } //for
                    }
                }*/
            var email : String? = comments[position].userId
            var splitedId :String = email!!.split("@").get(0)


            view.commentviewitem_textview_comment.text = comments[position].comment
            view.commentviewitem_textview_profile.text = splitedId

            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .get()
                .addOnCompleteListener { task->
                    if(task.isSuccessful){
                        val url = task.result!!["image"]
                        Glide.with(view.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                    }

                }

        }



        override fun getItemCount(): Int {
            return comments.size
        }

    }
}