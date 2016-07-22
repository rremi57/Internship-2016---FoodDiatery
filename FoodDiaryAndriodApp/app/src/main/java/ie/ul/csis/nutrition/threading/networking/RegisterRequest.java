package ie.ul.csis.nutrition.threading.networking;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import api.dto.accounts.AccountRegisterDto;
import api.httprequests.account.AccountRegister;
import ie.ul.csis.nutrition.user_interface.RegisterActivity;
import ie.ul.csis.nutrition.utilities.Tools;


/**
 * Created by Jonathan on 28/01/2016.
 */
public class RegisterRequest extends AsyncTask<AccountRegisterDto, Void,  AccountRegister>
{
    private final RegisterActivity activity;
    private ProgressDialog progressDialog;


    public RegisterRequest(RegisterActivity activity)
    {
        this.activity = activity;
        progressDialog = activity.getProgressDialog();
    }


    @Override
    protected void onPreExecute()
    {
        progressDialog.show();
    }

    @Override
    protected AccountRegister doInBackground(AccountRegisterDto... dto) {

        if(dto == null || !Tools.hasInternetConnection(activity))
        {
            return null;
        }

        try{
            AccountRegister register = new AccountRegister();
            register.request(dto[0]);
            return register;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(AccountRegister register)
    {
        if(register == null)
        {
            progressDialog.dismiss();
            return;
        }

        if(register.getResponseCode() == register.getExpectedResponceCode())
        {
            progressDialog.dismiss();
            Tools.toast(activity, "Email sent to your account, please activate!");
            activity.finish();
        }
        else
        {
            progressDialog.dismiss();
            Tools.toast(activity, register.getResponseMessage());
        }
    }
}
