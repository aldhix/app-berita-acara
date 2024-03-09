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

class PesertAbsenHttp(private val context: Context) {
    data class PesertaAbsenJson(
        @SerializedName("id") val id : Int,
        @SerializedName("peserta_absen") val peserta : List<ListPesertaAbsenJson>
    ){
        data class ListPesertaAbsenJson(
            @SerializedName("id") val id : Int,
            @SerializedName("nama") val nama : String
        )
    }

    data class MessagePesertaAbsenJson(
        @SerializedName("message") val message : String
    )

    data class PesertaAbsenError(
        val message: String,
        val errors : ListPesertaAbsenError,
    ){
        data class ListPesertaAbsenError(
            val nama : List<String>
        )
    }

    interface PesertaAbsenApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("berita_acara/{berita_acara}/peserta_absen")
        fun get(
            @Path("berita_acara") idJadwal : Int?
        ) : Call<PesertaAbsenJson>
    }

    interface StorePesertaAbsenApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @POST("berita_acara/{berita_acara}/peserta_absen")
        fun post(
            @Path("berita_acara") idJadwal : Int?,
            @Body body: JsonObject
        ) : Call<MessagePesertaAbsenJson>
    }

    interface DestroyPesertaAbsenApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @DELETE("berita_acara/{berita_acara}/peserta_absen/{peserta_absen}")
        fun deleted(
            @Path("berita_acara") idJadwal : Int?,
            @Path("peserta_absen") idPeserta : Int?,
        ) : Call<MessagePesertaAbsenJson>
    }

    fun send(idBeritaAcara : Int?, success : (it : PesertaAbsenJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PesertaAbsenApi::class.java)

        api.get(idBeritaAcara).enqueue(object : Callback<PesertaAbsenJson> {
            override fun onResponse(
                call: Call<PesertaAbsenJson>,
                response: Response<PesertaAbsenJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<PesertaAbsenJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun store(idBeritaAcara: Int?, nama : String, success : (it : MessagePesertaAbsenJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StorePesertaAbsenApi::class.java)

        api.post(idBeritaAcara, JsonObject().apply {
            addProperty("nama", nama)
        }).enqueue(object : Callback<MessagePesertaAbsenJson> {
            override fun onResponse(
                call: Call<MessagePesertaAbsenJson>,
                response: Response<MessagePesertaAbsenJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else {
                    Toast.makeText(context, "Gagal disimpan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessagePesertaAbsenJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun deleted(idBeritaAcara: Int?, idPeserta: Int?, success : (it : MessagePesertaAbsenJson) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DestroyPesertaAbsenApi::class.java)

        api.deleted(idBeritaAcara, idPeserta).enqueue(object : Callback<MessagePesertaAbsenJson> {
            override fun onResponse(
                call: Call<MessagePesertaAbsenJson>,
                response: Response<MessagePesertaAbsenJson>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                } else {
                    Toast.makeText(context, "Gagal dihapus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessagePesertaAbsenJson>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }

        })
    }

}