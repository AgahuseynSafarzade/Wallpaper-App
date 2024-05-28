package com.example.agahdeneme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Glide.with(this) //Glide adlı bir kütüphanenin çağrılmasını sağlar. Bu kütüphane, resim yükleme işlemleri için kullanılır.
            .load("https://i.pinimg.com/originals/9f/c2/12/9fc2126eec2c0a3876e3f2097af9b983.gif")
            .into(findViewById(R.id.reg_login_image))  //belirtilen adresi kullanarak resmi yükleyip belirtilen ImageView nesnesine yerleştirir
        val register = findViewById<Button>(R.id.reg_button_register)
        val user_name = findViewById<EditText>(R.id.reg_user_name)
        val user_password = findViewById<EditText>(R.id.reg_user_password)
        val user_email = findViewById<EditText>(R.id.reg_user_email)
        //val mail_list = arrayOf("@gmail.com","@gmail.com.tr")

        register.setOnClickListener(){

            if (user_name.text.toString().trim() != "" && user_password.text.toString().trim() != "" && user_email.text.toString().trim() != "")
            {
                if ((user_email.text.toString().contains("@gmail.com"))||(user_email.text.toString().contains("@gmail.com.tr"))||
                    (user_email.text.toString().contains("@yandex.com"))||(user_email.text.toString().contains("@yandex.com.tr"))||
                    (user_email.text.toString().contains("@outlook.com"))||(user_email.text.toString().contains("@outlook.com.tr"))||
                    (user_email.text.toString().contains("@yahoo.com"))||(user_email.text.toString().contains("@yahoo.com.tr")))
                {
                    register(user_name.text.toString(),user_email.text.toString(),user_password.text.toString())
                }
                else
                {
                    Toast.makeText(this, "Geçerli bir mail servisi girin", Toast.LENGTH_SHORT).show()
                }
            }
            else
                Toast.makeText(this, "Kullanici adi ve ya sifre bos girilemez", Toast.LENGTH_SHORT).show()




        }
    }
    private fun register(uname: String,email: String, sifre: String) {


        val json = JSONObject()
        json.put("_id", uname)
        json.put("email", email)
        json.put("pass", sifre)
        val url = "http://10.0.2.2:5000/register"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->
                if (response.getString("message").toString() == "Registration Successful")
                {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).putExtra("username",uname)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()

                }


            },
            Response.ErrorListener { Toast.makeText(this, "Bir Hata Oluştu Tekrar Deneyin" , Toast.LENGTH_LONG).show()}) {}
        queue.add(jsonapi)

    }
}