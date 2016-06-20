package de.mop.hs_mannheim.mop_strassenschaeden;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewDamageStatusActivity extends AppCompatActivity {

    private Button imageButton;
    private ImageView imageView;
    private Button sendButton;
    private EditText titel;
    private EditText beschreibung;
    private File file;
    private String path = "sdcard/strassenschaden_meldung/damage_status_image.jpg";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String URL = "http://mop.kaeltis.de:3000/strassenschaden";

    static final int CAM_REQUEST = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private OkHttpClient okclient = new OkHttpClient();


    private JSONObject postData;

    private LatLng loc;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_damage_status_activity);

        loc = new LatLng(getIntent().getDoubleExtra("Lat",0),getIntent().getDoubleExtra("Lon",0));

        imageButton = (Button) findViewById(R.id.captureDamageButton);
        imageView = (ImageView) findViewById(R.id.picturePreview);
        sendButton = (Button) findViewById(R.id.sendStatus);
        titel = (EditText) findViewById(R.id.titel);
        beschreibung = (EditText) findViewById(R.id.beschreibung);

        file = getFile();

        this.context = this;

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);

            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!file.exists()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Bitte eine Bild aufnehmen");
                    alert.setTitle("Kein Bild");
                    alert.create().show();
                    return;
                }
                Bitmap bm = BitmapFactory.decodeFile(path);


                try {

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    byte[] data = bos.toByteArray();

                    postData = new JSONObject();
                    postData.put("titel",titel.getText());
                    postData.put("beschreibung",beschreibung.getText());
                    postData.put("bild",data);
                    postData.put("Location",loc.toString());


                    AsyncTask<String,Void,String> task = new AsyncTask<String,Void,String>(){

                        @Override
                        protected String doInBackground(String... params) {

                            try {
                                post(params[0],params[1]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return "ok";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setMessage("Bericht Gesendet");
                            alert.setTitle("Erfolg");
                            alert.create().show();
                            titel.setText("");
                            beschreibung.setText("");
                            resettFile();
                            imageView.setImageDrawable(Drawable.createFromPath(path));
                        }
                    };

                    task.execute(URL,postData.toString());



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resettFile() {
        File fileToDelete = new File("sdcard/strassenschaden_meldung/damage_status_image.jpg");
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
        file = getFile();
    }

    private File getFile() {

        File folder = new File("sdcard/strassenschaden_meldung");

        if (!folder.exists()) {
            folder.mkdir();
        }

        return new File(folder, "damage_status_image.jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File file = new File(path);
        if(file.exists())
        imageView.setImageDrawable(Drawable.createFromPath(path));
    }



    private String post (String url, String json)throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okclient.newCall(request).execute();
        return response.body().string();
    }
}
