package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;

//crop library
import com.theartofdev.edmodo.cropper.CropImage;

public class GalleryAndCrop extends AppCompatActivity {

    int PICK_IMAGE = 0;
    Uri originalImageUri;
    Uri croppedImageUri;
    Bitmap originalBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_and_crop);

        openGallery();
    }

    //gallery method
    public void openGallery() {
        String[] mimeTypes = {"image/jpg", "image/jpeg","image/png"};
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    //activity results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //image selection result
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            originalImageUri = data.getData();
            cropImage();
        } else {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
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
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            }
        }
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
}
