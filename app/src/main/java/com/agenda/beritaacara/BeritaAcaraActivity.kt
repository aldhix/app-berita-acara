package com.agenda.beritaacara

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.agenda.beritaacara.data.ListPengawasData
import com.agenda.beritaacara.http.BeritaAcaraHttp
import com.agenda.beritaacara.http.PengawasHttp
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class BeritaAcaraActivity : AppCompatActivity() {
    private lateinit var tvTanggal : TextView
    private lateinit var tvWaktu : TextView
    private lateinit var tvMapel : TextView
    private lateinit var tvRuang : TextView
    private lateinit var tvJumHadir : TextView
    private lateinit var tvJumAbsen : TextView
    private lateinit var tvCatatan : TextView
    private lateinit var tvPengawas1 : TextView
    private lateinit var tvNip1 : TextView
    private lateinit var tvPengawas2 : TextView
    private lateinit var tvNip2 : TextView
    private lateinit var btnPesertaHadir : Button
    private lateinit var btnPesertaAbsen : Button
    private lateinit var btnSuntingCatatan : Button
    private lateinit var btnSuntingPengawas : Button
    private lateinit var listPengawas : ArrayList<ListPengawasData>
    private lateinit var optPengawas : ArrayList<String>
    private lateinit var adapterPengawas : ArrayAdapter<String>
    private var idJadwal : Int? = null
    private var idRuang : Int? = null
    private var idBeritaAcara : Int? = null

    var startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            reqBeritaAcara()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berita_acara)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        idJadwal = intent.getStringExtra("ID_JADWAL")?.toInt()
        idRuang = intent.getStringExtra("ID_RUANG")?.toInt()

        tvTanggal = findViewById(R.id.tv_tanggal)
        tvWaktu = findViewById(R.id.tv_waktu)
        tvMapel = findViewById(R.id.tv_mapel)
        tvRuang = findViewById(R.id.tv_ruang)
        tvJumHadir = findViewById(R.id.tv_jum_hadir)
        tvJumAbsen = findViewById(R.id.tv_jum_absen)
        tvCatatan = findViewById(R.id.tv_catatan)
        tvPengawas1 = findViewById(R.id.tv_pengawas1)
        tvNip1 = findViewById(R.id.tv_nip1)
        tvPengawas2 = findViewById(R.id.tv_pengawas2)
        tvNip2 = findViewById(R.id.tv_nip2)
        btnPesertaHadir = findViewById(R.id.btn_peserta_hadir)
        btnPesertaAbsen = findViewById(R.id.btn_peserta_absen)
        btnSuntingCatatan = findViewById(R.id.btn_sunting_catatan)
        btnSuntingPengawas = findViewById(R.id.btn_sunting_pengawas)

        listPengawas = arrayListOf<ListPengawasData>()
        optPengawas = arrayListOf<String>()
        adapterPengawas = ArrayAdapter(this, R.layout.option_item, optPengawas)

        btnPesertaHadir.visibility = View.GONE
        btnSuntingPengawas.visibility = View.GONE
        btnSuntingCatatan.visibility = View.GONE
        btnPesertaAbsen.visibility = View.GONE

        btnPesertaHadir.setOnClickListener {
            openPesertaHadir()
        }

        btnPesertaAbsen.setOnClickListener {
            openPesertaTidakHadir()
        }

        btnSuntingCatatan.setOnClickListener {
            openSuntingCatatan()
        }

        btnSuntingPengawas.setOnClickListener {
            openSuntingPengawas()
        }

        reqBeritaAcara()
        setList()

    }

    private fun setList() {
        listPengawas.clear()
        optPengawas.clear()
        adapterPengawas.notifyDataSetChanged()

        val pengawasHttp : PengawasHttp = PengawasHttp(this)
        pengawasHttp.send {
            it.forEach { row ->
                //listPengawas.add(ListPengawasData(11, "Dodo Sidodo, S.Pd"))
                listPengawas.add(ListPengawasData(row.id, row.nama))
            }

            listPengawas.forEach {
                optPengawas.add(it.nama)
            }

            adapterPengawas.notifyDataSetChanged()
        }
    }

    private fun openSuntingPengawas() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_sunting_pengawas)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lyPengawas1 : TextInputLayout = dialog.findViewById(R.id.layout_pengawas1)
        val lyPengawas2 : TextInputLayout = dialog.findViewById(R.id.layout_pengawas2)
        val acPengawas1 : AutoCompleteTextView = dialog.findViewById(R.id.ac_pengawas1)
        val acPengawas2 : AutoCompleteTextView = dialog.findViewById(R.id.ac_pengawas2)
        val btnSimpanPengawas : Button = dialog.findViewById(R.id.btn_simpan_pengawas)
        val btnBatalPengawas : Button = dialog.findViewById(R.id.btn_batal_pengawas)

        acPengawas1.setAdapter(adapterPengawas)
        acPengawas2.setAdapter(adapterPengawas)

        btnBatalPengawas.setOnClickListener {
            dialog.dismiss()
        }

        btnSimpanPengawas.setOnClickListener {
            val context : Context = this
            val pengawas1 : String = acPengawas1.text.toString()
            val pengawas2 : String = acPengawas2.text.toString()
            val p1 : Int = optPengawas.indexOf(pengawas1)
            val p2 : Int = optPengawas.indexOf(pengawas2)
            var idPengawas1 : Int? = null
            var idPengawas2 : Int? = null

            lyPengawas1.error = null
            lyPengawas2.error = null

            if ( p1 < 0 ){
                lyPengawas1.error = "Pengawasi wajib diisi."
            } else {
                idPengawas1 = listPengawas[p1].id
            }

            if (p2 >= 0 ) {
                idPengawas2 = listPengawas[p2].id
            }

            if ( idPengawas1 != null ) {
                val beritaAcaraHttp : BeritaAcaraHttp = BeritaAcaraHttp(context)
                beritaAcaraHttp.pengawas(idBeritaAcara, idPengawas1, idPengawas2, {
                    reqBeritaAcara()
                    dialog.dismiss()
                    Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT ).show()
                },{
                    Toast.makeText(context,"Gagal disimpan",Toast.LENGTH_SHORT).show()
                })
            }

        }

        dialog.show()
    }

    private fun openSuntingCatatan() {
        val context : Context = this
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_sunting_catatan)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lyCatatan : TextInputLayout = dialog.findViewById(R.id.layout_catatan)
        val etCatatan : TextInputEditText = dialog.findViewById(R.id.et_catatan)
        val btnSimpanCatatan : Button = dialog.findViewById(R.id.btn_simpan_catatan)
        val btnBatalCatatan : Button = dialog.findViewById(R.id.btn_batal_catatan)

        btnBatalCatatan.setOnClickListener {
            dialog.dismiss()
        }

        btnSimpanCatatan.setOnClickListener {
            val catatan : String = etCatatan.text.toString()

            lyCatatan.error = null

            if (catatan == null){
                lyCatatan.error = "Catatan wajib diisi."
            }

            if (catatan != null){
                val beritaAcaraHttp : BeritaAcaraHttp = BeritaAcaraHttp(context)
                beritaAcaraHttp.catatan(idBeritaAcara, catatan,{
                    reqBeritaAcara()
                    dialog.dismiss()
                    Toast.makeText(context,"Berhasil",Toast.LENGTH_SHORT).show()
                },{
                    lyCatatan.error = it.errors.catatan[0].toString()
                })
            }
        }

        dialog.show()
    }

    private fun reqBeritaAcara() {
        val beritaAcaraHttp : BeritaAcaraHttp = BeritaAcaraHttp(this)
        beritaAcaraHttp.send(idJadwal, idRuang){

            idBeritaAcara = it.id

            val pengawas1 = if (it.pengawas1 != null) it.pengawas1.nama else "-"
            val nip1  = if (it.pengawas1 != null) it.pengawas1.nip else "-"
            val pengawas2 = if (it.pengawas2 != null) it.pengawas2.nama else "-"
            val nip2  = if (it.pengawas2 != null) it.pengawas2.nip else "-"

            setValueTexview(
                it.jadwal.hari,
                it.jadwal.tanggal,
                "${it.jadwal.mulai} s.d ${it.jadwal.selesai}",
                it.jadwal.mapel,
                "${it.ruang.namaRuang}/ ${it.ruang.kelas}",
                it.jumHadir,
                it.jumAbsen,
                it.catatan,
                pengawas1,
                nip1,
                pengawas2,
                nip2
            )

            btnPesertaHadir.visibility = View.VISIBLE
            btnSuntingPengawas.visibility = View.VISIBLE
            btnSuntingCatatan.visibility = View.VISIBLE
            btnPesertaAbsen.visibility = View.VISIBLE
        }
    }

    private fun setValueTexview(
        hari : String,
        tanggal : String,
        waktu : String,
        mapel : String,
        ruang : String,
        jumHadir : Int,
        jumAbsen : Int,
        catatan: String,
        pengawas1 : String?,
        nip1 : String?,
        pengawas2 : String?,
        nip2 : String?
    ) {
        tvTanggal.text = "$hari, $tanggal"
        tvWaktu.text = waktu
        tvMapel.text = mapel
        tvRuang.text = ruang
        tvJumHadir.text = jumHadir.toString()
        tvJumAbsen.text = jumAbsen.toString()
        tvCatatan.text = catatan
        tvPengawas1.text = pengawas1
        tvNip1.text = nip1
        tvPengawas2.text = pengawas2
        tvNip2.text = nip2
    }


    private fun openPesertaTidakHadir() {
        val intent = Intent(this, PesertaTidakHadirActivity::class.java)
        intent.putExtra("ID_BERITA_ACARA",idBeritaAcara.toString())
        startActivityForResult.launch(intent)
    }

    private fun openPesertaHadir() {
        val intent = Intent(this, PesertaHadirActivity::class.java)
        intent.putExtra("ID_BERITA_ACARA",idBeritaAcara.toString())
        startActivityForResult.launch(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}