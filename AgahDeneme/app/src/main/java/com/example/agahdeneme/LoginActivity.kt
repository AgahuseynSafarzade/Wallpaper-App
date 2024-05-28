package com.example.agahdeneme

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.reflect.Method
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide


class LoginActivity : AppCompatActivity() { //LoginActivity sınıfı tanımlar ve bu sınıfın AppCompatActivity sınıfından türediğini belirtir.



    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) //uygulama etkinliğinde kullanılacak olan arayüzün belirler
        Glide.with(this) //Glide adlı bir kütüphanenin çağrılmasını sağlar. Bu kütüphane, resim yükleme işlemleri için kullanılır.
            .load("https://i.pinimg.com/originals/9f/c2/12/9fc2126eec2c0a3876e3f2097af9b983.gif")
            .into(findViewById(R.id.login_image))  //belirtilen adresi kullanarak resmi yükleyip belirtilen ImageView nesnesine yerleştirir





        val signin = findViewById<Button>(R.id.button_signin)
        val user_name = findViewById<EditText>(R.id.user_name)
        val user_password = findViewById<EditText>(R.id.user_password)
        val forgot_password = findViewById<TextView>(R.id.forgot_password)
        val register = findViewById<Button>(R.id.button_register)
        register.setOnClickListener(){

                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
        }
        signin.setOnClickListener(){

            if (user_name.text.toString().trim() != "" && user_password.text.toString().trim() != "")
            {
                login(user_name.text.toString(),user_password.text.toString())
            }
            else
                Toast.makeText(this, "Kullanici adi ve ya sifre bos girilemez", Toast.LENGTH_SHORT).show()


        }
        forgot_password.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        val ttb = AnimationUtils.loadAnimation(this,R.anim.ttb)
        val headertitle=findViewById(R.id.loginText) as TextView

        headertitle.startAnimation(ttb)

        val ttb2 = AnimationUtils.loadAnimation(this,R.anim.ttb2)
        val ttb3 = AnimationUtils.loadAnimation(this,R.anim.ttb3)
        val scale_anim = AnimationUtils.loadAnimation(this,R.anim.scale_anim)
        val user_text_anim=findViewById(R.id.user_name) as EditText
        val user_password_anim=findViewById(R.id.user_password) as EditText
        val button_signin_anim=findViewById(R.id.button_signin) as Button
        val button_register_anim=findViewById(R.id.button_register) as Button

        button_signin_anim.startAnimation(scale_anim)
        button_register_anim.startAnimation(scale_anim)
        user_text_anim.startAnimation(ttb2)
        user_password_anim.startAnimation(ttb3)

    }




    override fun onBackPressed() {

    }

    private fun login(uname: String, sifre: String) {


        val json = JSONObject()
        json.put("_id", uname)
        json.put("pass", sifre)
        val url = "http://10.0.2.2:5000/login"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->

                if(response.getString("message") == "Success")
                {
                    val intent = Intent(this, MainActivity::class.java).putExtra("username",uname)
                    startActivity(intent)
                }
                else if (response.getString("message") == "Wrong Password")
                {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Kullanıcı adı veya şifre yanlış" , Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { Toast.makeText(this, "Kullanıcı adı veya şifre yanlış" , Toast.LENGTH_LONG).show()}) {}
        queue.add(jsonapi)

    }

}

