package io.picopalette.apps.abacus.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import io.picopalette.apps.abacus.R
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_init.*


class InitActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val isInit = sharedPref.getInt(getString(R.string.event_no), -1)
        if(isInit != -1) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        enterButton.setOnClickListener { _ ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        enterButton.visibility = View.GONE

        val adapter = ArrayAdapter.createFromResource(this, R.array.events_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        Log.d("event_no", pos.toString())
        if(pos == 0) {
            enterButton.visibility = View.GONE
            return
        }
        with(sharedPref.edit()){
            putString(getString(R.string.event_name), parent.getItemAtPosition(pos) as String?)
            putInt(getString(R.string.event_no), pos)
            commit()
        }
        enterButton.visibility = View.VISIBLE
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        enterButton.visibility = View.GONE
    }

}
