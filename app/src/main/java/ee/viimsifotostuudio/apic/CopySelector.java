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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

public class CopySelector extends AppCompatActivity {

    ImageView imageView;
    Uri filterImageUri;
    TextView copyNumber;
    int numberOfCopies = 1;
    Bitmap filterBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_selector);

        copyNumber = findViewById(R.id.copyNumber);
        imageView = findViewById(R.id.imageView);
        filterImageUri = ((Variables) this.getApplication()).getFilterImageUri();

        setTitle("Number of Copies");

        //toolbar
        Toolbar copiesToolbar = findViewById(R.id.copyToolbar);
        setSupportActionBar(copiesToolbar);
        copiesToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        imageView.setImageURI(filterImageUri);

        //correct image orientation
        try {
            filterBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(String.valueOf(filterImageUri))));

            //change large preview size based on image orientation
            if (filterBitmap.getWidth() > filterBitmap.getHeight()) {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                imageView.setLayoutParams(params);
            } else {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(params);
            }

            copyNumber.setText(String.valueOf(numberOfCopies));
            Button addImage = findViewById(R.id.addImage);
            addImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    addImage();
                }
            });
            Button subtractImage = findViewById(R.id.subtractImage);
            subtractImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    subtractImage();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create Bitmap.", Toast.LENGTH_SHORT).show();
        }
    }

    private void subtractImage() {
        if (numberOfCopies > 1) {
            numberOfCopies = numberOfCopies - 1;
            copyNumber.setText(String.valueOf(numberOfCopies));
        }
    }

    private void addImage() {
        numberOfCopies = numberOfCopies + 1;
        copyNumber.setText(String.valueOf(numberOfCopies));
    }

    //options menu onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.accept:
                userInfo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //next step
    private void userInfo() {
        ((Variables) this.getApplication()).setNumberOfCopies(numberOfCopies);
        Intent userInfo = new Intent(this, UserInfo.class);
        startActivity(userInfo);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return true;
    }
}
