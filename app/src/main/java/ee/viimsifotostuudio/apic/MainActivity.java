package ee.viimsifotostuudio.apic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;

//crop library
import com.theartofdev.edmodo.cropper.CropImage;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    int PICK_IMAGE = 0;
    Uri originalImageUri;
    Uri croppedImageUri;
    Bitmap originalBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        //image selector button action
        Button imageSelector = findViewById(R.id.selectImage);
        imageSelector.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                checkForPermission();
            }
        });
    }

    public void checkForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    //gallery method
    private void openGallery() {
        String[] mimeTypes = {"image/jpg", "image/jpeg","image/png"};
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //crop method
    private void cropImage() {

        //get orientation
        try {
            originalBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), originalImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (originalBmp.getWidth() <= originalBmp.getHeight()){
            CropImage.activity(originalImageUri)
                    .setAspectRatio(10, 15)
                    .setAutoZoomEnabled(true)
                    .start(this);
        } else {
            CropImage.activity(originalImageUri)
                    .setAspectRatio(15, 10)
                    .setAutoZoomEnabled(true)
                    .start(this);
        }
    }

    //next step
    private void applyFilter() {
        ((Variables) this.getApplication()).setOriginalImageUri(originalImageUri);
        ((Variables) this.getApplication()).setCroppedImageUri(croppedImageUri);
        Intent filters = new Intent(this, Filters.class);
        startActivity(filters);
    }

    //activity results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //image selection result
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            originalImageUri = data.getData();
            cropImage();
        }

        //image crop result
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                croppedImageUri = result.getUri();
                applyFilter();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                e.printStackTrace();
            }
        }
    }

    //create options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_popup, menu);
        return true;
    }

    //options menu onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.terms:
                Intent terms = new Intent(this, TermsAndConditions.class);
                startActivity(terms);
                break;
            case R.id.license:
                Intent license = new Intent(this, License.class);
                startActivity(license);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
