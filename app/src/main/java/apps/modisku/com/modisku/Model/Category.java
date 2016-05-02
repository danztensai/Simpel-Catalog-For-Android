package apps.modisku.com.modisku.Model;

import java.util.Date;

/**
 * Created by Danz on 9/8/2015.
 */
public class Category {
    public int id;
    public String name;
    public String imageURL;
    public Date lastUpdate;
    public int expiredDay;

    public Category(int id, String name, String imageURL, Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.lastUpdate = lastUpdate;
    }

    public Category(int id, String name, String imageURL, Date lastUpdate, int expiredDay) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.lastUpdate = lastUpdate;
        this.expiredDay = expiredDay;
    }

    public int getExpiredDay() {
        return expiredDay;
    }

    public void setExpiredDay(int expiredDay) {
        this.expiredDay = expiredDay;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category()
    {

    }
    public Category (int id,String name,String imageURL)
    {

        this.id=id;
        this.name=name;
        this.imageURL =imageURL;

    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
