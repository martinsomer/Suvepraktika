package ee.viimsifotostuudio.apic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Memories Matter!");

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
                SelectImage();
            }
        } else {
            SelectImage();
        }
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage();
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
            case R.id.location:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fuji.ee/kontakt/"));
                startActivity(intent);
                break;
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

    //next step
    private void SelectImage() {
        ((Variables) this.getApplication()).setOverwriteLastPhoto(false);
        Intent gallery = new Intent(this, GalleryAndCrop.class);
        startActivity(gallery);
    }
}
