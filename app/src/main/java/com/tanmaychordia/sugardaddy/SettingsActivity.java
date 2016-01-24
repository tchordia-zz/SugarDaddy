package com.tanmaychordia.sugardaddy;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {
    TextView cGlucose;

    private int col = 0xFF5CB85C; // <<-- Put in HEX Code
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(R.id.listview, new GeneralPreferenceFragment()).commit();
        setContentView(R.layout.activity_child);

        cGlucose = (TextView) findViewById(R.id.cglucose);
        setGlucoseLevel((int) MainActivity.getData()[MainActivity.numData - 1]);
    }

    void setGlucoseLevel(int num) {
        String ns = Integer.toString(num);
        int nn = ns.length();
        ns += " bp";

        SpannableString ss1 = new SpannableString(ns);
        ss1.setSpan(new RelativeSizeSpan(2f), 0, nn, 0); // set size

        ss1.setSpan(new ForegroundColorSpan(col), 0, nn, 0);// set color

        cGlucose.setText(ss1);

    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);




            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            final Preference myPref = findPreference("checkbox");

            myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Log.v("click", myPref.getKey());

                    String messageToSend = "Your child brought diabetes strips today.";
                    String number = "5038939839";
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("checkListMaster");


                    query.getInBackground("pEXG4z9D9u", new GetCallback<ParseObject>() {
                        public void done(ParseObject gameScore, ParseException e) {
                            if (e == null) {
                                // Now let's update it with some new data. In this case, only cheatMode and score
                                // will get sent to the Parse Cloud. playerName hasn't changed.
                                gameScore.put("didBringStrips", 1);
                                gameScore.saveInBackground();
                            }
                        }
                    });

                    return true;
                }
            });

            final Preference myPref2 = findPreference("checkbox2");

            myPref2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    String messageToSend = "Your child ate today.";
                    String number = "5038939839";
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("checkListMaster");


                    query.getInBackground("pEXG4z9D9u", new GetCallback<ParseObject>() {
                        public void done(ParseObject gameScore, ParseException e) {
                            if (e == null) {
                                // Now let's update it with some new data. In this case, only cheatMode and score
                                // will get sent to the Parse Cloud. playerName hasn't changed.
                                gameScore.put("didEat", 1);
                                gameScore.saveInBackground();
                            }
                        }
                    });
                    Log.v("click", myPref2.getKey());
                    return true;
                }
            });

            final Preference myPref3 = findPreference("checkbox3");

            myPref3.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    String messageToSend = "Your child measured blood glucose today";
                    String number = "5038939839";
                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("checkListMaster");


                    query.getInBackground("pEXG4z9D9u", new GetCallback<ParseObject>() {
                        public void done(ParseObject gameScore, ParseException e) {
                            if (e == null) {
                                // Now let's update it with some new data. In this case, only cheatMode and score
                                // will get sent to the Parse Cloud. playerName hasn't changed.
                                gameScore.put("didMeasure", 1);
                                gameScore.saveInBackground();
                            }
                        }
                    });
                    Log.v("click", myPref3.getKey());
                    return true;
                }
            });
        }
    }
}
