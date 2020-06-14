package com.example.android.B10709027_HW2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.B10709027_HW2.data.WaitlistContract;
import com.example.android.B10709027_HW2.data.WaitlistDbHelper;
import com.example.android.waitlist.R;

public class AddActivity extends AppCompatActivity {

    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;
    private Button mButtonAdd;
    private Button mButtonCancel;
    private SQLiteDatabase mDb;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mNewGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = (EditText) this.findViewById(R.id.party_count_edit_text);
        mButtonAdd=(Button) this.findViewById(R.id.add_to_waitlist_button);
        mButtonCancel=(Button)this.findViewById(R.id.add_cancel_button);

        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb=dbHelper.getWritableDatabase();

    }

    public Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    public long addNewGuest(String _name, int pSize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, _name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, pSize);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }


    //add_to_waitlist_button
    public void addToWaitlist(View view) {
        Log.e("1", "addToWaitlist: ");
        if (mNewGuestNameEditText.getText().length() == 0 || mNewPartySizeEditText.getText().length() == 0) {
            return;
        }

        int size = 1;
        String name="";
        try {
            size = Integer.parseInt(mNewPartySizeEditText.getText().toString());
            name = mNewGuestNameEditText.getText().toString();
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();
        mNewPartySizeEditText.clearFocus();

        addNewGuest(name,size);
        //sync MainActivity recyclerView
        MainActivity.mAdapter.swapCursor(getAllGuests());
    }

    //add_cancel_button
    public void add_cancel(View view) {
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();
        mNewPartySizeEditText.clearFocus();
    }


}
