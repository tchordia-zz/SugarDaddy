package com.tanmaychordia.sugardaddy;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CircEase;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser==null) {
            navigateToLogin();
        }

//        ParseUser user = new ParseUser();
//        user.setUsername("SugarDaddy");
//        user.setPassword("sugar");
//        user.setEmail("email@example.com");
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                } else {
//                    // Sign up didn't succeed. Look at the ParseException
//                    // to figure out what went wrong
//                    System.err.println(e);
//                }
//            }
//        });
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

        LineChartView lView= (LineChartView) findViewById(R.id.linechart);
        LineSet dataSet = new LineSet(new String[]{"1", "2", "3", "4", "5", "6"}, new float[]{3f,7f,1f, 2.4f, 18f, 3f});
//
        dataSet.setDotsRadius(15);
        dataSet.setDotsColor(0xFFFFFF);
        dataSet.setColor(0xBCCACF);

        //dataSet.setGradientFill(new int[]{0xFFFFFF, 0x20CE99}, new float[]{0, 1});
        lView.setBackgroundColor(0x20CE99);
//        dataSet.setFill(0x20CE99);
        dataSet.setDotsStrokeColor(0xBCCACF);
        dataSet.setDotsStrokeThickness(10);


        Animation anim = new Animation(2000);
        anim.setEasing(new CircEase());
        lView.setYLabels(AxisController.LabelPosition.NONE);
//        lView.setYAxis(false);

        Paint p = new Paint();

        p.setColor(0xBCCACF);
//        lView.setValueThreshold(10, 10, p);
        lView.setAxisThickness(5);


        LineSet threshLower = new LineSet(new String[]{"1", "2", "3", "4", "5", "6"}, new float[]{10,10,10,10,10,10,});

        threshLower.setColor(0x000000);

        threshLower.setDashed(new float[]{10, 10, 10, 10,10,10});
        threshLower.setSmooth(true);
        threshLower.setThickness(5);

        lView.addData(dataSet);
        lView.addData(threshLower);
        lView.show(anim);




    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutButton) {
            ParseUser.logOut();
            navigateToLogin();
        }
//        else if (id== R.id.profileButton){
//            startActivity(new Intent(this, ProfileActivity.class));
//        }

        return super.onOptionsItemSelected(item);
    }


}
