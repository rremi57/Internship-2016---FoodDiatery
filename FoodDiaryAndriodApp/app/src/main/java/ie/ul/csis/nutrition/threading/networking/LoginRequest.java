package ie.ul.csis.nutrition.threading.networking;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import api.dto.accounts.AccountTokenDto;
import api.httprequests.account.AccountToken;
import ie.ul.csis.nutrition.R;
import ie.ul.csis.nutrition.user_interface.LoginActivity;
import ie.ul.csis.nutrition.utilities.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;



/**
 * Created by Jonathan on 28/01/2016.
 */


public class LoginRequest extends AsyncTask<AccountTokenDto, Void, AccountToken> {

    private final LoginActivity activity;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;


    public LoginRequest(LoginActivity activity) {
        this.activity = activity;
        preferences = activity.getSharedPreferences(
                activity.getApplicationContext().getString(
                        R.string.sharedPerfs), activity.MODE_PRIVATE);
        progressDialog = activity.getProgressDialog();
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected AccountToken doInBackground(AccountTokenDto... dtos) {
        if (dtos.length == 0 || !Tools.hasInternetConnection(activity)) {
            return null;
        }
        try {
            AccountToken token = new AccountToken();
            token.request(dtos[0]);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    @Override
    protected void onPostExecute(AccountToken token) {
        if (token == null )
        {
            progressDialog.dismiss();
            return;
        }

        try {
            if (token.getResponseCode() == token.getExpectedResponceCode()) {
                SharedPreferences.Editor editor = preferences.edit();
                JSONObject jsonResponse = new JSONObject(token.getResponseMessage());
                Tools.log("Json :" + jsonResponse.toString());
                Tools.log("Access Token : " + jsonResponse.get("access_token").toString());
                editor.putString(activity.getString(R.string.tokenKey),
                        jsonResponse.get("access_token").toString());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, jsonResponse.getInt("expires_in") - 60);
                editor.putLong(activity.getString(R.string.tokenExpiryTimeKey),
                        cal.getTimeInMillis());
                editor.commit();
                progressDialog.dismiss();
      //          activity.changeToMainActivity();

            }
            else
            {
                Tools.toast(activity, "Invalid Email or Password");
                progressDialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Tools.log("Error with login");
        }

    }
}
