package de.mop.hs_mannheim.mop_strassenschaeden;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class NewDamageStatusActivity extends AppCompatActivity {

    Button imageButton;
    ImageView imageView;

    static final int CAM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_damage_status_activity);

        imageButton = (Button) findViewById(R.id.captureDamageButton);
        imageView = (ImageView) findViewById(R.id.picturePreview);

        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);

            }
        });
    }

    private File getFile(){

        File folder = new File("sdcard/strassenschaden_meldung");

        if(!folder.exists()){
            folder.mkdir();
        }

        File image_file = new File(folder, "damage_status_image.jpg");

        return image_file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "sdcard/strassenschaden_meldung/damage_status_image.jpg";
        imageView.setImageDrawable(Drawable.createFromPath(path));
    }
}
