package io.picopalette.apps.abacus.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.graphics.PointF
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import io.picopalette.apps.abacus.R
import io.picopalette.apps.abacus.database.CheckInEntry
import io.picopalette.apps.abacus.database.EntryDatabase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.qrdecoder.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), QRCodeReaderView.OnQRCodeReadListener, View.OnClickListener {

    private lateinit var aidRegex: Regex
    private val MY_PERMISSIONS_REQUEST_CAMERA: Int = 5
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        addButton.setOnClickListener(this)
        eventNameTextView.text = sharedPref.getString(getString(R.string.event_name), "Reinstall App")

        aidRegex = Regex(getString(R.string.aid_pattern))

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA)
        } else {
            layoutInflater.inflate(R.layout.qrdecoder, qrdecoderFrame, true)
            initializeQRReader()
        }

        fab.setOnClickListener { _ ->
            val intent = Intent(this, ViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        if(text == null) {
            return
        }
        if(text.matches(aidRegex)) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE)
            if(vibrator is Vibrator) {
                vibrator.vibrate(200)
            }
            aidEditText.setText(text.substring(2))
        } else {
            Toast.makeText(applicationContext, getString(R.string.on_invalid_qr), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeQRReader() {
        qrdecoderView?.setOnQRCodeReadListener(this)
        qrdecoderView?.setQRDecodingEnabled(true)
        qrdecoderView?.setAutofocusInterval(2000L)
        qrdecoderView?.setBackCamera()
    }

    override fun onPause() {
        super.onPause()
        qrdecoderView?.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        qrdecoderView?.surfaceCreated(qrdecoderView.holder)
        qrdecoderView?.startCamera()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.addButton -> {
                if(aidEditText.text != null) {
                    addButton.visibility = View.GONE
                    val checkInEntry = CheckInEntry(null, "ab"+aidEditText.text, false)
                    doAsync {
                        try {
                            EntryDatabase.getInstance(applicationContext)?.checkInEntryDao()?.add(checkInEntry)
                            uiThread {
                                Toast.makeText(applicationContext, "Added: ab"+aidEditText.text, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: SQLiteConstraintException) {
                            uiThread {
                                Toast.makeText(applicationContext, "Already Checked in", Toast.LENGTH_LONG).show()
                            }
                        } finally {
                            uiThread {
                                addButton.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    layoutInflater.inflate(R.layout.qrdecoder, qrdecoderFrame, true)
                    initializeQRReader()
                }
                return
            }
        }
    }
}
