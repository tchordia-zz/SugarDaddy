package com.tanmaychordia.sugardaddy;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hound.android.fd.DefaultRequestInfoFactory;
import com.hound.core.model.sdk.ClientMatch;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * We use a singleton in order to not hold a memory reference to the host activity since this is registered in the Houndify
 * singleton.
 */
public class StatefulRequestInfoFactory extends DefaultRequestInfoFactory {

    public static StatefulRequestInfoFactory instance;

    private JsonNode conversationState;

    public static StatefulRequestInfoFactory get(final Context context) {
        if (instance == null) {
            instance= new StatefulRequestInfoFactory(context);
        }
        return instance;
    }

    private StatefulRequestInfoFactory(Context context) {
        super(context);
    }

    public void setConversationState(JsonNode conversationState) {
        this.conversationState = conversationState;
    }

    @Override
    public HoundRequestInfo create() {
        final HoundRequestInfo requestInfo = super.create();
        requestInfo.setConversationState(conversationState);

        /*
         * "Client Match"
         *
         * Below is sample code to demonstrate how to use the "Client Match" Houndify feature which
         * lets client apps specify their own custom phrases to match.  To try out this
         * feature you must:
         *
         * 1. Enable the "Client Match" domain from the Houndify website: www.houndify.com.
         * 2. Uncomment the code below.
         * 3. And finally, to see how the response is handled in go to the MainActivity and see
         *    "Client Match" demo code inside of onResponse()
         *
         * This example allows the user to say "turn on the lights", "turn off the lights", and
         * other variations on these phases.
         */

        //Uncomment for Client Match demo --------------------------------------------
        ArrayList<ClientMatch> clientMatchList = new ArrayList<>();

        ClientMatch clientMatch0 = new ClientMatch();
        clientMatch0.setExpression("(\"How\"|\"What\").(\"is\"|\"are\").[(\"my\"|\"the\")].(\"kid\"|\"child\"|\"children\"|\"son\"|\"daughter\").[\"doing\"]");
        clientMatch0.setSpokenResponse("His current blood glucose level is "+MainActivity.getData()[MainActivity.numData-1]);
        clientMatch0.setSpokenResponseLong("His current blood glucose level is "+MainActivity.getData()[MainActivity.numData-1]);
        clientMatch0.setWrittenResponse("Displaying Results...");
        clientMatch0.setWrittenResponseLong("Displaying Results...");

        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode result0Node = nodeFactory.objectNode();
        result0Node.put("Intent", "DISPLAY_RESULTS");
        clientMatch0.setResult(result0Node);

        clientMatchList.add(clientMatch0);

        final ClientMatch clientMatch1 = new ClientMatch();
        clientMatch1.setExpression("(\"Has\").[(\"my\"|\"the\")].(\"kid\"|\"child\"|\"children\"|\"son\"|\"daughter\").(\"measured\").[\"today\"]");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("checkListMaster");
        query.getInBackground("pEXG4z9D9u", new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data. In this case, only cheatMode and score
                    // will get sent to the Parse Cloud. playerName hasn't changed.
                    if ( (Integer)gameScore.get("didMeasure") > 0) {
                        clientMatch1.setSpokenResponse("Yes, your child has measured today");
                        clientMatch1.setSpokenResponseLong("Yes, your child has measured today");
                        clientMatch1.setWrittenResponse("Yes, your child has measured today");
                        clientMatch1.setWrittenResponseLong("Yes, your child has measured today");
                    }
                    else {
                        clientMatch1.setSpokenResponse("No, your child has not measured today");
                        clientMatch1.setSpokenResponseLong("No, your child has not measured today");
                        clientMatch1.setWrittenResponse("No, your child has not measured today");
                        clientMatch1.setWrittenResponseLong("No, your child has not measured today");
                    }
                }
            }
        });

        ObjectNode result1Node = nodeFactory.objectNode();
        result1Node.put("Intent", "PARENT_VIEW");
        clientMatch1.setResult(result1Node);

        clientMatchList.add(clientMatch1);

        //ClientMatch clientMatch2 = new ClientMatch();
        //clientMatch2.setExpression("(\"How\").(\"many\").(\"strips\").(\"does\").(\"my\").(\"child\").(\"have\")");


        // client match 1
        /*ClientMatch clientMatch1 = new ClientMatch();
        clientMatch1.setExpression("([1/100 (\"can\"|\"could\"|\"will\"|\"would\").\"you\"].[1/10 \"please\"].(\"turn\"|\"switch\"|(1/100 \"flip\")).\"on\".[\"the\"].(\"light\"|\"lights\").[1/20 \"for\".\"me\"].[1/20 \"please\"]) \n" +
                "| \n" +
                "([1/100 (\"can\"|\"could\"|\"will\"|\"would\").\"you\"].[1/10 \"please\"].[100 (\"turn\"|\"switch\"|(1/100 \"flip\"))].[\"the\"].(\"light\"|\"lights\").\"on\".[1/20 \"for\".\"me\"].[1/20 \"please\"]) \n" +
                "| \n" +
                "(((\"i\".(\"want\"|\"like\"))|(((\"i\".[\"would\"])|(\"i'd\")).(\"like\"|\"want\"))).[\"the\"].(\"light\"|\"lights\").[\"turned\"|\"switched\"|(\"to\".\"go\")|(1/100\"flipped\")].\"on\".[1/20\"please\"]) ");

        clientMatch1.setSpokenResponse("Ok, I'm turning the lights on.");
        clientMatch1.setSpokenResponseLong("Ok, I am turning the lights on.");
        clientMatch1.setWrittenResponse("Ok, I'm turning the lights on.");
        clientMatch1.setWrittenResponseLong("Ok, I am turning the lights on.");

        //final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode result1Node = nodeFactory.objectNode();
        result1Node.put("Intent", "TURN_LIGHT_ON");
        clientMatch1.setResult(result1Node);

        // add first client match data to the array/list
        clientMatchList.add(clientMatch1);

        // client match 2
        ClientMatch clientMatch2 = new ClientMatch();
        clientMatch2.setExpression("([1/100 (\"can\"|\"could\"|\"will\"|\"would\").\"you\"].[1/10 \"please\"].(\"turn\"|\"switch\"|(1/100 \"flip\")).\"off\".[\"the\"].(\"light\"|\"lights\").[1/20 \"for\".\"me\"].[1/20 \"please\"]) \n" +
                "| \n" +
                "([1/100 (\"can\"|\"could\"|\"will\"|\"would\").\"you\"].[1/10 \"please\"].[100 (\"turn\"|\"switch\"|(1/100 \"flip\"))].[\"the\"].(\"light\"|\"lights\").\"off\".[1/20 \"for\".\"me\"].[1/20 \"please\"]) \n" +
                "| \n" +
                "(((\"i\".(\"want\"|\"like\"))|(((\"i\".[\"would\"])|(\"i'd\")).(\"like\"|\"want\"))).[\"the\"].(\"light\"|\"lights\").[\"turned\"|\"switched\"|(\"to\".\"go\")|(1/100\"flipped\")].\"off\".[1/20\"please\"]) ");

        clientMatch2.setSpokenResponse("Ok, I'm turning the lights off.");
        clientMatch2.setSpokenResponseLong("Ok, I am turning the lights off.");
        clientMatch2.setWrittenResponse("Ok, I'm turning the lights off.");
        clientMatch2.setWrittenResponseLong("Ok, I am turning the lights off.");

        ObjectNode result2Node = nodeFactory.objectNode();
        result2Node.put("Intent", "TURN_LIGHT_OFF");
        clientMatch2.setResult(result2Node);

        // add next client match data to the array/list
        clientMatchList.add(clientMatch2);*/

        // add as many more client match entries as you like...


        // add the list of matches to the request info object
        requestInfo.setClientMatches(clientMatchList);
        //------------------------------------end of Client Match demo code */

        return requestInfo;
    }
}
