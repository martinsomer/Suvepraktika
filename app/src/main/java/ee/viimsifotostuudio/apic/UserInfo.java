package ee.viimsifotostuudio.apic;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Objects;

public class UserInfo extends AppCompatActivity {

    EditText FirstName;
    EditText LastName;
    EditText PhoneNumber;
    EditText Mail;
    RadioButton UserAddress1;
    RadioButton UserAddress2;
    EditText UserRealAddress;
    CheckBox CheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setTitle("Your Information");
        FirstName = findViewById(R.id.firstName);
        LastName = findViewById(R.id.lastName);
        PhoneNumber = findViewById(R.id.phoneNumber);
        Mail = findViewById(R.id.mail);
        UserAddress1 = findViewById(R.id.address1);
        UserAddress2 = findViewById(R.id.address2);
        UserRealAddress = findViewById(R.id.UserAddress);
        CheckBox = findViewById(R.id.checkBox);

        //toolbar
        Toolbar copiesToolbar = findViewById(R.id.copyToolbar);
        setSupportActionBar(copiesToolbar);
        copiesToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if (BuildConfig.DEBUG) {
            Variables vars = (Variables) this.getApplication();
            FirstName.setText(vars.getFirstName());
            LastName.setText(vars.getLastName());
            PhoneNumber.setText(vars.getPhone());
            Mail.setText(vars.getEMail());
            UserRealAddress.setText(vars.getAddress());
        }
    }

    //next step
    private void payment() {
        if (CheckBox.isChecked()) {
            if (checkUserInfo()) {
                //Intent payment = new Intent(this, Payment.class);
                //startActivity(payment);
                Intent uploader = new Intent(this, Uploader.class);
                startActivity(uploader);
            }
        } else {
            Toast.makeText(this, "Please Accept Terms and Conditions to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkUserInfo() {
        if (!FirstName.getText().toString().equals("") &&
                !LastName.getText().toString().equals("") &&
                !PhoneNumber.getText().toString().equals("") &&
                !Mail.getText().toString().equals("") &&
                        (UserAddress1.isChecked() ||
                        (UserAddress2.isChecked() && !UserRealAddress.getText().toString().equals("")))
                ) {

            //save user info to variables class
            ((Variables) this.getApplication()).setFirstName(FirstName.getText().toString());
            ((Variables) this.getApplication()).setLastName(LastName.getText().toString());
            ((Variables) this.getApplication()).setPhone(PhoneNumber.getText().toString());
            ((Variables) this.getApplication()).setEMail(Mail.getText().toString());
            if (UserAddress1.isChecked()) {
                ((Variables) this.getApplication()).setAddress("DEFAULT STUDIO ADDRESS");
            } else {
                ((Variables) this.getApplication()).setAddress(UserRealAddress.toString());
            }

            return true;

        }
        Toast.makeText(this, "Please check your information.", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_misc, menu);
        return true;
    }

    //options menu onclick
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.accept:
                payment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
