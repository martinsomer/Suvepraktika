package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.Objects;

public class Filters extends AppCompatActivity {

    Uri croppedImageUri;
    Bitmap originalBmp;
    Bitmap croppedBmp;
    ImageView filterLarge;
    int currentFilter = 0;
    Bitmap FilterBitmap;

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

            /*
                MAKE PREVIEW FIT PERFECTLY
            */

            originalBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);

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

            /*
                CREATE SQUARE BITMAP FOR THUMBNAILS
            */

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
            final int thumbnailSize = Math.round(width/5);

            /*
                COLOR FILTERS
            */

            ColorMatrix GrayScaleMatrix = new ColorMatrix();
            GrayScaleMatrix.setSaturation(0);
            final ColorMatrixColorFilter GrayScaleFilter = new ColorMatrixColorFilter(GrayScaleMatrix);

            ColorMatrix SepiaMatrix = new ColorMatrix();
            SepiaMatrix.setScale(1f, .95f, .82f, 1f);

            GrayScaleMatrix.setConcat(SepiaMatrix, GrayScaleMatrix);
            final ColorMatrixColorFilter SepiaFilter = new ColorMatrixColorFilter(GrayScaleMatrix);

        /*
            GENERATE THUMBNAIL FROM CROPPED BITMAP
        */

            Bitmap ThumbnailBitmap = Bitmap.createScaledBitmap(croppedBmp, thumbnailSize, thumbnailSize, false);

            //thumbnail 1
            ImageView filterView1 = findViewById(R.id.filter1);
            filterView1.setImageBitmap(ThumbnailBitmap);
            filterView1.setColorFilter(GrayScaleFilter);

            //thumbnail 2
            ImageView filterView2 = findViewById(R.id.filter2);
            filterView2.setImageBitmap(ThumbnailBitmap);
            filterView2.setColorFilter(SepiaFilter);

            //thumbnail 3
            ImageView filterView3 = findViewById(R.id.filter3);
            final Filter bluemess = FilterPack.getBlueMessFilter(this);
            filterView3.setImageBitmap(Bitmap.createScaledBitmap(bluemess.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

            //thumbnail 4
            ImageView filterView4 = findViewById(R.id.filter4);
            final Filter april = FilterPack.getAprilFilter(this);
            filterView4.setImageBitmap(Bitmap.createScaledBitmap(april.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

        /*
            GENERATE LARGE PREVIEW
        */
            final Bitmap bluemessLarge = bluemess.processFilter(originalBmp.copy(originalBmp.getConfig(), true));
            final Bitmap aprilLarge = april.processFilter(originalBmp.copy(originalBmp.getConfig(), true));


            //prevent crash when no filter is selected
            FilterBitmap = originalBmp;

            //onclick events
            filterView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Bitmap bitmap = Bitmap.createBitmap(
                            originalBmp.getWidth(),
                            originalBmp.getHeight(),
                            originalBmp.getConfig());
                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setColorFilter(GrayScaleFilter);
                    canvas.drawBitmap(originalBmp, 0, 0, paint);
                    filterLarge.setImageBitmap(bitmap);

                    FilterBitmap = bitmap;
                }
            });
            filterView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    final Bitmap bitmap = Bitmap.createBitmap(
                            originalBmp.getWidth(),
                            originalBmp.getHeight(),
                            originalBmp.getConfig());
                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setColorFilter(SepiaFilter);
                    canvas.drawBitmap(originalBmp, 0, 0, paint);
                    filterLarge.setImageBitmap(bitmap);

                    FilterBitmap = bitmap;
                }
            });
            filterView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    filterLarge.setImageBitmap(bluemessLarge);
                    FilterBitmap = bluemessLarge;
                }
            });
            filterView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterLarge.setImageBitmap(aprilLarge);
                    FilterBitmap = aprilLarge;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create Bitmap.", Toast.LENGTH_SHORT).show();
        }
    }

    static {
        System.loadLibrary("NativeImageProcessor");
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
                selectCopies();
                break;
            case R.id.undo:
                filterLarge.setImageBitmap(originalBmp);
                currentFilter = 0;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //next step
    private void selectCopies() {

        ((Variables) this.getApplication()).setFilterImage(FilterBitmap);

        Intent copySelector = new Intent(this, CopySelector.class);
        startActivity(copySelector);
    }
}
