package com.example.agahdeneme

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profile : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsArrayList : ArrayList<News>
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recomender, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = this.arguments?.getString("username")
        recommendation(username.toString(),view)


    }




    private fun recommendation(uname: String,view: View) {

        newsArrayList = arrayListOf<News>()
        val imagesList = ArrayList<String>()
        val idList = ArrayList<String>()
        val json = JSONObject()
        json.put("username", uname)
        val url = "http://10.0.2.2:5000/recommendation"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->
                val values =
                    response.getString("url").substring(0, response.getString("url").length - 1)
                val items: List<String> = values.split(",")
                for (item in items)
                    imagesList.add(item)
                val values2 =
                    response.getString("id").substring(0, response.getString("id").length - 1)
                val items2: List<String> = values2.split(",")
                for (item2 in items2)
                    idList.add(item2)
                for (i in imagesList.indices) {
                    val news = News(imagesList[i])
                    newsArrayList.add(news)
                }
                val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recyclerView = view.findViewById(R.id.recyclerview)
                recyclerView.layoutManager = layoutManager
                recyclerView.setHasFixedSize(true)
                adapter = MyAdapter(newsArrayList)
                recyclerView.adapter = adapter
                adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{

                    override fun onItemClick(position: Int) {

                        activity?.let{
                            logs(username.toString(),idList[position])
                            image_count(username.toString(),idList[position])
                            val intent = Intent (it, DecisionActivity::class.java).putExtra("username",username)
                                .putExtra("url",imagesList[position]).putExtra("id",idList[position])
                            it.startActivity(intent)
                        }
                    }
                })
            },
            Response.ErrorListener { Toast.makeText(activity, "Bir Sorun Oluştu" , Toast.LENGTH_LONG).show()}) {}
        queue.add(jsonapi)
    }




    private fun logs(uname: String,imageid: String) {

        val json = JSONObject()
        json.put("username", uname)
        json.put("imageid", imageid)
        val url = "http://10.0.2.2:5000/logs"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->

            },
            Response.ErrorListener { }) {}
        queue.add(jsonapi)

    }
    private fun image_count(uname: String, image_id: String) {

        val json = JSONObject()
        json.put("image_id", image_id)
        json.put("username", uname)
        val url = "http://10.0.2.2:5000/image_count"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response ->

            },
            Response.ErrorListener {
                Toast.makeText(
                    activity,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }) {}
        queue.add(jsonapi)
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}