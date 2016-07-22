package ie.ul.csis.nutrition.threading.networking;

import android.app.ProgressDialog;

import android.os.AsyncTask;

import api.dto.meals.MealsSaveImagesDto;
import ie.ul.csis.nutrition.Request.UploadImageMeal;
import ie.ul.csis.nutrition.user_interface.MainActivity;
import ie.ul.csis.nutrition.utilities.Tools;



public class SaveImagesRequest extends AsyncTask<MealsSaveImagesDto, Void, UploadImageMeal> {

    private final MainActivity activity;
    private ProgressDialog progressDialog;
    private String token;

    public SaveImagesRequest(MainActivity activity, String token)
    {
        this.activity = activity;
        this.token = token;
        progressDialog = activity.getProgressDialog();
    }


    @Override
    protected void onPreExecute()
    {
        progressDialog.show();
    }

    @Override
    protected UploadImageMeal doInBackground(MealsSaveImagesDto... dto)
    {

        if(dto.length == 0 && !Tools.hasInternetConnection(activity))
        {
            return null;
        }

        try
        {
            UploadImageMeal meal = new UploadImageMeal();
            Tools.log("token :" + token);
            meal.setToken(token);
            meal.request(dto[0]);
            return meal;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    protected void onPostExecute(UploadImageMeal meal)
    {
        if(meal == null)
        {
            progressDialog.dismiss();
            return;
        }

        if(meal.getResponseCode() == meal.getExpectedResponceCode())
        {
            activity.getImageView().setImageDrawable(null);
            activity.getImageFile().delete();
            Tools.toast(activity, "Image sent successfully");
        }
        else
        {
//            Tools.toast(activity, meal.getResponseMessage());
            Tools.toast(activity, "Image failed to send. Error code " + meal.getResponseCode());
            Tools.log(meal.getResponseMessage());
        }

        progressDialog.dismiss();

    }

}
