package com.example.winwintest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.winwintest.databinding.ActivityMainBinding
import com.example.winwintest.databinding.RecyclerInnerItemBinding
import com.example.winwintest.databinding.RecyclerItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerData: Data
    private lateinit var adapter: OuterRecycler
    private lateinit var innerAdapter: InnerRecycler
    private lateinit var binding: ActivityMainBinding
    private lateinit var decoration: Decoration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        decoration = Decoration(8)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java).getData()
        api.enqueue(object: Callback<jsonResult>{
            override fun onResponse(call: Call<jsonResult>, response: Response<jsonResult>) {
                if(response.isSuccessful){
                    recyclerData = response.body()?.data!!
                    binding.textViewTitle.text = "totalCount: ${response.body()?.data!!.totalCount}"

                    binding.recyclerOuter.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.recyclerOuter.addItemDecoration(decoration)
                    adapter = OuterRecycler()
                    binding.recyclerOuter.adapter = adapter
                } else {
                    Toast.makeText(this@MainActivity, "資料異常", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<jsonResult>, t: Throwable) {
                Toast.makeText(this@MainActivity, "連線異常", Toast.LENGTH_SHORT).show()
            }
        })
    }

    inner class OuterRecycler: RecyclerView.Adapter<OuterItem>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OuterItem {
            val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return OuterItem(binding)
        }

        override fun onBindViewHolder(holder: OuterItem, position: Int) {
            holder.name.text = recyclerData.items[position].user.nickName
            Glide.with(this@MainActivity)
                .load(recyclerData.items[position].user.imageUrl)
                .transform(CircleCrop())
                .into(holder.userImage)
            holder.innerRecycler.layoutManager = GridLayoutManager(this@MainActivity,5, GridLayoutManager.VERTICAL, false)
            innerAdapter = InnerRecycler(recyclerData.items[position].tags)
            holder.innerRecycler.adapter = innerAdapter
        }

        override fun getItemCount(): Int {
            return recyclerData.items.size
        }
    }

    inner class InnerRecycler(private val tagList: List<String>): RecyclerView.Adapter<InnerItem>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerItem {
            val binding = RecyclerInnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InnerItem(binding)
        }

        override fun onBindViewHolder(holder: InnerItem, position: Int) {
            holder.tag.text = tagList[position]
        }

        override fun getItemCount(): Int {
            return tagList.size
        }
    }
}

class OuterItem(itemView: RecyclerItemBinding):ViewHolder(itemView.root){
    val userImage = itemView.imageViewUser
    val name = itemView.textViewName
    val innerRecycler = itemView.recyclerTag

}

class InnerItem(innerItemView: RecyclerInnerItemBinding): ViewHolder(innerItemView.root){
    val tag = innerItemView.textView
}


interface ApiService{
    @GET("/winwiniosapp/interview/main/interview.json")
    fun getData(): Call<jsonResult>
}