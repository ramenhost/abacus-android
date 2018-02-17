package io.picopalette.apps.abacus.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.picopalette.apps.abacus.R
import io.picopalette.apps.abacus.database.CheckInEntry

/**
 * Created by ramkumar on 16/02/18.
 */
class EntryListAdapter(private var context: Context, var entries: List<CheckInEntry>): RecyclerView.Adapter<EntryListAdapter.EntryCardViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EntryCardViewHolder {
        var view: View = LayoutInflater.from(parent?.context).inflate(R.layout.entry_card, parent, false)
        return EntryCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: EntryCardViewHolder?, position: Int) {
        val checkInEntry = entries[position]
        holder?.aidTextView?.text = checkInEntry.aId
        if (checkInEntry.pushed) {
            holder?.statusView?.setImageDrawable(context.getDrawable(R.drawable.ic_cloud_done_black_24dp))
        }
    }

    class EntryCardViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var aidTextView: TextView = view.findViewById(R.id.rcAidTextView)
        var statusView: ImageView = view.findViewById(R.id.rcStatusView)

    }
}