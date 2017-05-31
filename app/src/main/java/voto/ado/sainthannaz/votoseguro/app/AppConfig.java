package voto.ado.sainthannaz.votoseguro.app;

import voto.ado.sainthannaz.votoseguro.helper.SessionManager;

/**
 * Created by SaintHannaz on 28/04/2016.
 */

public class AppConfig {

    private SessionManager session;

    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.6/android_login_api/login.php";

    // Server device MAC
    public static String URL_MAC = "http://192.168.0.6/android_login_api/checkMAC.php";

    // Server user register
    public static String URL_REGISTER_USER= "http://192.168.0.6/android_login_api/registerUser.php";

    // Server device register
    public static String URL_REGISTER_DEVICE= "http://192.168.0.6/android_login_api/registerDevice.php";

    // Server code register
    public static String URL_REGISTER_CODE= "http://192.168.0.6/android_login_api/registerCode.php";

    // Server vote register
    public static String URL_REGISTER_VOTE= "http://192.168.0.6/android_login_api/registerVote.php";

    // Server get region
    public static String URL_GET_REGION= "http://192.168.0.6/android_login_api/getRegion.php";

    // Server get code
    public static String URL_GET_CODE= "http://192.168.0.6/android_login_api/getCode.php";
    // Server get device MAC
    public static String URL_GET_MAC = "http://192.168.0.6/android_login_api/getMAC.php";

    // Server get vote
    public static String URL_GET_VOTE = "http://192.168.0.6/android_login_api/getVote.php";

    // Server get votes
    public static String URL_GET_VOTES = "http://192.168.0.6/android_login_api/getVotes.php";

    // Server close codes
    public static String URL_CLOSE_CODE = "http://192.168.0.6/android_login_api/closeCode.php";

    //Tags used in the JSON String
    public static final String TAG_REGION = "region";
    public static final String TAG_MAC = "mac";

    //JSON array name
    public static final String JSON_ARRAY_REGION = "result";
    public static final String JSON_ARRAY_MAC = "result";
    public static final String JSON_ARRAY_VOTES = "result";

}


