package com.agenda.beritaacara

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agenda.beritaacara.adapter.PesertaAdapter
import com.agenda.beritaacara.data.ListPesertaData
import com.agenda.beritaacara.http.PesertAbsenHttp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class PesertaTidakHadirActivity : AppCompatActivity(), PesertaAdapter.ListnerePesertaAdapter {
    private lateinit var listPeserta : ArrayList<ListPesertaData>
    private lateinit var adapterPeserta : PesertaAdapter
    private lateinit var rvPeserta : RecyclerView
    private lateinit var btnScan : Button
    private var idBeritaAcara : Int? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
        if (isGranted){
            showCamera()
        } else {
            Toast.makeText(this,"Membutuhkan izin kamera untuk menscan QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    private val scanLauncer = registerForActivityResult(ScanContract()){
        if (it.contents != null){
            sendData(it.contents)
        } else {
            setList()
        }
    }

    private fun sendData(nama : String){
        val context : Context = this
        val pesertaAbsenHttp : PesertAbsenHttp = PesertAbsenHttp(this)
        pesertaAbsenHttp.store(idBeritaAcara, nama){
            Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show()
            showCamera()
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QRCode Peserta Tidak Hadir")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

        scanLauncer.launch(options)
    }

    private fun checkPermissionCamera(context : Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
            Toast.makeText(context, "Wajib menggunakan kamera", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            onBackResult()
        }
    }

    private fun onBackResult() {
        setResult(RESULT_OK)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peserta_tidak_hadir)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Daftar Peserta Tidak Hadir"

        idBeritaAcara = intent.getStringExtra("ID_BERITA_ACARA")?.toInt()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback )

        rvPeserta = findViewById(R.id.rv_peserta_absen)
        btnScan = findViewById(R.id.btn_scan_absen)

        rvPeserta.layoutManager = LinearLayoutManager(this)
        rvPeserta.setHasFixedSize(true)
        listPeserta = arrayListOf<ListPesertaData>()
        adapterPeserta = PesertaAdapter(this, listPeserta)
        rvPeserta.adapter = adapterPeserta

        btnScan.setOnClickListener {
            checkPermissionCamera(this)
        }

        setList()

    }

    private fun setList() {
        listPeserta.clear()
        adapterPeserta.notifyDataSetChanged()

        val pesertaAbsenHttp : PesertAbsenHttp = PesertAbsenHttp(this)
        pesertaAbsenHttp.send(idBeritaAcara){
            it.peserta.forEach { row ->
                listPeserta.add(ListPesertaData(row.id,row.nama))
            }
            adapterPeserta.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackResult()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDelete(id: Int) {
        val pesertaAbsenHttp : PesertAbsenHttp = PesertAbsenHttp(this)
        pesertaAbsenHttp.deleted(idBeritaAcara, id){ result ->
            val result : List<ListPesertaData> = listPeserta.filter { it.id != id }
            listPeserta.clear()

            result.forEach { row->
                listPeserta.add(ListPesertaData(row.id,row.nama))
            }

            adapterPeserta.notifyDataSetChanged()
        }
    }
}