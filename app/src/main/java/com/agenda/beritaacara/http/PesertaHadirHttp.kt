package com.agenda.beritaacara.http

import android.content.Context
import android.widget.Toast
import com.agenda.beritaacara.R
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

class PesertaHadirHttp(private val context: Context) {
    data class PesertaHadirJson(
        @SerializedName("id") val id : Int,
        @SerializedName("peserta_hadir") val peserta : List<ListPesertaHadirJson>
    ){
        data class ListPesertaHadirJson(
            @SerializedName("id") val id : Int,
            @SerializedName("nama") val nama : String
        )
    }

    data class MessagePesertaHadirJson(
        @SerializedName("message") val message : String
    )

    data class PesertaHadirError(
        val message: String,
        val errors : ListPesertaHadirError,
    ){
        data class ListPesertaHadirError(
            val nama : List<String>
        )
    }

    interface PesertaHadirApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("berita_acara/{berita_acara}/peserta_hadir")
        fun get(
            @Path("berita_acara") idJadwal : Int?
        ) : Call<PesertaHadirJson>
    }

    interface StorePesertaHadirApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("berita_acara/{berita_acara}/peserta_hadir")
        fun post(
            @Path("berita_acara") idJadwal : Int?,
            @Body body: JsonObject
        ) : Call<MessagePesertaHadirJson>
    }

    interface DestroyPesertaHadirApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @DELETE("berita_acara/{berita_acara}/peserta_hadir/{peserta_hadir}")
        fun deleted(
            @Path("berita_acara") idJadwal : Int?,
            @Path("peserta_hadir") idPeserta : Int?,
        ) : Call<MessagePesertaHadirJson>
    }

    fun send(idBeritaAcara : Int?, success : (it : PesertaHadirJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PesertaHadirApi::class.java)

        api.get(idBeritaAcara).enqueue(object : Callback<PesertaHadirJson> {
            override fun onResponse(
                call: Call<PesertaHadirJson>,
                response: Response<PesertaHadirJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<PesertaHadirJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun store(idBeritaAcara: Int?, nama : String, success : (it : MessagePesertaHadirJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StorePesertaHadirApi::class.java)

        api.post(idBeritaAcara, JsonObject().apply {
            addProperty("nama", nama)
        }).enqueue(object : Callback<MessagePesertaHadirJson> {
            override fun onResponse(
                call: Call<MessagePesertaHadirJson>,
                response: Response<MessagePesertaHadirJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else {
                    Toast.makeText(context, "Gagal disimpan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessagePesertaHadirJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun deleted(idBeritaAcara: Int?, idPeserta: Int?, success : (it : MessagePesertaHadirJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DestroyPesertaHadirApi::class.java)

        api.deleted(idBeritaAcara, idPeserta).enqueue(object : Callback<MessagePesertaHadirJson> {
            override fun onResponse(
                call: Call<MessagePesertaHadirJson>,
                response: Response<MessagePesertaHadirJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else {
                    Toast.makeText(context, "Gagal dihapus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessagePesertaHadirJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }
}