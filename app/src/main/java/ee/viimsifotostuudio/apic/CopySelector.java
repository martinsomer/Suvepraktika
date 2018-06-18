package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CopySelector extends AppCompatActivity {

    double photoPrice;
    TextView totalSumView;
    BigDecimal totalSum;
    String totalSumWithText;
    ImageView imageView;
    TextView copyNumber;
    int numberOfCopies;
    Bitmap filterBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_selector);

        filterBitmap = ((Variables) this.getApplication()).getFilterImageArrayLastItem();

        copyNumber = findViewById(R.id.copyNumber);
        imageView = findViewById(R.id.imageView);
        totalSumView = findViewById(R.id.totalSumView);

        setTitle("Number of Copies");
        numberOfCopies = ((Variables) this.getApplication()).getFilterImageQuantityLastItem();

        //toolbar
        Toolbar copiesToolbar = findViewById(R.id.copyToolbar);
        setSupportActionBar(copiesToolbar);
        copiesToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        imageView.setImageBitmap(filterBitmap);

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

        photoPrice = ((Variables) this.getApplication()).getPrice();
        totalSum = new BigDecimal(photoPrice * numberOfCopies).setScale(1, RoundingMode.HALF_UP);
        updateTotalPrice();

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
    }

    private void subtractImage() {
        if (numberOfCopies > 1) {
            numberOfCopies -= 1;
            copyNumber.setText(String.valueOf(numberOfCopies));
            totalSum = totalSum.subtract(new BigDecimal(photoPrice).setScale(1, RoundingMode.HALF_UP));
            updateTotalPrice();
        }
    }

    private void addImage() {
        numberOfCopies = numberOfCopies + 1;
        copyNumber.setText(String.valueOf(numberOfCopies));
        totalSum = totalSum.add(new BigDecimal(photoPrice).setScale(1, RoundingMode.HALF_UP));
        updateTotalPrice();
    }

    public void updateTotalPrice() {
        totalSumWithText = "Total price: " + String.valueOf(totalSum) + "€";
        totalSumView.setText(totalSumWithText);
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
        ((Variables) this.getApplication()).setFilterImageQuantityLastItem(numberOfCopies);
        Intent reviewImages = new Intent(this, ReviewImages.class);
        startActivity(reviewImages);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_misc, menu);
        return true;
    }
}
