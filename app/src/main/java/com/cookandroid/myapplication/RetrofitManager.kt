package com.cookandroid.myapplication

import android.util.Log
import com.tftf.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {

    private val retrofit = Retrofit.Builder()
        .baseUrl(MusicServiceConnection.serverUrl)
        .addConverterFactory(NullOnEmptyConverterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(RetrofitAPI::class.java)


    private fun <T> enqueueCall(callApi: Call<T>, callbackOperation:(T?)->Unit) {
        val api = retrofit.create(RetrofitAPI::class.java)

        /** api를 통해 서버에 request를 보냄 */
        callApi.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                /** response가 정상적으로 왔다면 operation 함수 호출 */
                Log.d("tftf-RetrofitManager", "success : ${response.raw()}")
                callbackOperation(response.body())
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d("tftf-RetrofitManager", "failure : $t")
            }
        })
    }


    /** 호출 시 id를 통해 메타데이터를 서버에 요청 */
    fun getMusicMetadata(musicID: Int, callbackOperation:(Music?)->Unit) {
        enqueueCall(api.getMetadata(musicID), callbackOperation)
    }

    /** 호출 시 id 목록을 통해 메타데이터를 서버에 요청 */
    fun getMusicMetadataList(musicIDList:List<Int>, callbackOperation: (List<Music>?) -> Unit) {
        enqueueCall(api.getMetadataList(musicIDList), callbackOperation)
    }

    /** 호출 시 항목이름과 항목내용을 통해 메타데이터를 서버에 요청, 호출될 함수 operation의 인자가 리스트형태 */
    fun getMusicMetadataList(criterion:List<String>, keyword:String, callbackOperation:(List<Music>?)->Unit) {
        enqueueCall(api.getMetadataList(criterion, keyword), callbackOperation)
    }

    fun getMusicArtImg(musicID:Int, callbackOperation:(Array<Byte>?)->Unit) {
        enqueueCall(api.getArtImg(musicID), callbackOperation)
    }



    fun cumulateHistory(playInform:PlayInform, callbackOperation:(Boolean?)->Unit) {
        enqueueCall(api.cumulateHistory(playInform), callbackOperation)
    }



    fun saveUserPlaylist(playlist: Playlist, callbackOperation: (Boolean?) -> Unit) {
        enqueueCall(api.saveUserPlaylist(playlist), callbackOperation)
    }

    fun loadUserPlaylist(userID:String, callbackOperation: (List<Playlist>?) -> Unit) {
        enqueueCall(api.loadUserPlaylist(userID), callbackOperation)
    }

    fun deleteUserPlaylist(userID:String, name:String, callbackOperation: (Boolean?) -> Unit) {
        enqueueCall(api.deleteUserPlaylist(userID, name), callbackOperation)
    }

    fun uploadSharedList(playlist : PlaylistForShare, callbackOperation:(Boolean?)->Unit) {
        enqueueCall(api.uploadSharedList(playlist), callbackOperation)
    }

    fun getAllSharedPlaylist(listSize:Int, callbackOperation:(List<PlaylistForShare>?)->Unit) {
        enqueueCall(api.getAllSharedPlaylist(), callbackOperation)
    }



    fun getPersonalizedList(userID: String, surroundings: Surroundings, listSize: Int, callbackOperation:(Playlist?)->Unit) {
        enqueueCall(api.getPersonalizedList(userID, surroundings, listSize), callbackOperation)
    }

    fun getGeneralizedList(surroundings: Surroundings, listSize: Int, callbackOperation:(Playlist?)->Unit) {
        enqueueCall(api.getGeneralizedList(surroundings, listSize), callbackOperation)
    }



    fun getThemeList(surroundings: Surroundings, listSize: Int, callbackOperation:(List<Playlist>?)->Unit) {
        enqueueCall(api.getThemeList(surroundings, listSize), callbackOperation)
    }

    fun getTopRankList(listSize: Int, callbackOperation:(Playlist?)->Unit) {
        enqueueCall(api.getTopRankList(listSize), callbackOperation)
    }



    fun getPersonalMusicTagList(userID:String, musicIDList: List<Int>, callbackOperation:(List<MusicTag>?)->Unit) {
        enqueueCall(api.getPersonalMusicTagList(userID, musicIDList), callbackOperation)
    }
    fun getGeneralMusicTagList(musicIDList: List<Int>, callbackOperation:(List<MusicTag>?)->Unit) {
        enqueueCall(api.getGeneralMusicTagList(musicIDList), callbackOperation)
    }


    /*
    /** 호출 시 id를 통해 메타데이터를 서버에 요청, response가 오면 호출될 함수 operation을 인자로 넘겨주어야 함 */
    fun getMusicMetadata(id:Int, operation:(Music?)->Unit) {
        /** retrofit 객체 초기화 */
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        /** api를 통해 서버에 request를 보냄 */
        val callGetMetadata = api.getMetadata(id)
        callGetMetadata.enqueue(object: Callback<Music> {
            override fun onResponse(call: Call<Music>, response: Response<Music>) {
                /** response가 정상적으로 왔다면 operation 함수 호출 */
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<Music>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun getMusicMetadataList(ids:List<Int>, operation: (List<Music>?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getMetadataList(ids)
        callGetMetadata.enqueue(object: Callback<List<Music>> {
            override fun onResponse(call: Call<List<Music>>, response: Response<List<Music>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Music>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    /** 호출 시 항목이름과 항목내용을 통해 메타데이터를 서버에 요청, 호출될 함수 operation의 인자가 리스트형태 */
    fun getMusicMetadataList(items:List<String>, name:String, operation:(List<Music>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getMetadataList(items, name)
        callGetMetadata.enqueue(object: Callback<List<Music>> {
            override fun onResponse(call: Call<List<Music>>, response: Response<List<Music>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Music>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    /** 호출 시 항목이름과 항목내용을 통해 메타데이터를 서버에 요청, 호출될 함수 operation의 인자가 리스트형태 */
    fun getMusicArtImg(id:Int, operation:(Array<Byte>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getArtImg(id)
        callGetMetadata.enqueue(object: Callback<Array<Byte>> {
            override fun onResponse(call: Call<Array<Byte>>, response: Response<Array<Byte>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<Array<Byte>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun loadPlaytimeHistory(musicId: Int, operation:(PlaytimeHistoryDTO?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaytimeHistory(userID, musicId)
        callGetMetadata.enqueue(object: Callback<PlaytimeHistoryDTO> {
            override fun onResponse(call: Call<PlaytimeHistoryDTO>, response: Response<PlaytimeHistoryDTO>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<PlaytimeHistoryDTO>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun loadPlaytimeHistoryList(operation:(List<PlaytimeHistoryDTO>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaytimeHistoryList(userID)
        callGetMetadata.enqueue(object: Callback<List<PlaytimeHistoryDTO>> {
            override fun onResponse(call: Call<List<PlaytimeHistoryDTO>>, response: Response<List<PlaytimeHistoryDTO>>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<PlaytimeHistoryDTO>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun savePlaytimeHistory(dto:PlaytimeHistoryDTO, operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.savePlaytimeHistory(dto)
        callGetMetadata.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(true)
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }



    fun loadPlaylistManager(operation:(PlaylistManagerDTO?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.selectPlaylistManager(userID)
        callGetMetadata.enqueue(object: Callback<PlaylistManagerDTO> {
            override fun onResponse(call: Call<PlaylistManagerDTO>, response: Response<PlaylistManagerDTO>) {
                Log.d("myTag", "success : ${response.raw()}")
                Log.d("myTag", response.body().toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<PlaylistManagerDTO>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun savePlaylistManager(operation:(Boolean?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.savePlaylistManager(PlaylistManager.toDto(userID))
        callGetMetadata.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(true)
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }


    fun getPersonalizedList(surroundings: Surroundings, listSize: Int = 20, operation:(List<Int>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getPersonalizedList(userID, surroundings, listSize)
        callGetMetadata.enqueue(object: Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun getGeneralizedList(surroundings: Surroundings, listSize: Int = 20, operation:(List<Int>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getGeneralizedList(surroundings, listSize)
        callGetMetadata.enqueue(object: Callback<List<Int>> {
            override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }


    fun getThemeList(surroundings: Surroundings, listSize: Int = 20, operation:(List<Playlist>?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getThemeList(surroundings, listSize)
        callGetMetadata.enqueue(object: Callback<List<Playlist>> {
            override fun onResponse(call: Call<List<Playlist>>, response: Response<List<Playlist>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<List<Playlist>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun getTopRankList(listSize: Int = 20, operation:(Playlist?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)
        val callGetMetadata = api.getTopRankList(listSize)
        callGetMetadata.enqueue(object: Callback<Playlist> {
            override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(response.body())
            }
            override fun onFailure(call: Call<Playlist>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }



    fun uploadShareList(playlist : PlaylistForShare, operation:(Boolean)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.uploadShareList(PlaylistManager.createPlaylistForShareToDto(playlist))
        callGetMetadata.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(true)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("myTag", "failure : $t")
                operation(false)
            }
        })
    }

    fun downloadSharedLists(listSize:Int = 20, operation:(List<PlaylistForShare>)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.downloadSharedLists()
        callGetMetadata.enqueue(object: Callback<List<PlaylistForShareDTO>> {
            override fun onResponse(call: Call<List<PlaylistForShareDTO>>, response: Response<List<PlaylistForShareDTO>>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())

                val listOfPlaylistForShare = ArrayList<PlaylistForShare>()
                result?.forEach { dto ->
                    listOfPlaylistForShare.add(PlaylistManager.getPlaylistForShareFromDto(dto))
                }
                operation(listOfPlaylistForShare)
            }
            override fun onFailure(call: Call<List<PlaylistForShareDTO>>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }

    fun getMusicTag(musicID:Int, operation:(MusicTag?)->Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MusicServiceConnection.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(RetrofitAPI::class.java)

        val callGetMetadata = api.getMusicTag(musicID)
        callGetMetadata.enqueue(object : Callback<MusicTag> {
            override fun onResponse(call: Call<MusicTag>, response: Response<MusicTag>) {
                Log.d("myTag", "success : ${response.raw()}")
                val result = response.body()
                Log.d("myTag", result.toString())
                operation(result)
            }

            override fun onFailure(call: Call<MusicTag>, t: Throwable) {
                Log.d("myTag", "failure : $t")
            }
        })
    }
    */
}