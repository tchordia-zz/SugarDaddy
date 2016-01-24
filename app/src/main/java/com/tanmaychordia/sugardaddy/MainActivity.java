package com.tanmaychordia.sugardaddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CircEase;
import com.parse.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.hound.android.fd.HoundSearchResult;
import com.hound.android.libphs.PhraseSpotterReader;
import com.hound.android.fd.Houndify;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.core.model.sdk.CommandResult;
import com.hound.core.model.sdk.HoundResponse;
import com.parse.ParseUser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private PhraseSpotterReader phraseSpotterReader;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    TextToSpeechMgr textToSpeechMgr;
    private int col = 0xFF5CB85C; // <<-- Put in HEX Code
    TextView bGlucose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser==null) {
            navigateToLogin();
        }

        // Text view for displaying written result
        textView = (TextView)findViewById(R.id.textView);

        // Setup TextToSpeech
        textToSpeechMgr = new TextToSpeechMgr( this );

        float[] bgData = updateGraph();

        drawGraph(bgData);
    }

    float[] updateGraph() {
        long timeStep = 1445214540000L;
        float[] results = new float[6];

        ParseQuery<ParseObject> query = ParseQuery.getQuery("data");
        query.whereEqualTo("flag", 1);
        try {
            ParseObject flagged = query.getFirst();
            timeStep = flagged.getLong("unixTimeStamp");
            flagged.put("flag", 0);
            flagged.saveInBackground();
            Log.d("Find Flag", flagged.getObjectId());
        }
        catch(ParseException e) {
            Log.d("Find Flag", "Error: " + e.getMessage());
        }
        query = ParseQuery.getQuery("data");
        query.orderByAscending("unixTimeStamp");
        query.whereGreaterThan("unixTimeStamp", timeStep);
        try {
            ParseObject nextFlag = query.getFirst();
            nextFlag.put("flag", 1);
            nextFlag.saveInBackground();
            Log.d("Set Next Flag", nextFlag.getObjectId() + " is " + nextFlag.getInt("flag"));
        }
        catch(ParseException e) {
            Log.d("Set Next Flag", "Error: " + e.getMessage());
        }
        query = ParseQuery.getQuery("data");
        query.orderByDescending("unixTimeStamp");
        query.whereLessThan("unixTimeStamp", timeStep);
        for(int i = 0; i < 6; i++) {
            try {
                ParseObject data = query.getFirst();
                results[5-i] = (float) data.getInt("Bg");
                query.whereLessThan("unixTimeStamp", data.getLong("unixTimeStamp"));
            } catch (ParseException e) {
                Log.d("Fill Results Array", "Failed");
            }
        }
        /*query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> data, ParseException e) {
                int i = 0;

                if (e == null) {
                    Log.d("Blood Glucose", "Retrieved " + data.size() + " Blood Glucose Data Points");
                    for(ParseObject person : data) {
                        results[i] = person.getInt("Bg");
                        i++;
                    }
                }
                else {
                    Log.d("Blood Glucose", "Error: " + e.getMessage());
                }
            }
        });*/
        return results;
    }

    void drawGraph(float[] data) {
        LineChartView lView= (LineChartView) findViewById(R.id.linechart);
        LineSet dataSet = new LineSet(new String[]{"1", "2", "3", "4", "5", "6"}, data);
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


        //LineSet threshLower = new LineSet(new String[]{"1", "2", "3", "4", "5", "6"}, new float[]{10,10,10,10,10,10});

//        LineSet threshLower = new LineSet(new String[]{"1", "2", "3", "4", "5", "6"}, new float[]{10,10,10,10,10,10,});
//
//        threshLower.setColor(0x000000);
//
//        threshLower.setDashed(new float[]{10, 10, 10, 10, 10, 10});
//        threshLower.setSmooth(true);
//        threshLower.setThickness(5);

        lView.addData(dataSet);
        lView.addData(createThresh(dataSet, 70));
        lView.addData(createThresh(dataSet, 200));

        lView.show(anim);


        bGlucose = (TextView)findViewById(R.id.bglucose);


        setGlucoseLevel((int) data[5]);
    }

    LineSet createThresh(LineSet d, int height)
    {

        LineSet thresh = new LineSet();
        int num = 0;
        try{
            for(int i = 0; true; i++)
            {
                num = i;
                String s = d.getLabel(i);
                thresh.addPoint(s, height);

            }

        }catch(Exception e) {

        }


        thresh.setColor(0x000000);
        thresh.setSmooth(true);
        thresh.setThickness(5);



        float[] f = new float[num];
        for(int i = 0; i < num; i++)
        {
            f[i] = height;
        }
        thresh.setDashed(f);
        return thresh;

    }

    void setGlucoseLevel(int num)
    {
        String ns = Integer.toString(num);
        int nn = ns.length();
        ns+= " bp";

        SpannableString ss1=  new SpannableString(ns);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,nn, 0); // set size

        ss1.setSpan(new ForegroundColorSpan(col), 0, nn, 0);// set color

        bGlucose.setText(ss1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startPhraseSpotting();
    }

    /**
     * Called to start the Phrase Spotter
     */
    private void startPhraseSpotting() {
        if ( phraseSpotterReader == null ) {
            phraseSpotterReader = new PhraseSpotterReader(new SimpleAudioByteStreamSource());
            phraseSpotterReader.setListener( phraseSpotterListener );
            phraseSpotterReader.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if ( phraseSpotterReader != null ) {
            stopPhraseSpotting();
        }
    }

    /**
     * Called to stop the Phrase Spotter
     */
    private void stopPhraseSpotting() {
        if ( phraseSpotterReader != null ) {
            phraseSpotterReader.stop();
            phraseSpotterReader = null;
        }
    }

    /**
     * Implementation of the PhraseSpotterReader.Listener interface used to handle PhraseSpotter
     * call back.
     */
    private final PhraseSpotterReader.Listener phraseSpotterListener = new PhraseSpotterReader.Listener() {
        @Override
        public void onPhraseSpotted() {

            // It's important to note that when the phrase spotter detects "Ok Hound" it closes
            // the input stream it was provided.
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopPhraseSpotting();
                    // Now start the HoundifyVoiceSearchActivity to begin the search.
                    Houndify.get( MainActivity.this ).voiceSearch( MainActivity.this );
                }
            });
        }

        @Override
        public void onError(final Exception ex) {

            // for this sample we don't care about errors from the "Ok Hound" phrase spotter.

        }
    };

    /**
     * The HoundifyVoiceSearchActivity returns its result back to the calling Activity
     * using the Android's onActivityResult() mechanism.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Houndify.REQUEST_CODE) {
            final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);

            if (result.hasResult()) {
                onResponse( result.getResponse() );
            }
            else if (result.getErrorType() != null) {
                onError(result.getException(), result.getErrorType());
            }
            else {
                textView.setText("Aborted search");
            }
        }
    }

    /**
     * Called from onActivityResult() above
     *
     * @param response
     */
    private void onResponse(final HoundResponse response) {
        if (response.getResults().size() > 0) {
            // Required for conversational support
            StatefulRequestInfoFactory.get(this).setConversationState(response.getResults().get(0).getConversationState());

            textView.setText("Received response\n\n" + response.getResults().get(0).getWrittenResponse());
            textToSpeechMgr.speak(response.getResults().get(0).getSpokenResponse());

            /**
             * "Client Match" demo code.
             *
             * Houndify client apps can specify their own custom phrases which they want matched using
             * the "Client Match" feature. This section of code demonstrates how to handle
             * a "Client Match phrase".  To enable this demo first open the
             * StatefulRequestInfoFactory.java file in this project and and uncomment the
             * "Client Match" demo code there.
             *
             * Example for parsing "Client Match"
             */
            if ( response.getResults().size() > 0 ) {
                CommandResult commandResult = response.getResults().get( 0 );
                if ( commandResult.getCommandKind().equals("ClientMatchCommand")) {
                    JsonNode matchedItemNode = commandResult.getJsonNode().findValue("MatchedItem");
                    String intentValue = matchedItemNode.findValue( "Intent").textValue();

                    if ( intentValue.equals("TURN_LIGHT_ON") ) {
                        textToSpeechMgr.speak("Client match TURN LIGHT ON successful");
                    }
                    else if ( intentValue.equals("TURN_LIGHT_OFF") ) {
                        textToSpeechMgr.speak("Client match TURN LIGHT OFF successful");
                    }
                    else if ( intentValue.equals("DISPLAY_RESULTS")) {
                        textToSpeechMgr.speak("Displaying Results.");
                    }
                }
            }
        }
        else {
            textView.setText("Received empty response!");
        }
    }

    /**
     * Called from onActivityResult() above
     *
     * @param ex
     * @param errorType
     */
    private void onError(final Exception ex, final VoiceSearchInfo.ErrorType errorType) {
        textView.setText(errorType.name() + "\n\n" + exceptionToString(ex));
    }

    private static String exceptionToString(final Exception ex) {
        try {
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        catch (final Exception e) {
            return "";
        }
    }


    /**
     * Helper class used for managing the TextToSpeech engine
     */
    class TextToSpeechMgr implements TextToSpeech.OnInitListener {
        private TextToSpeech textToSpeech;

        public TextToSpeechMgr( Activity activity ) {
            textToSpeech = new TextToSpeech( activity, this );
        }

        @Override
        public void onInit( int status ) {
            // Set language to use for playing text
            if ( status == TextToSpeech.SUCCESS ) {
                int result = textToSpeech.setLanguage(Locale.US);
            }
        }

        /**
         * Play the text to the device speaker
         *
         * @param textToSpeak
         */
        public void speak( String textToSpeak ) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
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
        else if(id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
//        else if (id== R.id.profileButton){
//            startActivity(new Intent(this, ProfileActivity.class));
//        }

        return super.onOptionsItemSelected(item);
    }


}
