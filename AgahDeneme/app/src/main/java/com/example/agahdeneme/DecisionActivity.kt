package com.example.agahdeneme
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.io.File
import java.io.IOException


class DecisionActivity : AppCompatActivity() {
    private var per=0
    private val requestPermision=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        per=if(it){1}else{0}
    }
    private lateinit var user:String
    private lateinit var url:String
    private lateinit var title:String
    private lateinit var id:String
    private lateinit var favoritebutton:ImageButton
    private lateinit var control:String
    var img:ImageView? = null
    private lateinit var adapter: MyAdapterRec
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsArrayList : ArrayList<RecNews>
    private lateinit var dialog : BottomSheetDialog
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f
    private var mBottomSheetLayout: LinearLayout? = null
    private var sheetBehavior: BottomSheetBehavior<*>? = null
    private var header_Arrow_Image: ImageView? = null



    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decision)
        requestPermision.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermision.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        user = intent.getStringExtra("username").toString()
        url = intent.getStringExtra("url").toString()
        id = intent.getStringExtra("id").toString()
        control = "control"
        recommendation(user.toString())
        img = findViewById(R.id.title_image)
        Glide.with(this)
            .load(url)
            .into(findViewById(R.id.title_image))
        val menubtn=findViewById<ImageButton>(R.id.popupmenubtn)
        class ScaleListener : SimpleOnScaleGestureListener() {
            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                mScaleFactor *= scaleGestureDetector.scaleFactor
                findViewById<ImageView>(R.id.title_image).scaleX = mScaleFactor
                findViewById<ImageView>(R.id.title_image).scaleY = mScaleFactor
                return true
            }
        }
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_layout));
        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow)
        menubtn.setOnClickListener(){
            val popupMenu : PopupMenu = PopupMenu(this,menubtn)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener (PopupMenu.OnMenuItemClickListener {
                item -> when(item.itemId) {
                    R.id.share-> {
                        val bitmapDrawable = img!!.drawable as BitmapDrawable
                        val bitmap = bitmapDrawable.bitmap
                        val bitmapPath = MediaStore.Images.Media.insertImage(contentResolver,bitmap,"Some Title",null)
                        val bitmapUri = Uri.parse(bitmapPath)
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM,bitmapUri)
                        startActivity(Intent.createChooser(intent,"Share Image"))
                    }
                    R.id.download-> {
                        downloadImageNew(url)
                    }
                    R.id.backgroundwallpaper-> {
                        setWallpaper()
                    }
                }
                true

            })
            popupMenu.show()
        }
        favoritebutton =findViewById(R.id.likedbtn)
        favorite(user,id,control)
        favoritebutton.setOnClickListener(){
            when (control) {
                "true" -> {
                    control = "remove"
                    favorite(user,id,control)
                }
                "false" -> {
                    control = "add"
                    favorite(user,id,control)
                }
                "remove" -> {
                    control = "add"
                    favorite(user,id,control)
                }
                "add" -> {
                    control = "remove"
                    favorite(user,id,control)
                }
            }
        }
    }




    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mScaleGestureDetector!!.onTouchEvent(event)
    }

    private fun favorite(uname: String, imageid: String,state:String) {

        val json = JSONObject()
        json.put("name", uname)
        json.put("imageid", imageid)
        json.put("control", state)
        val url = "http://10.0.2.2:5000/liked"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->


                if(response.getString("message").toString() == "true")
                {
                    control = "true"
                    favoritebutton.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
                if(response.getString("message").toString() == "false")
                {
                    control = "false"
                    favoritebutton.setImageResource(R.drawable.ic_baseline_favorite_24_white)
                }
                if(response.getString("message").toString() == "add")
                {
                    Toast.makeText(this, "Picture Liked", Toast.LENGTH_SHORT).show()
                    control = "add"
                    favoritebutton.setImageResource(R.drawable.ic_baseline_favorite_24)


                    val scale_anim = AnimationUtils.loadAnimation(this,R.anim.scale_anim)
                    favoritebutton.startAnimation(scale_anim)

                }
                if(response.getString("message").toString() == "remove")
                {
                    Toast.makeText(this, "Removed From Your Picture Likes", Toast.LENGTH_SHORT).show()
                    control = "remove"
                    favoritebutton.setImageResource(R.drawable.ic_baseline_favorite_24_white)
                }

            },
            Response.ErrorListener { Toast.makeText(this,it.message.toString() , Toast.LENGTH_LONG).show()}) {}
        queue.add(jsonapi)

    }




    private fun setWallpaper() {


        val wallpaperManager = WallpaperManager.getInstance(baseContext)
        wallpaperManager.setBitmap(findViewById<ImageView>(R.id.title_image).drawToBitmap())
        Toast.makeText(this, "Wallpaper set", Toast.LENGTH_SHORT).show()
    }

    fun setLockWallpaper() {

        val wallpaperManager = WallpaperManager.getInstance(this)

        try {

                //wallpaperManager.setBitmap(findViewById<ImageView>(R.id.title_image).drawToBitmap(), null, true, WallpaperManager.FLAG_LOCK)
                Toast.makeText(this, "Lock Screen Set Wallpaper!", Toast.LENGTH_SHORT).show()


        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }






    private fun downloadImageNew(downloadUrlOfImage: String) {
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("WallyBest")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator.toString() + "WallyBest" + ".png"
                )
            dm.enqueue(request)
            Toast.makeText(this, "Image download started.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Image download failed.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun recommendation(uname: String) {

        newsArrayList = arrayListOf<RecNews>()

        val imagesList = ArrayList<String>()
        val idList = ArrayList<String>()
        val json = JSONObject()
        json.put("username", uname)

        val url = "http://10.0.2.2:5000/recommendation"
        val queue = Volley.newRequestQueue(this)
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

                    val news = RecNews(imagesList[i])
                    newsArrayList.add(news)

                }

                /*val dialogView = layoutInflater.inflate(R.layout.bottom_sheet,null)
                dialog = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
                dialog.setContentView(dialogView)
                dialog.behavior.peekHeight = 100
                val window: Window? = dialog.window
                val wlp: WindowManager.LayoutParams? = window?.attributes


                if (wlp != null) {
                    wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
                }
                if (window != null) {
                    window.attributes = wlp
                }
               *//* dialog.setOnDismissListener(){
                   dialog.show()
                }*/

                val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                recyclerView = findViewById(R.id.reccyclerview)
                recyclerView.layoutManager = layoutManager
                recyclerView.setHasFixedSize(true)
                adapter = MyAdapterRec(newsArrayList)
                recyclerView.adapter = adapter
                header_Arrow_Image!!.setOnClickListener {
                    if (sheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        sheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                }
                sheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {}
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        header_Arrow_Image!!.rotation = slideOffset * 180
                    }
                })
                adapter.setOnItemClickListener(object : MyAdapterRec.onItemClickListener{


                    override fun onItemClick(position: Int) {


                            logs(uname.toString(),idList[position])
                            image_count(uname.toString(),idList[position])
                            this@DecisionActivity.finish()
                            val intent =Intent(this@DecisionActivity, DecisionActivity::class.java).putExtra("username",uname)
                                .putExtra("url",imagesList[position]).putExtra("id",idList[position])
                            startActivity(intent)

                    }
                })


            },
            Response.ErrorListener { Toast.makeText(this, "Bir Sorun Oluştu" , Toast.LENGTH_LONG).show()
          }) {}
        queue.add(jsonapi)
    }




    private fun image_count(uname: String, image_id: String) {


        val json = JSONObject()
        json.put("image_id", image_id)
        json.put("username", uname)
        val url = "http://10.0.2.2:5000/image_count"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response ->

            },
            Response.ErrorListener {
                Toast.makeText(
                    this,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }) {}
        queue.add(jsonapi)
    }



    private fun logs(uname: String,imageid: String) {



        val json = JSONObject()
        json.put("username", uname)
        json.put("imageid", imageid)
        val url = "http://10.0.2.2:5000/logs"
        val queue = Volley.newRequestQueue(this)
        val jsonapi: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, json,
            Response.Listener { response->

            },
            Response.ErrorListener { }) {}
        queue.add(jsonapi)

    }

}