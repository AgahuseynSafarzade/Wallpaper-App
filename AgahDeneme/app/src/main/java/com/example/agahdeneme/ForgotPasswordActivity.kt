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

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        Glide.with(this) //Glide adlı bir kütüphanenin çağrılmasını sağlar. Bu kütüphane, resim yükleme işlemleri için kullanılır.
            .load("https://i.pinimg.com/originals/9f/c2/12/9fc2126eec2c0a3876e3f2097af9b983.gif")
            .into(findViewById(R.id.forgot_password_image))  //belirtilen adresi kullanarak resmi yükleyip belirtilen ImageView nesnesine yerleştirir
        val mailsend = findViewById<Button>(R.id.send_mail)
        val user_email = findViewById<EditText>(R.id.forgot_password_mail)
        mailsend.setOnClickListener(){
            if(user_email.text.toString().trim() != ""){
                forgot_pass(user_email.text.toString().trim())
            }
            else
            {
                Toast.makeText(this, "Email Alanı boş geçilemez", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun forgot_pass(email: String) {

        val json = JSONObject()
        json.put("email", email)
        val url = "http://10.0.2.2:5000/emailsend"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->
                if(response.getString("message").toString() == "Password sent to your mail address")
                {
                    Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
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