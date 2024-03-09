package com.agenda.beritaacara

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.agenda.beritaacara.data.ListJadwalData
import com.agenda.beritaacara.data.ListRuangData
import com.agenda.beritaacara.http.JadwalHttp
import com.agenda.beritaacara.http.RuangHttp
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var btnBeritaAcara : Button
    private lateinit var lyJadwal : TextInputLayout
    private lateinit var lyRuang : TextInputLayout
    private lateinit var acJadwal : AutoCompleteTextView
    private lateinit var acRuang : AutoCompleteTextView
    private lateinit var listJadwal : ArrayList<ListJadwalData>
    private lateinit var listRuang : ArrayList<ListRuangData>
    private lateinit var optJadwal : ArrayList<String>
    private lateinit var optRuang : ArrayList<String>
    private lateinit var adapterJadwal : ArrayAdapter<String>
    private lateinit var adapterRuang : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        lyJadwal = findViewById(R.id.layout_jadwal)
        lyRuang = findViewById(R.id.layout_ruang)
        acJadwal = findViewById(R.id.ac_jadwal)
        acRuang = findViewById(R.id.ac_ruang)
        btnBeritaAcara = findViewById(R.id.btn_berita_acara)
        listJadwal = arrayListOf<ListJadwalData>()
        listRuang = arrayListOf<ListRuangData>()
        optJadwal = arrayListOf<String>()
        optRuang = arrayListOf<String>()

        adapterJadwal = ArrayAdapter(this, R.layout.option_item, optJadwal)
        adapterRuang = ArrayAdapter(this, R.layout.option_item, optRuang)

        acJadwal.setAdapter(adapterJadwal)
        acRuang.setAdapter(adapterRuang)

        btnBeritaAcara.setOnClickListener {
            openBeritaAcara()
        }

        setList()
    }

    private fun setList() {
        listJadwal.clear()
        listRuang.clear()
        optJadwal.clear()
        optRuang.clear()
        adapterJadwal.notifyDataSetChanged()
        adapterRuang.notifyDataSetChanged()

        val jadwalHttp : JadwalHttp = JadwalHttp(this)
        val ruangHttp : RuangHttp = RuangHttp(this)

        jadwalHttp.send {
            it.forEach {row ->
                //listJadwal.add(ListJadwalData(11,"Senin - Bahasa Indonesia"))
                listJadwal.add(ListJadwalData(row.id,"${row.hari} - ${row.mapel}"))
            }
            listJadwal.forEach {
                optJadwal.add(it.jadwal)
            }
            adapterJadwal.notifyDataSetChanged()
        }

        ruangHttp.send {
            it.forEach { row ->
                //listRuang.add(ListRuangData(21,"01/ XII PPLG A"))
                listRuang.add(ListRuangData(row.id,"${row.ruang}/ ${row.kelas}"))
            }

            listRuang.forEach {
                optRuang.add(it.ruang)
            }

            adapterRuang.notifyDataSetChanged()
        }
    }

    private fun openBeritaAcara() {
        val jadwal : String = acJadwal.text.toString()
        val ruang : String = acRuang.text.toString()
        val j : Int = optJadwal.indexOf(jadwal)
        val r : Int = optRuang.indexOf(ruang)

        lyJadwal.error = null
        lyRuang.error = null

        if (j < 0) {
            lyJadwal.error = "Jadwal wajib diisi."
        }

        if (r < 0){
            lyRuang.error = "Ruang wajib diisi."
        }

        if (j >= 0 && r >= 0){
            val intent = Intent(this, BeritaAcaraActivity::class.java)
            intent.putExtra("ID_JADWAL",listJadwal[j].id.toString())
            intent.putExtra("ID_RUANG",listRuang[r].id.toString())
            startActivity(intent)
        }
    }

}