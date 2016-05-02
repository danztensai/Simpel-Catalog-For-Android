package apps.modisku.com.modisku.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Danz on 9/8/2015.
 */
public class Product implements Parcelable {
    public int id;
    public String title;
    public String imageURL;
    public int quantity;
    public String description;
    public boolean selected;
    public Date lastUpdate;
    int expiredDay;
    double price;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                ", selected=" + selected +
                ", lastUpdate=" + lastUpdate +
                ", expiredDay=" + expiredDay +
                ", price=" + price +
                '}';
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product(int id, String title, String imageURL, int quantity, String description, boolean selected, Date lastUpdate, int expiredDay, double price) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.quantity = quantity;
        this.description = description;
        this.selected = selected;
        this.lastUpdate = lastUpdate;
        this.expiredDay = expiredDay;
        this.price = price;
    }

    public Product(int id, String title, String imageURL, int quantity, String description, boolean selected, Date lastUpdate, int expiredDay) {
        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.quantity = quantity;
        this.description = description;
        this.selected = selected;
        this.lastUpdate = lastUpdate;
        this.expiredDay = expiredDay;
    }

    public int getExpiredDay() {
        return expiredDay;
    }

    public void setExpiredDay(int expiredDay) {
        this.expiredDay = expiredDay;
    }

    public Product(int id, String title, String imageURL, Date lastUpdate,int expiredDay) {

        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.lastUpdate = lastUpdate;
        this.expiredDay = expiredDay;

    }

    public boolean isSelected() {

        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static Creator<Product> getCREATOR() {
        return CREATOR;
    }

    public Product() {
    }

    public Product(int id, String title, String imageURL, int quantity, String description) {

        this.id = id;
        this.title = title;
        this.imageURL = imageURL;
        this.quantity = quantity;
        this.description = description;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (id != other.id)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    private Product(Parcel in) {
        super();
        this.id = in.readInt();
        this.title = in.readString();
        this.imageURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getId());
        parcel.writeString(getTitle());
        parcel.writeString(getImageURL());
    }
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}