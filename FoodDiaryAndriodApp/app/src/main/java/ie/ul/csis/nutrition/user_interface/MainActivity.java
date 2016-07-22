package ie.ul.csis.nutrition.user_interface;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.util.concurrent.atomic.*;
import java.lang.Thread.*;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import api.dto.meals.MealsSaveImagesDto;
import ie.ul.csis.nutrition.R;
import ie.ul.csis.nutrition.threading.networking.LogoutRequest;
import ie.ul.csis.nutrition.threading.networking.SaveImagesRequest;
import ie.ul.csis.nutrition.utilities.Tools;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private Button btnTakenPhoto;
    private Button btnSendPhoto;
    private ImageView imageView;
    private File imageFile;
    private final int PHOTO_SUCCESS = 0;
    //    private Bitmap photo;
    private Intent intent;
    private Context context;
    private boolean hasFaces;

    private ProgressDialog pDialog;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
  //startService(new Intent( MainActivity.this,NetworkStatus.class));



        // Thread for Files
        FileStatus myFileRunnable = new FileStatus(this);
        Thread myFileThread = new Thread(myFileRunnable);
        ConnectivityObserver myFileObserver = new ConnectivityObserver();
             // myFileRunnable.addObserver(myFileObserver);
        myFileThread.start();





        // Thread for Connectivity
        NetworkStatus myCoRunnable = new NetworkStatus(this);
        Thread myCoThread = new Thread(myCoRunnable);
        myCoRunnable.addObserver(myFileRunnable);
        ConnectivityObserver myCoObserver = new ConnectivityObserver();
        myCoRunnable.addObserver(myCoObserver);
        myCoThread.start();
        myCoThread.interrupt();





        context = this;
        btnTakenPhoto = (Button) findViewById(R.id.btn_takenPic);
        btnSendPhoto = (Button) findViewById(R.id.btn_sendPic);
        imageView = (ImageView) findViewById(R.id.displayImage);
        imageFile = null;

        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Uploading");
        pDialog.setMessage("Please Wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);

        hasFaces = false;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},10);

                return;
            } else {
                configureButtons();
            }
        }else{
            configureButtons();
        }


    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
  //ERRORTHERE      mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                configureButtons();
            }
        }
    }

    private void configureButtons()
    {

        btnTakenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              dispatchTakePictureIntent();
            }
        });

        btnSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pDialog.show();

                if (imageView.getDrawable() == null) {
                    Tools.toast(context, "Please take a picture");
                    return;
                }

                if (hasFaces) {
                    Tools.toast(context, "Please retake photo." +
                            " Cannot send images that contain faces");
                    return;
                }
                SendImage();

            }
        });

    }

    public void SendImage()
    {
        MealsSaveImagesDto dto = new MealsSaveImagesDto(null, null, new File[]{imageFile} );
        SharedPreferences preferences =
                getSharedPreferences(context.getString(R.string.sharedPerfs), MODE_PRIVATE);

        SaveImagesRequest request = new SaveImagesRequest(this,
                preferences.getString(context.getString(R.string.tokenKey), "") );
        request.execute(dto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.logout:
                logout();
                context.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.action_settings:
                Tools.toast(context, "There are no settings currently");
                break;
            default:
                Tools.toast(context, "unknown menu Item");
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_SUCCESS) {

      //ERRORTHERE      new BitmapTask(imageView).execute(imageFile.getPath());
        }
        else if (imageView.getDrawable() == null)
        {
          //  Tools.toast(context, "Please capture meal");
        }

    }

    private void logout(){

        SharedPreferences preferences =
                getSharedPreferences(context.getString(R.string.sharedPerfs), MODE_PRIVATE);

        LogoutRequest logout = new LogoutRequest(this,
                preferences.getString(context.getString(R.string.tokenKey), "") );
        logout.execute();


        SharedPreferences.Editor  editor = preferences.edit();
        editor.remove(context.getString(R.string.tokenKey));
        editor.remove(context.getString(R.string.tokenExpiryTimeKey));
        editor.remove(context.getString(R.string.rememberMeKey));
        editor.apply();
    }















    public File getImageFile(){return  imageFile;}

    public ProgressDialog getProgressDialog(){
        return pDialog;
    }

    public ImageView getImageView(){return imageView;}
}