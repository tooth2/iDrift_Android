package edu.cmu.idrift0602.ViewActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.Settings;

import edu.cmu.idrift0602.HomeActivity;
import edu.cmu.idrift0602.Model.EventDao;
import edu.cmu.idrift0602.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddEventActivity extends ActionBarActivity {
    private long rowID;

    private EditText nameEditText;
    private EditText dateTimEditText;
    private EditText locationEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

// get action bar
       // ActionBar actionBar = getActionBar();
        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);


        nameEditText = (EditText) findViewById(R.id.nameEditText);
        dateTimEditText = (EditText) findViewById(R.id.dateTimEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);



        Bundle extras = getIntent().getExtras();


        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            nameEditText.setText(extras.getString("name"));
            dateTimEditText.setText(extras.getString("dateTime"));
            locationEditText.setText(extras.getString("location"));

        }

        Button saveEventButton = (Button) findViewById(R.id.saveEventButton);
        saveEventButton.setOnClickListener(saveEventButtonClicked);
    }


    View.OnClickListener saveEventButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (nameEditText.getText().length() != 0)
            {
                if (dateTimEditText.getText().length()==0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    String currentDateTime = sdf.format(new Date());

                    dateTimEditText.setText(currentDateTime);


                }



                AsyncTask<Object, Object, Object> saveEventTask = new AsyncTask<Object, Object, Object>()
                {
                    @Override
                    protected Object doInBackground(Object... params)
                    {

                        saveEvent();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object result)
                    {
                        finish();
                    }
                };

                saveEventTask.execute((Object[]) null);
            }
            else
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show();
            }
        }
    };


    private void saveEvent()
    {

        EventDao dbhelper = new EventDao(this);
        if (getIntent().getExtras() == null)
        {

            dbhelper.insertEvent(
                    nameEditText.getText().toString(),
                    dateTimEditText.getText().toString(),
                    locationEditText.getText().toString());


        }
        else
        {
            dbhelper.updateEvent(rowID,
                    nameEditText.getText().toString(),
                    dateTimEditText.getText().toString(),
                    locationEditText.getText().toString());

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_event, menu);
        return true;
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        switch (id) {



            case R.id.home:

                Intent listEvent = new Intent(AddEventActivity.this, HomeActivity.class);
                startActivity(listEvent);

                return true;
            case R.id.listEventItem:

                Intent itemList = new Intent(AddEventActivity.this, EventListActivity.class);
                startActivity(itemList);

                return true;





            default:
                return super.onOptionsItemSelected(item);
        }
        // return super.onOptionsItemSelected(item);

    }

}

