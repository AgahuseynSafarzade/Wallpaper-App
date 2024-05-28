package com.example.agahdeneme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.agahdeneme.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username:String = intent.getStringExtra("username").toString()

        replaceFragment(Home(),username)
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.home -> replaceFragment(Home(),username)
                R.id.profile -> replaceFragment(Profile(),username)
                R.id.settings -> replaceFragment(Profil(),username)


                else ->{

                }
            }
            true

        }



    }
    override fun onBackPressed() {

    }
    private fun replaceFragment(fragment: Fragment,name: String){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentManager.fragments.remove(fragment.parentFragment)
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
        val args = Bundle()
        args.putString("username", name)
        fragment.arguments = args




    }
}