package com.cookandroid.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookandroid.myapplication.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicAdapter: Adapter

    companion object{
        lateinit var MusicListMA: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ///Button bindings

        ///셔플 버튼 -> PlayMusicActivity
        binding.shuffleBtn.setOnClickListener{
            val intent = Intent(this@MainActivity,PlayMusicActivity::class.java)
            startActivity(intent)
        }
        ///플레이리스트 버튼 -> PlaylistActivity
        binding.playlistBtn.setOnClickListener{
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        ///넥스트 버튼 -> PlayNext
        binding.playNextBtn.setOnClickListener{
            startActivity((Intent(this@MainActivity,PlayNext::class.java)))
        }
    }

    ///런타임 권한요청(내부 저장소 접근)
    private fun requestRuntimePermission(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
            else
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){
        requestRuntimePermission()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.musicRV.setHasFixedSize(true)
        val musicList = ArrayList<String>()
        MusicListMA = getAllAudio()
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager= LinearLayoutManager(this@MainActivity)
        musicAdapter = Adapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSongs.text = "Total Songs : "+musicAdapter.itemCount
    }
    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            MediaStore.Audio.Media.DATE_ADDED+"DESC", null)
        if(cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, artUri = artUriC)
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }
}