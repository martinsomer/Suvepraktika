package ee.viimsifotostuudio.apic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ReviewImages extends AppCompatActivity {

    LinearLayout parent_layout;
    double photoPrice;
    BigDecimal totalSum;
    int numberOfCopies;
    String totalSumWithText;
    TextView totalSumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_images);

        //toolbar
        Toolbar copiesToolbar = findViewById(R.id.copyToolbar);
        setSupportActionBar(copiesToolbar);
        copiesToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        copiesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Log.d("IMAGES: ", String.valueOf(((Variables) this.getApplication()).getFilterImageArrayLength()));
        Log.d("IMAGES: ", String.valueOf(((Variables) this.getApplication()).getFilterImageQuantityLength()));

        setTitle("Cart");

        ((Variables) this.getApplication()).setOverwriteLastPhoto(true);

        parent_layout = findViewById(R.id.parent_layout);

        numberOfCopies = ((Variables) this.getApplication()).getTotalCopies();
        photoPrice = ((Variables) this.getApplication()).getPrice();

        totalSumView = findViewById(R.id.totalSumView);
        totalSum = new BigDecimal(photoPrice * numberOfCopies).setScale(1, RoundingMode.HALF_UP);
        totalSumWithText = "Total price: " + String.valueOf(totalSum) + "€";
        totalSumView.setText(totalSumWithText);

        LinearLayout addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addNew();
            }
        });

        loadImages();
    }

    private void loadImages() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < ((Variables) this.getApplication()).getFilterImageArrayLength(); i++) {
            assert inflater != null;
            View to_add = inflater.inflate(R.layout.cart_item, parent_layout, false);

            TextView text = to_add.findViewById(R.id.itemID);
            text.setText(String.valueOf(i+1));

            ImageView thumbnail = to_add.findViewById(R.id.itemThumbnail);
            thumbnail.setImageBitmap(((Variables) this.getApplication()).getFilterImageArray(i));

            TextView quantity = to_add.findViewById(R.id.quantity);
            quantity.setText(String.valueOf(((Variables) this.getApplication()).getFilterImageQuantity(i)));

            parent_layout.addView(to_add);
        }
    }

    public void deleteItem(View v) {
        // row is your row, the parent of the clicked button
        View row = (View) v.getParent();
        // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
        ViewGroup container = ((ViewGroup)row.getParent());
        int index = container.indexOfChild(row);

        numberOfCopies -= ((Variables) this.getApplication()).getFilterImageQuantity(index);

        // delete the row and invalidate your view so it gets redrawn
        ((Variables) this.getApplication()).removeFilterImageArray(index);
        ((Variables) this.getApplication()).removeFilterImageQuantity(index);

        container.removeView(row);

        for (int i = 0; i < ((Variables) this.getApplication()).getFilterImageQuantityLength(); i++) {
            TextView id = container.getChildAt(i).findViewById(R.id.itemID);
            id.setText(String.valueOf(i+1));
        }

        updateTotalPrice();
    }

    public void subtractImage(View v) {
        // row is your row, the parent of the clicked button
        View row = (View) v.getParent().getParent().getParent();
        // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
        ViewGroup container = ((ViewGroup)row.getParent());
        int index = container.indexOfChild(row);

        ((Variables) this.getApplication()).removeOneFilterImageQuantity(index);

        TextView quantity = row.findViewById(R.id.quantity);
        quantity.setText(String.valueOf(((Variables) this.getApplication()).getFilterImageQuantity(index)));

        if (numberOfCopies > 1) {
            numberOfCopies -= 1;
        }
        updateTotalPrice();
    }

    public void addImage(View v) {
        // row is your row, the parent of the clicked button
        View row = (View) v.getParent().getParent().getParent();
        // container contains all the rows, you could keep a variable somewhere else to the container which you can refer to here
        ViewGroup container = ((ViewGroup)row.getParent());
        int index = container.indexOfChild(row);

        ((Variables) this.getApplication()).addOneFilterImageQuantity(index);

        TextView quantity = row.findViewById(R.id.quantity);
        quantity.setText(String.valueOf(((Variables) this.getApplication()).getFilterImageQuantity(index)));


        numberOfCopies += 1;
        updateTotalPrice();
    }

    public void addNew() {
        ((Variables) this.getApplication()).setOverwriteLastPhoto(false);
        Intent gallery = new Intent(this, AddNewPhoto.class);
        startActivity(gallery);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_misc, menu);
        return true;
    }

    public void updateTotalPrice() {
        totalSum = new BigDecimal(photoPrice * numberOfCopies).setScale(1, RoundingMode.HALF_UP);
        totalSumWithText = "Total price: " + String.valueOf(totalSum) + "€";
        totalSumView.setText(totalSumWithText);
    }

    //options menu onclick
    @Override
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
        if (totalSum.compareTo(new BigDecimal("4")) > 0) {
            Intent userInfo = new Intent(this, UserInfo.class);
            startActivity(userInfo);
        } else {
            Toast.makeText(this, "Please select at least 5€ worth of images to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (((Variables) this.getApplication()).getFilterImageQuantityLength() < 1) {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        } else {
            finish();
        }
    }
}
