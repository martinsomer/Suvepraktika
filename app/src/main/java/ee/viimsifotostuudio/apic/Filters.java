package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
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
    int currentFilter = 1;

    Bitmap adeleLarge;
    Bitmap haanLarge;
    Bitmap aprilLarge;
    Bitmap bluemessLarge;

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
            final int thumbnailSize = Math.round(width/5);

        //thumbnail 1
            ImageView filterView1 = findViewById(R.id.filter1);
            final Filter adele = FilterPack.getAdeleFilter(this);
            filterView1.setImageBitmap(Bitmap.createScaledBitmap(adele.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

        //thumbnail 2
            ImageView filterView2 = findViewById(R.id.filter2);
            final Filter haan = FilterPack.getHaanFilter(this);
            filterView2.setImageBitmap(Bitmap.createScaledBitmap(haan.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

        //thumbnail 3
            ImageView filterView3 = findViewById(R.id.filter3);
            final Filter april = FilterPack.getAprilFilter(this);
            filterView3.setImageBitmap(Bitmap.createScaledBitmap(april.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

        //thumbnail 4
            ImageView filterView4 = findViewById(R.id.filter4);
            final Filter bluemess = FilterPack.getBlueMessFilter(this);
            filterView4.setImageBitmap(Bitmap.createScaledBitmap(bluemess.processFilter(croppedBmp.copy(croppedBmp.getConfig(), true)), thumbnailSize, thumbnailSize, false));

        //generate bitmaps for preview
            adeleLarge = adele.processFilter(originalBmp.copy(originalBmp.getConfig(), true));
            haanLarge = haan.processFilter(originalBmp.copy(originalBmp.getConfig(), true));
            aprilLarge = april.processFilter(originalBmp.copy(originalBmp.getConfig(), true));
            bluemessLarge = bluemess.processFilter(originalBmp.copy(originalBmp.getConfig(), true));

            //onclick events
            filterView1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setImageBitmap(adeleLarge);
                    currentFilter = 1;
                }
            });
            filterView2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setImageBitmap(haanLarge);
                    currentFilter = 2;
                }
            });
            filterView3.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setImageBitmap(aprilLarge);
                    currentFilter = 3;
                }
            });
            filterView4.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    filterLarge.setImageBitmap(bluemessLarge);
                    currentFilter = 4;
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //next step
    private void selectCopies() {
        if (currentFilter == 1) {
            ((Variables) this.getApplication()).setFilterImage(adeleLarge);
        } else if (currentFilter == 2) {
            ((Variables) this.getApplication()).setFilterImage(haanLarge);
        } else if (currentFilter == 3) {
            ((Variables) this.getApplication()).setFilterImage(aprilLarge);
        } else if (currentFilter == 4) {
            ((Variables) this.getApplication()).setFilterImage(bluemessLarge);
        }

        Intent copySelector = new Intent(this, CopySelector.class);
        startActivity(copySelector);
    }
}
