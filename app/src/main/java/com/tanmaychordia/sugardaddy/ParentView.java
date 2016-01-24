package com.tanmaychordia.sugardaddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

public class ParentView extends AppCompatActivity {

    CheckBox t1;
    CheckBox t2;
    CheckBox t3;

    int red = 0xFFD9534F;
    int green = 0xFF5CB85C;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);

        t1 = (CheckBox) findViewById(R.id.stripsInd);
        t2 = (CheckBox) findViewById(R.id.foodInd);
        t3 = (CheckBox) findViewById(R.id.measInd);

//        t1.setBackgroundColor(red);
//        t2.setBackgroundColor(red);
//        t3.setBackgroundColor(red);

        TimerTask t = new TimerTask()
        {
            @Override
            public void run(){
                ParseQuery<ParseObject> query = ParseQuery.getQuery("checkListMaster");


                query.getInBackground("pEXG4z9D9u", new GetCallback<ParseObject>() {
                    public void done(ParseObject gameScore, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data. In this case, only cheatMode and score
                            // will get sent to the Parse Cloud. playerName hasn't changed.

                            System.out.println(20);
                            if ( (Integer)gameScore.get("didEat") > 0) {

                                t2.setChecked(true);
                            }
                            if ( (Integer)gameScore.get("didMeasure") > 0) {

                                t3.setChecked(true);
                            }
                            if ( (Integer)gameScore.get("didBringStrips") > 0) {

                                t1.setChecked(true);
                            }
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(t, 0, 1000);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout2) {
            ParseUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
