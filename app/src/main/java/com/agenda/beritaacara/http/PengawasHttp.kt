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

class PengawasHttp(private val context: Context) {
    data class PengawasJson(
        @SerializedName("id") val id : Int,
        @SerializedName("nama") val nama : String
    )

    interface PengawasApi{
        @Headers("X-Requested-With: XMLHttpRequest")
        @GET("list/pengawas")
        fun get() : Call<List<PengawasJson>>
    }

    fun send( success : (it : List<PengawasJson>) -> Unit){
        var url = context.getString(R.string.url_api)
        val api = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PengawasApi::class.java)

        api.get().enqueue(object : Callback<List<PengawasJson>> {
            override fun onResponse(
                call: Call<List<PengawasJson>>,
                response: Response<List<PengawasJson>>
            ) {
                if (response.isSuccessful){
                    response.body()?.let {
                        success(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<PengawasJson>>, t: Throwable) {
                Toast.makeText(context, "Galat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}