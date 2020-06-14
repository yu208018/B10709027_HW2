package com.example.android.B10709027_HW2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.android.B10709027_HW2.data.TestUtil;
import com.example.android.B10709027_HW2.data.WaitlistContract;
import com.example.android.B10709027_HW2.data.WaitlistDbHelper;
import com.example.android.waitlist.R;


public class MainActivity extends AppCompatActivity{

    public static GuestListAdapter mAdapter;
    public SQLiteDatabase mDb;
    public Cursor mcursor;
    public TestUtil testUtil;
    RecyclerView waitlistRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waitlistRV = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        waitlistRV.setLayoutManager(new LinearLayoutManager(this));

        WaitlistDbHelper dbHelper=new WaitlistDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        testUtil.insertFakeData(mDb);
        mcursor=getAllGuests();
        mAdapter = new GuestListAdapter(this, mcursor);
        waitlistRV.setAdapter(mAdapter);

        Intent intent = getIntent();
        mAdapter.swapCursor(getAllGuests());


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Alert Window")
                        .setMessage("Are you sure to DELETE?")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.swapCursor(getAllGuests());
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long id = (long) viewHolder.itemView.getTag();
                                removeGuest(id);
                                mAdapter.swapCursor(getAllGuests());
                            }
                        }).show();
            }}).attachToRecyclerView(waitlistRV);

    }

    //Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //id of selected item
        int id=item.getItemId();
        if(id==R.id.add){
            Intent intent2=new Intent(this,AddActivity.class);
            startActivity(intent2);
            return true;
        }else if(id==R.id.settings){
            Intent intentS=new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intentS);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private boolean removeGuest(long id) {
        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }


}