package ie.ul.csis.nutrition.user_interface;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import api.dto.accounts.AccountTokenDto;
import ie.ul.csis.nutrition.threading.networking.LoginRequest;
import ie.ul.csis.nutrition.utilities.Tools;

/**
 * Created by ruppe on 07/07/2016.
 */
    public class NetworkStatus extends Observable implements Runnable {

        List<Observer> observerList;
        Context context;
        public NetworkStatus(Context context) {
            this.context = context;
            observerList = new ArrayList<Observer>();

        }
        String email = "test87@gmail.com";
        String password = "Password123";

        AccountTokenDto dto = new AccountTokenDto(email, password);
        LoginRequest request = new LoginRequest(this);
        request.execute(dto);



        public static boolean isConnectedToInternet(Context context) {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }

        /**
         * Add observer to list
         */
        @Override
        public void addObserver(Observer o) {
            observerList.add(o);

        }

        /**
         * Remove NetwokObserver from list
         */
        @Override
        public void deleteObserver(Observer o) {
            observerList.remove(o);
        }

        @Override
        public void notifyObservers() {
            for(Observer observer :  observerList) {
                observer.update(this,this);
            }
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            while(true) {
                if (Thread.interrupted()) {
                    Log.d("NetworkStatus", "NetworkStatus is interupted");
                }

                Log.d("NetworkStatus", "Checking Network Status");
                if(isConnectedToInternet(context)){
                    notifyObservers();
                   Log.d("NetworkStatus", "Network Connection is established");

                }
                else {
                    Log.d("NetworkStatus", "Not connected to network");
                }
                try {
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    Log.d("NetworkStatus", "NetworkStatus is interupted");
                    e.printStackTrace();
                }
            }

        }
    }

