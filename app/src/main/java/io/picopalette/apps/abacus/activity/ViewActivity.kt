package io.picopalette.apps.abacus.activity

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import io.picopalette.apps.abacus.R
import io.picopalette.apps.abacus.adapter.EntryListAdapter
import io.picopalette.apps.abacus.database.CheckInEntry
import io.picopalette.apps.abacus.database.EntryDatabase
import io.picopalette.apps.abacus.networking.VolleySingleton
import kotlinx.android.synthetic.main.activity_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject

class ViewActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private val domain = "https://dash.abacus.org.in"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        supportActionBar?.title = sharedPref.getString(getString(R.string.event_name), "Reinstall App")

        val entriesAdapter = EntryListAdapter(applicationContext, listOf())
        entriesRecyclerView.adapter = entriesAdapter
        entriesRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        doAsync {
            val entries: List<CheckInEntry>? = EntryDatabase.getInstance(applicationContext)?.checkInEntryDao()?.getAllEntries()
            if (entries != null) {
                entriesAdapter.entries = entries
                entriesAdapter.notifyDataSetChanged()
            }
        }

        pushButton.setOnClickListener {
            val passphrase = passphraseEditText.text
            if(passphrase.isNotBlank()) {
                val data = JSONObject()
                data.put("eventNo", sharedPref.getInt(getString(R.string.event_no), -1))
                data.put("passphrase", passphrase)
                doAsync {
                    val entries: List<String>? = EntryDatabase.getInstance(applicationContext)?.checkInEntryDao()?.getNewEntries()
                    if (entries != null && entries.isNotEmpty()) {
                        val aids = JSONArray(entries)
                        data.put("entries", aids)
                        Log.d("Push data", data.toString())
                        val request = JsonObjectRequest(Request.Method.POST, domain+"/checkin", data, { response: JSONObject? ->
                            val pushedIds = response?.getJSONArray("pushed")
                            if (pushedIds != null) {
                                (0..(pushedIds.length() - 1))
                                        .map { pushedIds[it] as String }
                                        .forEach {
                                            doAsync {
                                                EntryDatabase.getInstance(applicationContext)?.checkInEntryDao()?.markEntryPushed(it)
                                            }
                                        }
                                doAsync {
                                    val entries: List<CheckInEntry>? = EntryDatabase.getInstance(applicationContext)?.checkInEntryDao()?.getAllEntries()
                                    if (entries != null) {
                                        uiThread {
                                            entriesAdapter.entries = entries
                                            entriesAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }, { error: VolleyError? ->
                            VolleySingleton.handleNetworkError(applicationContext, error)
                        })
                        VolleySingleton.getInstance(applicationContext).requestQueue.add(request)

                    } else {
                        uiThread {
                            Toast.makeText(applicationContext, "No data to Push", Toast.LENGTH_SHORT).show()
                        }
                    }
                }


            } else {
                Toast.makeText(this, "PassPhrase needed", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
