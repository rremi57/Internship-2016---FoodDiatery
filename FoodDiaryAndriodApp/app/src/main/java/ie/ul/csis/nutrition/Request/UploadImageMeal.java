package ie.ul.csis.nutrition.Request;

import android.util.Base64;


import org.json.JSONArray;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.OutputStreamWriter;

import api.config.Config;
import api.config.ConfigReader;
import api.dto.Dto;
import api.dto.meals.MealsSaveImagesDto;
import api.httprequests.PostRequest;
import ie.ul.csis.nutrition.utilities.Tools;

/**
 * Created by Jonathan on 19/03/2016.
 */
public class UploadImageMeal  extends PostRequest{

    public UploadImageMeal() throws Exception {

        super(ConfigReader.getInstance().getElementAsString(Config.CHARSET_UTF8.getValue()),
                ConfigReader.getInstance().getElementAsString(Config.CONTENT_TYPE_APPLICATION_JSON.getValue()),
                ConfigReader.getInstance().getElementAsString(Config.MEALS_SAVE_IMAGE.getValue()),
                ConfigReader.getInstance().getElementAsInt(Config.MEALS_SAVE_IMAGE_TIMEOUT.getValue())
        );

    }


    @Override
    protected void sendDto(OutputStreamWriter outputStreamWriter, Dto dto) throws IOException
    {

        if(!(dto instanceof MealsSaveImagesDto))
            return;

        MealsSaveImagesDto mealDto = (MealsSaveImagesDto)dto;

        String json = "{\"Name\":";
        if(mealDto.Name ==  null)
        {
            outputStreamWriter.write(json+"\"Default Name\",");
        }
        else
        {
            outputStreamWriter.write(json+"\""+mealDto.Name+"\",");
        }
        outputStreamWriter.flush();




        if(mealDto.Images == null || mealDto.Images.length == 0)
        {
            json = "\"Images\": [],";
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }
        else
        {

            FileInputStream fileInputStream;


            json = "\"Images\": [";

            for(int currentFile = 0; currentFile < mealDto.Images.length; currentFile++)
            {

                json += "{\"Name\":" + "\"" + mealDto.Images[currentFile].getName()+"\",";
                json += "\"Base64\":" + "\"" ;
                outputStreamWriter.write(json);
                outputStreamWriter.flush();

                fileInputStream = new FileInputStream(mealDto.Images[currentFile]);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[8192];
                int bytesRead;
                while((bytesRead = fileInputStream.read(buffer)) != -1)
                {
                    byteArrayOutputStream.write(buffer,0,bytesRead);
                }
                fileInputStream.close();

                byte[] bytes = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();

                outputStreamWriter.write(Base64.encodeToString(bytes, Base64.DEFAULT));

                outputStreamWriter.flush();


                json = "\"}";
                if(currentFile != mealDto.Images.length-1)
                    json+=",";


                outputStreamWriter.write(json);
                outputStreamWriter.flush();
            }

            json = "],";
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
        }

        if(mealDto.FoodItems == null || mealDto.FoodItems.length == 0)
        {
            json = "\"FoodItems\": []}";
        }
        else
        {
//            json = "\"FoodItems\":"+new JSONArray(mealDto.FoodItems) + "}";
        }
        outputStreamWriter.write(json);
        outputStreamWriter.flush();


    }

}
