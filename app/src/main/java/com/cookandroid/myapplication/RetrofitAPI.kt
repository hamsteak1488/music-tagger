package com.cookandroid.myapplication

import com.tftf.util.Music
import com.tftf.util.MusicTag
import com.tftf.util.PlayInform
import com.tftf.util.Playlist
import com.tftf.util.PlaylistForShare
import com.tftf.util.Surroundings
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** http 통신을 위한 retrofit의 API 인터페이스 */
interface RetrofitAPI {
    /** id를 통해 메타데이터에 접근, id는 유일성을 가지므로 반환형은 Music */
    @GET("/metadata")
    fun getMetadata(@Query("musicID") id:Int) : Call<Music>

    @GET("/metadatalist")
    fun getMetadataList(@Query("musicIDList") musicIDList:List<Int>) : Call<List<Music>>

    /** 메타데이터 항목의 내용으로 접근, 여러 결과가 나올 수 있으므로 반환형은 List<Music> */
    @GET("/metadatalist")
    fun getMetadataList(@Query("itemList") item:List<String>,
                        @Query("name") name:String) : Call<List<Music>>



    /** id를 통해 ArtImage 요청 */
    @GET("/img")
    fun getArtImg(@Query("id") id:Int) : Call<Array<Byte>>



    /** 재생기록 저장 */
    @POST("/playtimehistory/cumulate")
    fun cumulateHistory(@Body playInform: PlayInform) : Call<Boolean>


    /*
    /** 재생기록  불러오기 */
    @POST("/playtimehistory/select")
    fun selectPlaytimeHistory(@Query("email") email:String,
                              @Query("musicId") musicId:Int) : Call<PlaytimeHistoryDTO>

    @POST("/playtimehistory/select")
    fun selectPlaytimeHistoryList(@Query("email") email: String) : Call<List<PlaytimeHistoryDTO>>
    */


    @POST("/playlist/save")
    fun saveUserPlaylist(@Body playlist: Playlist) : Call<Boolean>

    @POST("/playlist/select_by_userid")
    fun loadUserPlaylist(@Query("userID") userID: String) : Call<List<Playlist>>

    @POST("/playlist/delete")
    fun deleteUserPlaylist(@Query("userID") userID: String, @Query("name") name:String) : Call<Boolean>



    @POST("/share/upload")
    fun uploadSharedList(@Body playlist:PlaylistForShare) : Call<Boolean>

    @POST("/share/selectAll")
    fun getAllSharedPlaylist() : Call<List<PlaylistForShare>>



    @POST("/recommend/personalized")
    fun getPersonalizedList(@Query("userID") userID: String,
                            @Body surroundings: Surroundings,
                            @Query("listSize") listSize: Int) : Call<Playlist>

    @POST("/recommend/generalized")
    fun getGeneralizedList(@Body surroundings: Surroundings,
                           @Query("listSize") listSize: Int) : Call<Playlist>

    @POST("/recommend/theme")
    fun getThemeList(@Body surroundings: Surroundings,
                     @Query("listSize") listSize:Int) : Call<List<Playlist>>

    @POST("/recommend/toprank")
    fun getTopRankList(@Query("listSize") listSize:Int) : Call<Playlist>



    @POST("/tag/personal")
    fun getPersonalMusicTagList(@Query("userID") userID:String, @Query("musicIDList") musicIDList:List<Int>) : Call<List<MusicTag>>

    @POST("/tag/general")
    fun getGeneralMusicTagList(@Query("musicIDList") musicIDList:List<Int>) : Call<List<MusicTag>>
}