package voto.ado.sainthannaz.votoseguro.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 9/22/2015.
 */
public class ParseJSON {
    public static String[] ids;
    public static String[] code_votes;
    public static String[] votes;
    public static String[] date_votes;

    public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "id";
    public static final String KEY_CODEVOTE = "code_vote";
    public static final String KEY_VOTE = "vote";
    public static final String KEY_DATEVOTE = "date_vote";
    public static String[] initalArray;
    public static JSONArray allVotes = null;

    private String json;

    public ParseJSON(String json){
        this.json = json;
    }

    public void parseJSON(){
        JSONObject jsonObject=null;

        try {
            jsonObject = new JSONObject(json);
            allVotes = jsonObject.getJSONArray(JSON_ARRAY);
            //initalArray = new String[allVotes.length()];
            ids = new String[allVotes.length()];
            code_votes = new String[allVotes.length()];
            votes = new String[allVotes.length()];
            date_votes = new String[allVotes.length()];

            for(int i=0;i<allVotes.length();i++){
                JSONObject jo = allVotes.getJSONObject(i);
                ids[i] = jo.getString(KEY_ID);
                code_votes[i] = jo.getString(KEY_CODEVOTE);
                votes[i] = jo.getString(KEY_VOTE);
                date_votes[i] = jo.getString(KEY_DATEVOTE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
