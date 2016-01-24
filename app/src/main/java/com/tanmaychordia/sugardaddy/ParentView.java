package com.tanmaychordia.sugardaddy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Timer;
import java.util.TimerTask;

public class ParentView extends AppCompatActivity {

    TextView t1;
    TextView t2;
    TextView t3;

    int red = 0xFFD9534F;
    int green = 0xFF5CB85C;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view);

        t1 = (TextView) findViewById(R.id.stripsInd);
        t2 = (TextView) findViewById(R.id.foodInd);
        t3 = (TextView) findViewById(R.id.measInd);

        t1.setBackgroundColor(red);
        t2.setBackgroundColor(red);
        t3.setBackgroundColor(red);

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
                            if ( (Integer)gameScore.get("didEat") > 0) {

                                t2.setBackgroundColor(green);
                            }
                            if ( (Integer)gameScore.get("didMeasure") > 0) {

                                t3.setBackgroundColor(green);
                            }
                            if ( (Integer)gameScore.get("didBringStrips") > 0) {

                                t1.setBackgroundColor(green);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
