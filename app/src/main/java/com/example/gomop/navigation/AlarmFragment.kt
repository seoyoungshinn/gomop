package com.example.gomop.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gomop.DataClassObject.AlarmDTO
import com.example.gomop.R
import com.example.gomop.UserHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_home.*
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*

class AlarmFragment : Fragment(){
    val fbdb = Firebase.firestore
    var uid :String? = null
    var str_9 :String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)

        view.alarmfragment_recycleview.adapter = AlarmRecyclerviewAdapter()
        view.alarmfragment_recycleview.layoutManager = LinearLayoutManager(activity)
        uid = arguments?.getString("destinationUid")
        return view

    }
    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmDTOList.clear()
                if(querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot.documents){
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view : View): RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder.itemView



            FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList[position].uid!!).get().addOnCompleteListener { task->
                if(task.isSuccessful){
                    val url = task.result!!["image"]
                    Glide.with(view.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                }

            }







            when(alarmDTOList[position].kind){
                0->{
                    fbdb.collection("uid") //???????????? ????????? (player ?????? ?????????????????? ?????? ??????)
                        .get()
                        .addOnCompleteListener { task ->

                            var afound = false  //????????? ?????? ????????????

                            if (task.isSuccessful) { //????????? ?????? ?????????
                                //Log.d("1","uid????????????")
                                for (i in task.result!!) {
                                    //Log.d("s","${alarmDTOList[position].uid}")
                                    if (i.id == alarmDTOList[position].uid) { //????????? ???????????? ?????? ????????? ?????????(player id ??????)
                                        //Log.d("2","${uid} ???????????? ??????")
                                        val theNickName = i.data["id"] //?????? ?????????
                                        /*textv1.text =
                                            theNickName.toString()   //text1??? ?????? nicknmae ?????? ????????? ??????*/
                                        //Log.d("3",theNickName.toString())
                                        str_9 = theNickName.toString()
                                        val str_0 = str_9  +" "+ getString(R.string.alarm_favorite)
                                        view.commentviewitem_textview_profile.text = str_0

                                    } //if (task.
                                } //for
                            }
                        }

                }
                1->{
                    fbdb.collection("uid") //???????????? ????????? (player ?????? ?????????????????? ?????? ??????)
                        .get()
                        .addOnCompleteListener { task ->

                            var afound = false  //????????? ?????? ????????????

                            if (task.isSuccessful) { //????????? ?????? ?????????
                                //Log.d("1","uid????????????")
                                for (i in task.result!!) {
                                    //Log.d("s","${alarmDTOList[position].uid}")
                                    if (i.id == alarmDTOList[position].uid) { //????????? ???????????? ?????? ????????? ?????????(player id ??????)
                                        //Log.d("2","${uid} ???????????? ??????")
                                        val theNickName = i.data["id"] //?????? ?????????
                                        /*textv1.text =
                                            theNickName.toString()   //text1??? ?????? nicknmae ?????? ????????? ??????*/
                                        //Log.d("3",theNickName.toString())
                                        str_9 = theNickName.toString()
                                        val str_0 = str_9 +" "+ getString(R.string.alarm_comment)+ " of "+ alarmDTOList[position].message
                                        view.commentviewitem_textview_profile.text = str_0

                                    } //if (task.
                                } //for
                            }
                        }

                }
                2->{
                    fbdb.collection("uid") //???????????? ????????? (player ?????? ?????????????????? ?????? ??????)
                        .get()
                        .addOnCompleteListener { task ->

                            var afound = false  //????????? ?????? ????????????

                            if (task.isSuccessful) { //????????? ?????? ?????????
                                //Log.d("1","uid????????????")
                                for (i in task.result!!) {
                                    //Log.d("s","${alarmDTOList[position].uid}")
                                    if (i.id == alarmDTOList[position].uid) { //????????? ???????????? ?????? ????????? ?????????(player id ??????)
                                        //Log.d("2","${uid} ???????????? ??????")
                                        val theNickName = i.data["id"] //?????? ?????????
                                        /*textv1.text =
                                            theNickName.toString()   //text1??? ?????? nicknmae ?????? ????????? ??????*/
                                        //Log.d("3",theNickName.toString())
                                        str_9 = theNickName.toString()
                                        val str_0 = str_9 +" " + getString(R.string.alarm_follow)
                                        view.commentviewitem_textview_profile.text = str_0
                                    } //if (task.
                                } //for
                            }
                        }

                }
            }
            view.commentviewitem_textview_comment.visibility = View.INVISIBLE
        }

        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

    }
}