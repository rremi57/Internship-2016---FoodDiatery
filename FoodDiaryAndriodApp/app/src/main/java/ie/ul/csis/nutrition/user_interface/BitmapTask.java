package ie.ul.csis.nutrition.user_interface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.widget.ImageView;


/**
 * Created by Jonathan on 11/04/2016.
 */
public class BitmapTask extends AsyncTask<String, Void, Bitmap> {

    private int imageWidth;
    private int imageHeight;
    private ImageView imageView;

    public BitmapTask(ImageView view)
    {
        this.imageView = view;
        this.imageHeight = view.getHeight()/2;
        this.imageWidth = view.getWidth()/2;
    }

    @Override
    protected Bitmap doInBackground(String... params)
    {

        if(params.length == 0)
        {
            return null;
        }

        try
        {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(params[0], options);

            options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(params[0],options);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  null;
        }
    }


    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        System.out.println(inSampleSize);
        return inSampleSize;
    }
    @Override
    protected void onPostExecute(Bitmap image) {

        if (image != null) {
            imageView.setImageBitmap(image);
        }
    }


}
