package ie.ul.csis.nutrition.user_interface;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.Observer;
import java.util.Observable;

/**
 * Created by ruppe on 08/07/2016.
 */
public class ConnectivityObserver extends Activity implements Observer {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.offline_mode_layout);

        //Register for network status updates

    }

        @Override
        public void update(Observable o, Object arg) {

            Log.d("ConnectivityObserver", "Connection Established, Uploading Points!");


        }
    }
