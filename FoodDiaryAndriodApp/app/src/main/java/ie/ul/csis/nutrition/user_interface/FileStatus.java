package ie.ul.csis.nutrition.user_interface;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by ruppe on 10/07/2016.
 */

public class FileStatus implements Runnable, Observer {

    List<Observer> observerList;
    Context context;
    AtomicBoolean doIt = new AtomicBoolean(true);

    public FileStatus(Context context) {
        this.context = context;
        observerList = new ArrayList<Observer>();
         }

    public static boolean isFilePresent(Context context) {


        boolean haveFile = true;

        File file = (File) context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files.length == 0) {
                //directory is empty
                haveFile = false;
            }
        }

        return haveFile;
    }




    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        while (true) {
            Log.d("FileStatuss", "Checking is file existing");
            if (isFilePresent(context)){
                Log.d("FileStatus", "Folder is not empty, files are existing");

            }
            else{
                Log.d("FileStatus", "Folder is empty, no files");
                doIt.set(false);

            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Log.d("FileStatus", "FileStatus is interupted");
                e.printStackTrace();
            }

        }
    }
    public void update(Observable o, Object arg) {

        Log.d("ConnectivityObserver", "File checked, Uploading Points!");


    }
}
