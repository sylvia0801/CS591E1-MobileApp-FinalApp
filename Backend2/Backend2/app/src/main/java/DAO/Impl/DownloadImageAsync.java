package DAO.Impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.example.backend.Activity.MainActivity.mytag;

public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {
    ImageView imv=null;
    String url;// url list imv list
    int i=0;
    public DownloadImageAsync(ImageView imv,String url) {
        super();
        this.imv=imv;
        this.url=url;
    }
    private  Bitmap getImageBitmapFromUrl(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(mytag, "Error getting bitmap", e);
        }
        return bm;
    }

    protected Bitmap doInBackground(String... urll) {
        if(urll.length>0) {
            url=urll[0];
            Log.i(mytag, "back froung"+url);

            return getImageBitmapFromUrl(url);
        }
        else return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap!=null){
           // Bitmap b = BitmapFactory.decodeByteArray(bitmapProfilePic , 0, bitmapProfilePic .length)
            imv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 50, 50, false));
        imv.setImageBitmap(bitmap);
        Log.i(mytag,"back setttt"+url+imv);
        }

    }

}
