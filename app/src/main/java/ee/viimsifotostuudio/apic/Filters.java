package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import jp.co.cyberagent.android.gpuimage.*;

public class Filters extends AppCompatActivity {

    Uri croppedImageUri;
    Bitmap originalBmp;
    ImageView filterLarge;
    Bitmap FilterBitmap;

    Bitmap adeleLarge;
    Bitmap sepiaLarge;
    Bitmap bluemessLarge;
    Bitmap aprilLarge;

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
        FilterBitmap = originalBmp;

        try {

            /*
                RESIZE PREVIEW
            */

            originalBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
            FilterBitmap = originalBmp;

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
                GENERATE THUMBNAILS
            */

            //load native image processor
            System.loadLibrary("NativeImageProcessor");

            //thumbnail 1
            GPUImageView filterView1 = findViewById(R.id.filter1);
            filterView1.setImage(originalBmp);
            filterView1.setFilter(new GPUImageGrayscaleFilter());

            //thumbnail 2
            GPUImageView filterView2 = findViewById(R.id.filter2);
            filterView2.setImage(originalBmp);
            filterView2.setFilter(new GPUImageSepiaFilter());

            //thumbnail 3
            ImageView filterView3 = findViewById(R.id.filter3);
            filterView3.setImageBitmap(originalBmp);
            filterView3.setImageBitmap(FilterPack.getBlueMessFilter(this).processFilter(originalBmp.copy(originalBmp.getConfig(), true)));

            //thumbnail 4
            ImageView filterView4 = findViewById(R.id.filter4);
            filterView4.setImageBitmap(originalBmp);
            filterView4.setImageBitmap(FilterPack.getAprilFilter(this).processFilter(originalBmp.copy(originalBmp.getConfig(), true)));


            /*
                GENERATE LARGE PREVIEWS
            */

            final Filter adele = FilterPack.getAdeleFilter(this);
            adeleLarge = adele.processFilter(originalBmp.copy(originalBmp.getConfig(), true));

            GPUImage mGPUImage = new GPUImage(this);
            mGPUImage.setFilter(new GPUImageSepiaFilter());
            sepiaLarge = mGPUImage.getBitmapWithFilterApplied(originalBmp);

            final Filter bluemess = FilterPack.getBlueMessFilter(this);
            bluemessLarge = bluemess.processFilter(originalBmp.copy(originalBmp.getConfig(), true));

            final Filter april = FilterPack.getAprilFilter(this);
            aprilLarge = april.processFilter(originalBmp.copy(originalBmp.getConfig(), true));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create Bitmap.", Toast.LENGTH_SHORT).show();
        }
    }

    public void filter1clicked(View view) {
        filterLarge.setImageBitmap(adeleLarge);
        FilterBitmap = adeleLarge;
    }
    public void filter2clicked(View view) {
        filterLarge.setImageBitmap(sepiaLarge);
        FilterBitmap = sepiaLarge;
    }
    public void filter3clicked(View view) {
        filterLarge.setImageBitmap(bluemessLarge);
        FilterBitmap = bluemessLarge;
    }
    public void filter4clicked(View view) {
        filterLarge.setImageBitmap(aprilLarge);
        FilterBitmap = aprilLarge;
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
                FilterBitmap = originalBmp;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //next step
    private void selectCopies() {
        if (((Variables) this.getApplication()).getOverwriteLastPhoto()) {
            ((Variables) this.getApplication()).setFilterImageArrayLastItem(FilterBitmap);
            ((Variables) this.getApplication()).setFilterImageQuantityLastItem(1);
        } else {
            ((Variables) this.getApplication()).addFilterImageArray(FilterBitmap);
            ((Variables) this.getApplication()).addFilterImageQuantity(1);
        }

        Intent copySelector = new Intent(this, CopySelector.class);
        startActivity(copySelector);
    }
}
