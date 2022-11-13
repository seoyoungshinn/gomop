package com.example.gomop.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gomop.MyAdapter
import com.example.gomop.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val fbdb = Firebase.firestore //파이어베이스.파이어스토어 설정

    override fun onCreateView(inflater: LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)


        viewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true)
        // 2
        viewAdapter = MyAdapter()

        // 3
        /*recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }*/

        val btn2 = view.findViewById<Button>(R.id.searchBtn)
        val edittext1 = view.findViewById<EditText>(R.id.searchWord)
        val textv1 = view.findViewById<TextView>(R.id.textView)
        textv1.visibility = View.INVISIBLE
        btn2.setOnClickListener { //버튼 2 이벤트 처리 , 읽기 버튼

            val aPlayerId = edittext1.text        //firestore  player id로 찾을 문서 string

            fbdb.collection("player") //첫번째칸 컬렉션 (player 부분 필드데이터를 전부 읽음)
                .get()
                .addOnCompleteListener { task ->

                    var afound = false  //데이터 찾지 못했을때

                    if (task.isSuccessful) { //제대로 접근 했다면
                        for (i in task.result!!) {
                            if (i.id == aPlayerId.toString()) { //입력한 데이터와 같은 이름이 있다면(player id 부분)
                                val theNickName = i.data["nickname"] //필드 데이터
                                /*textv1.text =
                                    theNickName.toString()   //text1에 읽은 nicknmae 필드 데이터 입력*/

                                var fragment = UserFragment()
                                var bundle = Bundle()
                                var uid = theNickName.toString()
                                bundle.putString("destinationUid",uid)
                               fragment.arguments = bundle
                                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()

                                afound = true  //찾았다

                                break
                            } //if (task.
                        } //for

                        if (!afound) {  //해당 데이터 찾지 못했다면
                            textv1.visibility = View.VISIBLE
                            textv1.setText("\"${edittext1.text}\"검색 결과 없음")
                        }

                    } else { //오류 발생시
                        textv1.setText("Task fail")
                    }
                }
        } //버튼

        return view
    }
}