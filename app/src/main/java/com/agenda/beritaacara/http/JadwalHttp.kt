package com.agenda.beritaacara.http

import android.content.Context
import android.widget.Toast
import com.agenda.beritaacara.R
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

class JadwalHttp(private val context: Context) {

    data class JadwalJson(
        @SerializedName("id") val id : Int,
        @SerializedName("hari") val hari : String,
        @SerializedName("nama_mapel") val mapel : String
    )

    interface JadwalApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("list/jadwal")
        fun get() : Call<List<JadwalJson>>
    }

    fun send( success : (it : List<JadwalJson>) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JadwalApi::class.java)

        api.get().enqueue(object : Callback<List<JadwalJson>>{
            override fun onResponse(
                call: Call<List<JadwalJson>>,
                response: Response<List<JadwalJson>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<JadwalJson>>, t: Throwable) {
                Toast.makeText(context, "Galat",Toast.LENGTH_SHORT).show()
            }
        })
    }
}