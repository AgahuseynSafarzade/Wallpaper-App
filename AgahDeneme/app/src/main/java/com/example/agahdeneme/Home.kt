package com.example.agahdeneme

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.agahdeneme.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class Home : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var adapter: MyAdapter
    lateinit var genreadapter: GenreAdapter
    private lateinit var newsArrayList : ArrayList<News>
    private lateinit var genrerecyclerView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var genrenewsArrayList : ArrayList<GenreNews>
    val allimagesList = ArrayList<String>()
    val allidList = ArrayList<String>()
    val mainimagesList = ArrayList<String>()
    val mainidList = ArrayList<String>()
    private lateinit var binding : ActivityMainBinding
    var isScrolling = false
    private var username: String? = null
    private var state: String? = null
    private lateinit var dialog : BottomSheetDialog
    lateinit var progressDialog: ProgressDialog
    private var progressBar: ProgressBar? = null
    var count : Int = 0
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    companion object {

        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)

                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading...")
        progressDialog.window?.setGravity(Gravity.CENTER)
        progressDialog.setCancelable(false) // blocks UI interaction
        progressDialog.show()
        binding = ActivityMainBinding.inflate(layoutInflater)
        username = this.arguments?.getString("username")
        state = this.arguments?.getString("state")
        recyclerView = view.findViewById(R.id.recyclerview)


        activity?.let { Glide.get(it).clearMemory() }
        genrelist(view)
        if(state.toString()=="favorites")
        {
            favoriteslist(view,username.toString())
        }
        else
        {
            images_array(view,"null")
        }



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
            Response.ErrorListener {}) {}
        queue.add(jsonapi)

    }


    private fun images_array(view: View,genre : String) {
        progressDialog.show()
        newsArrayList = arrayListOf<News>()

        mainimagesList.clear()
        allimagesList.clear()
        mainidList.clear()
        allidList.clear()
        val json = JSONObject()
        json.put("genre", genre)
        val url = "http://10.0.2.2:5000/imagelist"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->
                val values =
                    response.getString("url").substring(0, response.getString("url").length - 1)
                val items: List<String> = values.split(",")
                for (item in items)
                    allimagesList.add(item)

                val values2 =
                    response.getString("id").substring(0, response.getString("id").length - 1)
                val items2: List<String> = values2.split(",")
                for (item2 in items2)
                    allidList.add(item2)

                val trylist = ArrayList<String>()
                val tempList = ArrayList<String>()

                for (item in allimagesList.indices)
                {
                    trylist.add(allimagesList[item])

                }
                for (item in allidList.indices)
                {
                    tempList.add(allidList[item])

                }
                allimagesList.shuffle()

                for (i in trylist.indices)
                {
                    for (k in allimagesList.indices)
                    {
                        if (trylist[i] == allimagesList[k])

                        {
                            allidList[k] = tempList[i]
                        }

                    }

                }
                count = if ((allimagesList.size - mainimagesList.size) > 30) {
                    30
                } else {
                    allimagesList.size - mainimagesList.size
                }

                for (i in 0 until count) {

                    mainimagesList.add(allimagesList[i])

                    val news = News(mainimagesList[i])
                    newsArrayList.add(news)

                }



                val layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)



                recyclerView.layoutManager = layoutManager
                recyclerView.setHasFixedSize(true)
                recyclerView.setItemViewCacheSize(30)
                adapter = MyAdapter(newsArrayList)
                recyclerView.adapter = adapter



                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!recyclerView.canScrollVertically(1)) {
                            onScrolledToBottom()
                        }
                    }
                })

                adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{

                    override fun onItemClick(position: Int) {

                        activity?.let{
                            logs(username.toString(),allidList[position])
                            image_count(username.toString(),allidList[position])
                            val intent = Intent (it, DecisionActivity::class.java).putExtra("username",username)
                                .putExtra("url",allimagesList[position]).putExtra("id",allidList[position])
                            it.startActivity(intent)
                        }
                    }


                })
                progressDialog.hide()

            },
            Response.ErrorListener {
                progressDialog.hide()
                Toast.makeText(activity, "Bir Sorun Oluştu" , Toast.LENGTH_LONG).show()
            }) {}
        queue.add(jsonapi)

    }


  @SuppressLint("SuspiciousIndentation", "NotifyDataSetChanged")
  fun onScrolledToBottom() {
      progressBar?.visibility  = View.VISIBLE
        if((allimagesList.size - mainimagesList.size)>=30)
        {
            for (i in count until count+30) {

                mainimagesList.add(allimagesList[i])
                mainidList.add(allidList[i])
                val news = News(mainimagesList[i])
                newsArrayList.add(news)

            }
            adapter.notifyItemRangeInserted(mainimagesList.size,count+30)

            count+=30
            adapter.notifyDataSetChanged()



        }
      else
        {
            var tempcount = allimagesList.size - mainimagesList.size
            for (i in count until tempcount) {

                mainimagesList.add(allimagesList[i])
                mainidList.add(allidList[i])
                val news = News(mainimagesList[i])
                newsArrayList.add(news)

            }
            count+=30
            adapter.notifyDataSetChanged()

        }
      progressBar?.visibility  = View.INVISIBLE

        }


    private fun favoriteslist(view: View,username: String) {
        progressDialog.show()
        newsArrayList = arrayListOf<News>()

        val json = JSONObject()
        json.put("username", username)
        val url = "http://10.0.2.2:5000/favoriteslist"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->
                if(response.getString("message") == "empty")
                {
                    Toast.makeText(activity, "Beğenilenleriniz Boş!", Toast.LENGTH_SHORT).show()
                    images_array(view,"")
                }
                if(response.getString("message") == "notempty")
                {
                    val values =
                        response.getString("url").substring(0, response.getString("url").length - 1)
                    val items: List<String> = values.split(",")
                    for (item in items)
                        allimagesList.add(item)

                    val values2 =
                        response.getString("id").substring(0, response.getString("id").length - 1)
                    val items2: List<String> = values2.split(",")
                    for (item2 in items2)
                        allidList.add(item2)

                    allimagesList.reverse()
                    allidList.reverse()
                    for (i in allidList.indices) {

                        val news = News(allimagesList[i])
                        newsArrayList.add(news)

                    }



                    val layoutManager = GridLayoutManager(view.context,GridLayoutManager.VERTICAL)

                    recyclerView.layoutManager = layoutManager
                    recyclerView.setHasFixedSize(true)

                    adapter = MyAdapter(newsArrayList)
                    recyclerView.adapter = adapter
                    adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{


                        override fun onItemClick(position: Int) {

                            activity?.let{
                                logs(username.toString(),allidList[position])
                                val intent = Intent (it, DecisionActivity::class.java).putExtra("username",username)
                                    .putExtra("url",allimagesList[position]).putExtra("id",allidList[position])
                                it.startActivity(intent)
                            }
                        }


                    })
                }
                progressDialog.hide()
            },
            Response.ErrorListener { Toast.makeText(activity, "Bir Sorun Oluştu" , Toast.LENGTH_LONG).show()
           }) {}
        queue.add(jsonapi)

    }


    private fun genrelist(view: View) {

        genrenewsArrayList = arrayListOf<GenreNews>()
        val titleList = ArrayList<String>()
        val json = JSONObject()
        val url = "http://10.0.2.2:5000/get_genres"
        val queue = Volley.newRequestQueue(activity)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->


                val values =
                    response.getString("title").substring(0, response.getString("title").length - 1)
                val items: List<String> = values.split(",")
                for (item in items)
                    titleList.add(item)


                for (i in titleList.indices) {

                    val news = GenreNews(titleList[i])
                    genrenewsArrayList.add(news)


                    val layoutManager = GridLayoutManager(context,1,RecyclerView.HORIZONTAL,false)
                    genrerecyclerView = view.findViewById(R.id.genrerecyclerview)
                    genrerecyclerView.layoutManager = layoutManager
                    genrerecyclerView.setHasFixedSize(true)
                    genreadapter = GenreAdapter(genrenewsArrayList)
                    genrerecyclerView.adapter = genreadapter
                    genreadapter.setOnItemClickListener(object : GenreAdapter.onItemClickListener{


                        override fun onItemClick(position: Int) {

                            activity?.let{
                                images_array(view,titleList[position])
                            }
                        }
                    })
                }

            },
            Response.ErrorListener { Toast.makeText(activity, "Bir Sorun Oluştu" , Toast.LENGTH_LONG).show() }) {}
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
}







