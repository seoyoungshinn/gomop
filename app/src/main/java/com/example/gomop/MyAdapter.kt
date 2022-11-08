package com.example.gomop

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gomop.navigation.SearchFragment


class MyAdapter() :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var itemimage: ImageView = itemView.findViewById(R.id.profile_image)
        public var itemtitle: TextView = itemView.findViewById(R.id.user_name_txt)
        public var itemdetail: TextView = itemView.findViewById(R.id.user_uid_txt)
    }

    // 1. Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {


        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)

        return MyViewHolder(cardView)
    }




    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.itemimage.setImageResource(R.drawable.ic_notice)

        holder.itemtitle.setText("아이디 입니다")
        holder.itemdetail.setText("UID 입니다")
    }


    // 3. Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return 5
    }
}