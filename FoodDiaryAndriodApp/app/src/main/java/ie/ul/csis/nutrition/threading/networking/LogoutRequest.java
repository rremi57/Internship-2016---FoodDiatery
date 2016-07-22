package ie.ul.csis.nutrition.threading.networking;

import android.app.Activity;
import android.os.AsyncTask;

import api.httprequests.account.AccountLogOut;
import ie.ul.csis.nutrition.utilities.Tools;

/**
 * Created by Jonathan on 17/03/2016.
 */
public class LogoutRequest extends AsyncTask<Void, Void, AccountLogOut> {

    private String token;
    private Activity activity;

    public LogoutRequest(Activity activity, String token)
    {
        this.activity = activity;
        this.token = token;
    }

    @Override
    protected AccountLogOut doInBackground(Void... voids) {

        if(!Tools.hasInternetConnection(activity))
        {
            return null;
        }
        try
        {
            AccountLogOut logOut = new AccountLogOut(token);
            logOut.request(null);
            return logOut;
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
