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

class RuangHttp(private val context: Context) {
    data class RuangJson(
        @SerializedName("id") val id : Int,
        @SerializedName("kelas") val kelas : String,
        @SerializedName("nama_ruang") val ruang : String
    )

    interface RuangApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("list/ruang")
        fun get() : Call<List<RuangJson>>
    }

    fun send( success : (it : List<RuangJson>) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RuangApi::class.java)

        api.get().enqueue(object : Callback<List<RuangJson>> {
            override fun onResponse(
                call: Call<List<RuangJson>>,
                response: Response<List<RuangJson>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<RuangJson>>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}