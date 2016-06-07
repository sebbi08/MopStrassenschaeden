package de.mop.hs_mannheim.mop_strassenschaeden;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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

    OkHttpClient okclient = new OkHttpClient();

    LatLng loc;

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
                if(!file.exists()) return;
                Bitmap bm = BitmapFactory.decodeFile(path);


                try {

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                    byte[] data = bos.toByteArray();

                    JSONObject postData = new JSONObject();
                    postData.put("tiel",titel.getText());
                    postData.put("beschreibung",beschreibung.getText());
                    postData.put("bild",data);
                    postData.put("Location",loc.toString());



                    post(URL,postData.toString());



                } catch (Exception e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                }
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        imageView.setImageDrawable(Drawable.createFromPath(path));
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewDamageStatus Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.mop.hs_mannheim.mop_strassenschaeden/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewDamageStatus Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://de.mop.hs_mannheim.mop_strassenschaeden/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
