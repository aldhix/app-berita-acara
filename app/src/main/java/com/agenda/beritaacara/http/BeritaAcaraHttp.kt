package com.agenda.beritaacara.http

import android.content.Context
import android.widget.Toast
import com.agenda.beritaacara.R
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

class BeritaAcaraHttp(private val context: Context) {
    data class BeritaAcaraJson(
        @SerializedName("id") val id : Int,
        @SerializedName("catatan") val catatan : String,
        @SerializedName("jum_peserta_hadir") val jumHadir : Int,
        @SerializedName("jum_peserta_absen") val jumAbsen : Int,
        @SerializedName("jadwal") val jadwal : JadwalBeritaAcaraJson,
        @SerializedName("ruang") val ruang : RuangBeritaAcaraJson,
        @SerializedName("pengawas1") val pengawas1 : Pengawas1BeritaAcaraJson?,
        @SerializedName("pengawas2") val pengawas2 : Pengawas2BeritaAcaraJson?
    ){
        data class JadwalBeritaAcaraJson(
            @SerializedName("id") val id : Int,
            @SerializedName("hari") val hari : String,
            @SerializedName("tanggal") val tanggal : String,
            @SerializedName("waktu_mulai") val mulai : String,
            @SerializedName("waktu_selesai") val selesai : String,
            @SerializedName("nama_mapel") val mapel : String,
        )

        data class RuangBeritaAcaraJson(
            @SerializedName("id") val id : Int,
            @SerializedName("nama_ruang") val namaRuang : String,
            @SerializedName("kelas") val kelas : String,
        )

        data class Pengawas1BeritaAcaraJson(
            @SerializedName("id") val id : Int,
            @SerializedName("nama") val nama : String,
            @SerializedName("nip") val nip : String?
        )

        data class Pengawas2BeritaAcaraJson(
            @SerializedName("id") val id : Int,
            @SerializedName("nama") val nama : String,
            @SerializedName("nip") val nip : String?
        )
    }

    data class MessageBeritaAcaraJson(
        @SerializedName("message") val message : String
    )

    data class CatatanError(
        val message: String,
        val errors : ListCatatanError,
    ){
        data class ListCatatanError(
            val catatan : List<String>
        )
    }

    data class PengawasError(
        val message: String,
        val errors : ListPengawasError,
    ){
        data class ListPengawasError(
            val pengawas1 : List<String>?,
            val pengawas2: List<String>?
        )
    }

    interface BeritaAcaraApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("berita_acara/jadwal/{jadwal}/ruang/{ruang}")
        fun get(
            @Path("jadwal") idJadwal : Int?,
            @Path("ruang") idRuang : Int?
        ) : Call<BeritaAcaraJson>
    }

    interface CatatanApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("berita_acara/{berita_acara}/catatan")
        fun post(
            @Path("berita_acara") idBeritaAcara : Int?,
            @Body body : JsonObject
        ) : Call<MessageBeritaAcaraJson>
    }

    interface PengawasUjianApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("berita_acara/{berita_acara}/pengawas")
        fun post(
            @Path("berita_acara") idBeritaAcara : Int?,
            @Body body : JsonObject
        ) : Call<MessageBeritaAcaraJson>
    }

    fun send( idJadwal : Int?, idRuang : Int?,  success : (it : BeritaAcaraJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BeritaAcaraApi::class.java)

        api.get(idJadwal, idRuang).enqueue(object : Callback<BeritaAcaraJson> {
            override fun onResponse(
                call: Call<BeritaAcaraJson>,
                response: Response<BeritaAcaraJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<BeritaAcaraJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun catatan(
        idBeritaAcara: Int?,
        catatan : String?,
        success : (it : MessageBeritaAcaraJson)-> Unit,
        failed : (it : CatatanError) -> Unit
    ){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatatanApi::class.java)

        api.post(idBeritaAcara, JsonObject().apply {
            addProperty("catatan", catatan)
        }).enqueue(object : Callback<MessageBeritaAcaraJson>{
            override fun onResponse(
                call: Call<MessageBeritaAcaraJson>,
                response: Response<MessageBeritaAcaraJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else if (response.code() == 422){
                    val data = Gson().fromJson( response.errorBody()?.string(), CatatanError::class.java )
                    failed(data)
                }
            }

            override fun onFailure(call: Call<MessageBeritaAcaraJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun pengawas(
        idBeritaAcara: Int?,
        pengawas1 : Int?,
        pengawas2 : Int?,
        success : (it : MessageBeritaAcaraJson)-> Unit,
        failed : (it : PengawasError) -> Unit
    ){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PengawasUjianApi::class.java)

        api.post(idBeritaAcara, JsonObject().apply {
            addProperty("pengawas1", pengawas1)
            addProperty("pengawas2",pengawas2)
        }).enqueue(object : Callback<MessageBeritaAcaraJson>{
            override fun onResponse(
                call: Call<MessageBeritaAcaraJson>,
                response: Response<MessageBeritaAcaraJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else if (response.code() == 422){
                    val data = Gson().fromJson( response.errorBody()?.string(), PengawasError::class.java )
                    failed(data)
                }
            }

            override fun onFailure(call: Call<MessageBeritaAcaraJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

}