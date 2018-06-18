package ee.viimsifotostuudio.apic;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Variables extends Application {

    //price
    public double getPrice() {
        return 1.0;
    }

    //original uri
    private Uri originalImageUri;
    public void setOriginalImageUri(Uri variable) {
        this.croppedImageUri = variable;
    }
    public Uri getOriginalImageUri() {
        return croppedImageUri;
    }

    //cropped uri
    private Uri croppedImageUri;
    public void setCroppedImageUri(Uri variable) {
        this.croppedImageUri = variable;
    }
    public Uri getCroppedImageUri() {
        return croppedImageUri;
    }

    //filter bitmap array
    private List<Bitmap> FilterImageArray = new ArrayList<Bitmap>();
    public void addFilterImageArray(Bitmap variable) {
        this.FilterImageArray.add(variable);
    }
    public Bitmap getFilterImageArray(int index) {
        return FilterImageArray.get(index);
    }
    public int getFilterImageArrayLength() {
        return FilterImageArray.size();
    }
    public void removeFilterImageArray(int index) {
        FilterImageArray.remove(index);
    }
    public Bitmap getFilterImageArrayLastItem() {
        return FilterImageArray.get(getFilterImageArrayLength() - 1);
    }
    public void setFilterImageArrayLastItem(Bitmap variable) {
        this.FilterImageArray.set((getFilterImageArrayLength() - 1), variable);
    }
    public void removeFilterImageArrayLastItem() {
        FilterImageArray.remove(getFilterImageArrayLength() - 1);
    }

    //quantity array
    private List<Integer> FilterImageQuantity = new ArrayList<Integer>();
    public void addFilterImageQuantity(int variable) {
        this.FilterImageQuantity.add(variable);
    }
    public void removeOneFilterImageQuantity(int index) {
        if (FilterImageQuantity.get(index) > 1) {
            this.FilterImageQuantity.set(index, FilterImageQuantity.get(index) - 1);
        }
    }
    public void addOneFilterImageQuantity(int index) {
        this.FilterImageQuantity.set(index, FilterImageQuantity.get(index) + 1);
    }
    public int getFilterImageQuantity(int index) {
        return FilterImageQuantity.get(index);
    }
    public void removeFilterImageQuantity(int index) {
        FilterImageQuantity.remove(index);
    }
    public int getFilterImageQuantityLength() {
        return FilterImageQuantity.size();
    }
    public Integer getFilterImageQuantityLastItem() {
        return FilterImageQuantity.get(getFilterImageQuantityLength() - 1);
    }
    public void setFilterImageQuantityLastItem(int variable) {
        this.FilterImageQuantity.set((getFilterImageQuantityLength() - 1), variable);
    }
    public void removeFilterImageQuantityLastItem() {
        FilterImageQuantity.remove(getFilterImageQuantityLength() - 1);
    }
    public int getTotalCopies() {
        int variable = 0;
        for (int i=0; i<getFilterImageQuantityLength(); i++) {
            variable += getFilterImageQuantity(i);
        }
        return variable;
    }

    //for checking if image gets saved twice
    private boolean OverwriteLastPhoto;
    public void setOverwriteLastPhoto(boolean variable) {
        this.OverwriteLastPhoto = variable;
    }
    public boolean getOverwriteLastPhoto() {
        return OverwriteLastPhoto;
    }

    //copy number
//    private int numberOfCopies;
//    public void setNumberOfCopies(int variable) {
//        this.numberOfCopies = variable;
//    }
//    public int getNumberOfCopies() {
//        return numberOfCopies;
//    }

    //first name
    private String firstName;
    public void setFirstName(String variable) {
        this.firstName = variable;
    }
    public String getFirstName() {
        return firstName;
    }

    //last name
    private String lastName;
    public void setLastName(String variable) {
        this.lastName = variable;
    }
    public String getLastName() {
        return lastName;
    }

    //phone number
    private String phone;
    public void setPhone(String variable) {
        this.phone = variable;
    }
    public String getPhone() {
        return phone;
    }

    //email address
    private String eMail;
    public void setEMail(String variable) {
        this.eMail = variable;
    }
    public String getEMail() {
        return eMail;
    }

    //physical address
    private String address;
    public void setAddress(String variable) {
        this.address = variable;
    }
    public String getAddress() {
        return address;
    }

    //phsyical address
    private String paymentToken;
    public void setPaymentToken(String token)
    {
        paymentToken = token;
    }
    public String getPaymentToken()
    {
        return paymentToken;
    }
}