package ie.ul.csis.nutrition.utilities;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Jonathan on 13/09/2015.
 */
public  class Tools {

    public static void log(final String className, final String methodName, final String message){
        Log.d("MyApp", "   Class: " + className +
                       "   Method: " + methodName +
                       "   Message: " + message);
    }

    public static void log( final String message){
        Log.d("MyApp", "   Message: " + message);
    }

    public static void toast( final Context context, final String message){

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static boolean hasInternetConnection(Context context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if ( activeNetworkInfo != null
//                && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && activeNetworkInfo.isConnected())
        {
            return true;
        }

        toast(context, "No connection");
        return false;

    }
}
