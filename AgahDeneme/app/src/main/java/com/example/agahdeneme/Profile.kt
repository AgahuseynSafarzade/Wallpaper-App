package com.example.agahdeneme

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.util.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var adapter: MyAdapter
private lateinit var recyclerView: RecyclerView
private lateinit var newsArrayList : ArrayList<News>

class Profil : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profil().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var username_text = view.findViewById<TextView>(R.id.user_name_text)
        var new_pass = view.findViewById<EditText>(R.id.new_password)
        //fragment oluştuğunda yapılacak aktiviteler tanımlanıyor
        username = this.arguments?.getString("username")
        username_text.text = username.toString()
        view.findViewById<Button>(R.id.update_button).setOnClickListener() {
            if (new_pass.text.trim().toString() != "") {
                update_password(username.toString(),new_pass.text.toString())
            }

        }

        Glide.with(this)
            .load("https://i.pinimg.com/originals/ba/3d/73/ba3d738757089a28da691cf46235428b.gif")
            .into(view.findViewById(R.id.settings_image))

        view.findViewById<Button>(R.id.exit_button).setOnClickListener() {
            activity?.let {
                val intent = Intent (it, LoginActivity::class.java)
                it.startActivity(intent)

            }}
        view.findViewById<Button>(R.id.favorites_button).setOnClickListener() {
            activity?.let {
                val b = Bundle()
                b.putString("state", "favorites")
                b.putString("username", username.toString())
                val fragmentA = Home()
                fragmentA.arguments = b


                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout,fragmentA)
                    .commit()





            }}
    }


    private fun update_password(username: String,newpass: String) {

        val json = JSONObject()
        json.put("_id",username)
        json.put("newpass",newpass)
        val url = "http://10.0.2.2:5000/updatepassword"
        val queue = Volley.newRequestQueue(activity)

        val jsonapi: JsonObjectRequest =object :JsonObjectRequest(
            Method.POST,
            url,
            json,
            Response.Listener { response ->


                Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener { Toast.makeText(activity,"Sifre Guncellenemedi!",Toast.LENGTH_LONG).show()}){}
        queue.add(jsonapi)
    }

    //fragment yüklendiğinde resmin urli api tarafından göndürülüyor ve dönen url glide ile imageviewa çiziliyor


}

