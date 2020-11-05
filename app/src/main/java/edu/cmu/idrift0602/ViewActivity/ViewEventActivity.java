package edu.cmu.idrift0602.ViewActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import edu.cmu.idrift0602.Model.EventDao;
import edu.cmu.idrift0602.R;



public class ViewEventActivity extends FragmentActivity

        {

    private long rowID;
    private static final String TAG = "ViewEventList";
    private TextView nameTextView;
    //private Button checkInButton;

    private TextView dateTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // get action bar
        ActionBar actionBar = getActionBar();

        // Enabling Up / Back navigation
       // actionBar.setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong(EventListActivity.ROW_ID);

        nameTextView = (TextView) findViewById(R.id.nameTextView);

        dateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);

    }







    @Override
    protected void onResume() {
        super.onResume();
        new LoadEventTask().execute(rowID);

    }



    @Override
    public void onPause() {
        super.onPause();

    }

    private class LoadEventTask extends AsyncTask<Long, Object, Cursor>
    {

        EventDao dbConnector= new EventDao(ViewEventActivity.this);
        @Override
        protected Cursor doInBackground(Long... params)
        {
            dbConnector.open();
            return dbConnector.getOneEvent(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);
            result.moveToFirst();

            int nameIndex = result.getColumnIndex("name");

            int dateTimeIndex = result.getColumnIndex("dateTime");

            nameTextView.setText(result.getString(nameIndex));

            dateTimeTextView.setText(result.getString(dateTimeIndex));
              result.close();
            dbConnector.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.view_event, menu);
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.view_event, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        return super.onOptionsItemSelected(item);
*/

        switch (item.getItemId())
        {
            case R.id.home :
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, EventListActivity.class));
                return true;

            case R.id.editItem:

                Intent addEvent = new Intent(this, AddEventActivity.class);

                addEvent.putExtra(EventListActivity.ROW_ID, rowID);
                addEvent.putExtra("name", nameTextView.getText());

                addEvent.putExtra("dateTime", dateTimeTextView.getText());

                startActivity(addEvent);

                return true;

            case R.id.deleteItem:
                deleteEvent();
                return true;


            /*case R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, EventListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;*/


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteEvent()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventActivity.this);

        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);

        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final EventDao dbConnector =  new EventDao(ViewEventActivity.this);

                        AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>()
                        {
                            @Override
                            protected Object doInBackground(Long... params)
                            {
                                dbConnector.deleteEvent(params[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result)
                            {
                                finish();
                            }
                        };


                        deleteTask.execute(new Long[] { rowID });
                    }
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }
}
