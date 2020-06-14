package com.example.android.B10709027_HW2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.B10709027_HW2.data.WaitlistContract;
import com.example.android.waitlist.R;


public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.GuestViewHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Holds on to the cursor to display the waitlist
    private Cursor mCursor;
    private Context mContext;
    private GuestViewHolder mholder;
    private String s;
    Drawable mcolor;

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param cursor the db cursor with waitlist data to display
     */
    public GuestListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }


    class GuestViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView partySizeTextView;

        public GuestViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            partySizeTextView = (TextView) itemView.findViewById(R.id.party_size_text_view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {

        this.mholder=holder;

        if (!mCursor.moveToPosition(position))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME));
        int partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE));

        long id = mCursor.getLong(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry._ID));

        holder.nameTextView.setText(name);
        holder.partySizeTextView.setText(String.valueOf(partySize));
        holder.itemView.setTag(id);

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        resetColor(sharedPreferences.getString(mContext.getResources().getString(R.string.pref_color_key),mContext.getResources().getString(R.string.pref_color_value_blue)));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void resetColor(String s){
        if (s.equals(mContext.getString(R.string.pref_color_value_red))) {
            mcolor=ContextCompat.getDrawable(mContext,R.drawable.red);
        } else if (s.equals(mContext.getString(R.string.pref_color_value_blue))) {
            mcolor=ContextCompat.getDrawable(mContext,R.drawable.circle);
        } else if(s.equals(mContext.getString(R.string.pref_color_value_green))){
            mcolor=ContextCompat.getDrawable(mContext,R.drawable.green);
        }
        mholder.partySizeTextView.setBackground(mcolor);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.guest_list_item, parent, false);
        return new GuestViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(mContext.getResources().getString(R.string.pref_color_key))){
            s=sharedPreferences.getString(key,mContext.getResources().getString(R.string.pref_color_value_blue));
            resetColor(s);
        }
        this.notifyDataSetChanged();
    }

}