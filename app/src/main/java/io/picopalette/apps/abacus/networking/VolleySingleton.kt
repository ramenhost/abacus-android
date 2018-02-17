package io.picopalette.apps.abacus.networking


import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NoConnectionError
import com.android.volley.VolleyError
import io.picopalette.apps.abacus.R


class VolleySingleton private constructor(context: Context) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)

    companion object {
        private var mInstance: VolleySingleton? = null

        fun getInstance(context: Context): VolleySingleton {
            if (mInstance == null) {
                mInstance = VolleySingleton(context)
            }
            return mInstance as VolleySingleton
        }

        fun handleNetworkError(context: Context, error: VolleyError?): Boolean {
            Log.e("Error", error?.toString())
            if (error is NoConnectionError || error?.networkResponse == null) {
                Toast.makeText(context.applicationContext, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            } else if(error is AuthFailureError){
                Toast.makeText(context.applicationContext, context.getString(R.string.passphrase_support), Toast.LENGTH_SHORT).show()
            }
            return false
        }
    }

}