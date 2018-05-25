package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Filters extends AppCompatActivity {

    Uri croppedImageUri;
    Uri filterImageUri;
    Bitmap originalBmp;
    Bitmap croppedBmp;
    ImageView filterLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        setTitle("Filter");
        croppedImageUri = ((Variables) this.getApplication()).getCroppedImageUri();
        filterLarge = findViewById(R.id.filterLarge);

        //toolbar
        Toolbar filtersToolbar = findViewById(R.id.filter_toolbar);
        setSupportActionBar(filtersToolbar);
        filtersToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        filterLarge.setImageURI(croppedImageUri);

        //create bitmap
        try {
            originalBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);

            //change large preview size based on image orientation
            if (originalBmp.getWidth() > originalBmp.getHeight()) {
                ViewGroup.LayoutParams params = filterLarge.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                filterLarge.setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams params = filterLarge.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                filterLarge.setLayoutParams(params);
            }

            //crop bitmap into square
            if (originalBmp.getWidth() > originalBmp.getHeight()){
                croppedBmp = Bitmap.createBitmap(
                        originalBmp,
                        originalBmp.getWidth()/2 - originalBmp.getHeight()/2,
                        0,
                        originalBmp.getHeight(),
                        originalBmp.getHeight()
                );

            } else {
                croppedBmp = Bitmap.createBitmap(
                        originalBmp,
                        0,
                        originalBmp.getHeight()/2 - originalBmp.getWidth()/2,
                        originalBmp.getWidth(),
                        originalBmp.getWidth()
                );
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int thumbnailSize = Math.round(width/5);

        //thumbnail 1 [NO FILTER]
            ImageView filterView1 = findViewById(R.id.filter1);
            filterView1.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, thumbnailSize, thumbnailSize, false));

            ColorMatrix matrixNone = new ColorMatrix();
            final ColorMatrixColorFilter filterNone = new ColorMatrixColorFilter(matrixNone);

        //thumbnail 2 [GRAY SCALE]
            ImageView filterView2 = findViewById(R.id.filter2);
            filterView2.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, thumbnailSize, thumbnailSize, false));

            ColorMatrix matrixBW = new ColorMatrix();
            matrixBW.setSaturation(0);
            final ColorMatrixColorFilter filterBW = new ColorMatrixColorFilter(matrixBW);
            filterView2.setColorFilter(filterBW);

        //thumbnail 3 [SEPIA]
            ImageView filterView3 = findViewById(R.id.filter3);
            filterView3.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, thumbnailSize, thumbnailSize, false));

            ColorMatrix matrixSepia = new ColorMatrix();
            matrixSepia.setScale(1f, .95f, .82f, 1.0f);
            matrixBW.setConcat(matrixSepia, matrixBW);
            final ColorMatrixColorFilter filterSepia = new ColorMatrixColorFilter(matrixBW);
            filterView3.setColorFilter(filterSepia);

        //thumbnail 4 [CROSS PROCESS]
            ImageView filterView4 = findViewById(R.id.filter4);
            filterView4.setImageBitmap(Bitmap.createScaledBitmap(croppedBmp, thumbnailSize, thumbnailSize, false));

            ColorMatrix matrixCrossProcessBW = new ColorMatrix();
            ColorMatrix matrixCrossProcess = new ColorMatrix();
            matrixCrossProcess.setScale(.9f, .6f, .8f, 1.0f);
            matrixCrossProcessBW.setConcat(matrixCrossProcess, matrixCrossProcessBW);
            final ColorMatrixColorFilter filterCrossProcess = new ColorMatrixColorFilter(matrixCrossProcessBW);
            filterView4.setColorFilter(filterCrossProcess);


            //onclick events
            filterView1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setColorFilter(filterNone);
                }
            });
            filterView2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setColorFilter(filterBW);
                }
            });
            filterView3.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setColorFilter(filterSepia);
                }
            });
            filterView4.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setColorFilter(filterCrossProcess);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create Bitmap.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return true;
    }

    //options menu onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.accept:
                saveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //save image with filter
    private void saveImage() {
        OutputStream output;
        Bitmap bitmap = loadBitmapFromView(findViewById(R.id.filterLarge));

        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/DCIM/Apic/");
        String imageName = System.currentTimeMillis() + ".jpg";
        dir.mkdirs();

        //Create a name for the saved image
        File file = new File(dir, imageName);

        try {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            output.flush();
            output.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterImageUri = Uri.parse(dir + "/" + imageName);
        selectCopies();
    }
    private Bitmap loadBitmapFromView(View v) {
        final int w = v.getWidth();
        final int h = v.getHeight();
        final Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        final Canvas c = new  Canvas(b);

        //v.layout(0, 0, w, h);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    //next step
    private void selectCopies() {
        ((Variables) this.getApplication()).setFilterImageUri(filterImageUri);
        Intent copySelector = new Intent(this, CopySelector.class);
        startActivity(copySelector);
    }
}
