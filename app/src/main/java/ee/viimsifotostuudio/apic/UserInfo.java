package ee.viimsifotostuudio.apic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.regex.Pattern;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    //next step
    private void payment() {
        if (CheckBox.isChecked()) {
            if (checkUserInfo()) {

                if (isNetworkAvailable()) {
                    Intent payment = new Intent(this, PaymentWebView.class);
                    startActivity(payment);
                }

//                Intent payment = new Intent(this, Payment.class);
//                startActivity(payment);
            }
        } else {
            Toast.makeText(this, "Please Accept Terms and Conditions to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void openLocation(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.fuji.ee/kontakt/"));
        startActivity(intent);
    }
    public void userAddress1checked(View v) {
        UserAddress1.setChecked(true);
        UserAddress2.setChecked(false);
    }
    public void userAddress2checked(View v) {
        UserAddress1.setChecked(false);
        UserAddress2.setChecked(true);
    }

    private boolean checkUserInfo() {
        if ((!FirstName.getText().toString().equals("") &&   //first name is not empty
                FirstName.getText().toString().matches("[a-zA-Z]+")) &&   //first only contains letters

                (!LastName.getText().toString().equals("") &&   //last name is not empty
                LastName.getText().toString().matches("[a-zA-Z]+")) &&    //last name only contains letters

                (!PhoneNumber.getText().toString().equals("") &&    //phone number is not empty
                !PhoneNumber.getText().toString().equals("+") &&    //phone number is not '+'
                PhoneNumber.getText().toString().matches("[0-9+]{8,}+")) &&  //phone number only containts numbers and '+' and is at least 8 characers

                (!Mail.getText().toString().equals("") &&   //mail is not empty
                checkEmail(Mail.getText().toString())) &&   //mail is valid

                (UserAddress1.isChecked() ||    //studio is checked
                (UserAddress2.isChecked() && !UserRealAddress.getText().toString().equals("")))    //user address in checked and not empty
            ) {

            //save user info to variables class
            ((Variables) this.getApplication()).setFirstName(FirstName.getText().toString());
            ((Variables) this.getApplication()).setLastName(LastName.getText().toString());
            ((Variables) this.getApplication()).setPhone(PhoneNumber.getText().toString());
            ((Variables) this.getApplication()).setEMail(Mail.getText().toString());
            if (UserAddress1.isChecked()) {
                ((Variables) this.getApplication()).setAddress("Stuudio");
            } else {
                ((Variables) this.getApplication()).setAddress(UserRealAddress.toString());
            }

            return true;

        }
        Toast.makeText(this, "Please check your information.", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public void showTermsAndConditions(View v) {
        Intent terms = new Intent(this, TermsAndConditions.class);
        startActivity(terms);
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
