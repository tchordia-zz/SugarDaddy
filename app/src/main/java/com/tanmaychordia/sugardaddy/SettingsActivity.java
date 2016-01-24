package com.tanmaychordia.sugardaddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.hound.android.fd.HoundSearchResult;
import com.hound.android.fd.Houndify;
import com.hound.android.libphs.PhraseSpotterReader;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.core.model.sdk.CommandResult;
import com.hound.core.model.sdk.HoundResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;


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
    private TextView textView;
    private PhraseSpotterReader phraseSpotterReader;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    TextToSpeechMgr textToSpeechMgr;

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

        // Text view for displaying written result
        textView = (TextView) findViewById(R.id.textView);
        // Setup TextToSpeech
        textToSpeechMgr = new TextToSpeechMgr(this);

        cGlucose = (TextView) findViewById(R.id.cglucose);
        setGlucoseLevel(69);
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

        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.
         *
         * @see #sBindPreferenceSummaryToValueListener
         */
        private static void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {
                String stringValue = value.toString();


                preference.setEnabled(false);
                System.out.println();

                return true;
            }
        };


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
        if (phraseSpotterReader == null) {
            phraseSpotterReader = new PhraseSpotterReader(new SimpleAudioByteStreamSource());
            phraseSpotterReader.setListener(phraseSpotterListener);
            phraseSpotterReader.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if (phraseSpotterReader != null) {
            stopPhraseSpotting();
        }
    }

    /**
     * Called to stop the Phrase Spotter
     */
    private void stopPhraseSpotting() {
        if (phraseSpotterReader != null) {
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
                    Houndify.get(SettingsActivity.this).voiceSearch(SettingsActivity.this);
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
                onResponse(result.getResponse());
            } else if (result.getErrorType() != null) {
                onError(result.getException(), result.getErrorType());
            } else {
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
        } else {
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
        } catch (final Exception e) {
            return "";
        }
    }


    /**
     * Helper class used for managing the TextToSpeech engine
     */
    class TextToSpeechMgr implements TextToSpeech.OnInitListener {
        private TextToSpeech textToSpeech;

        public TextToSpeechMgr(Activity activity) {
            textToSpeech = new TextToSpeech(activity, this);
        }

        @Override
        public void onInit(int status) {
            // Set language to use for playing text
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
            }
        }

        /**
         * Play the text to the device speaker
         *
         * @param textToSpeak
         */
        public void speak(String textToSpeak) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }
}
