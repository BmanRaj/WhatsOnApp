package xyz.whatson.android.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "events")
public class Event implements Parcelable {
    // Core fields

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "key")
    private String key;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "eventStartTime")
    private Date eventStartTime;

    @ColumnInfo(name = "eventEndTime")
    private Date eventEndTime;

    @ColumnInfo(name = "eventDate")
    private Date eventDate;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "priceInCents")
    private int priceInCents;

    @ColumnInfo(name = "eventLocationText")
    private String eventLocationText;

    @ColumnInfo(name = "imageURL")
    private String imageURL;

    @ColumnInfo(name = "category")
    private String category;
//    private Pair<Float, Float> eventLocationPin;

    @ColumnInfo(name = "registerURL")
    private String registerURL;

    @ColumnInfo(name = "host")
    private String host;

    @ColumnInfo(name = "owner")
    private String owner;

    @ColumnInfo(name = "lastModified")
    private Date lastModified;
//    private Bitmap image;
//    private User eventHost;

    public Event(){}

    public Event(String title, String description, String host,  Date eventDate, Date eventStartTime, Date eventEndTime, String category, String imageURL, String owner) {
        this.title = title;
        this.description = description;
        this.host = host;
        this.eventDate = eventDate;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.category = category;
        this.priceInCents = 0;
        this.eventLocationText = "";
        this.imageURL = imageURL;
//        this.eventLocationPin = null;
        this.registerURL = null;
        this.owner = owner;
//        this.image = null;
//        this.eventHost = creator;
        this.lastModified = new Date();
    }

    public Event(String title, String description, String host,  Date eventDate, Date eventStartTime, Date eventEndTime, String category, String imageURL, String owner, Date lastModified)
    {
        this(title, description, host, eventDate, eventStartTime, eventEndTime, category, imageURL, owner);
        this.lastModified = lastModified;
    }

    /*
        The serialisation methods required to implement the Parcelable interface.

     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(title);
        dest.writeSerializable(eventDate);
        dest.writeSerializable(eventStartTime);
        dest.writeSerializable(eventEndTime);
        dest.writeString(description);
        dest.writeString(host);
//        dest.writeString(imageURL);
        dest.writeString(category);
        dest.writeInt(priceInCents);
        dest.writeString(eventLocationText);
//        dest.writeSerializable(eventLocationPin);
        dest.writeString(registerURL);
        dest.writeString(owner);
//        dest.writeSerializable(image);
//        dest.writeSerializable(eventHost);
        dest.writeSerializable(lastModified);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    // Unpack object
    public Event(Parcel in) {
        key = in.readString();
        title = in.readString();
        eventDate = (Date) in.readSerializable();
        eventStartTime = (Date) in.readSerializable();
        eventEndTime = (Date) in.readSerializable();
        description = in.readString();
        host = in.readString();
        category = in.readString();
        priceInCents = in.readInt();
        eventLocationText = in.readString();
        registerURL = in.readString();
//        imageURL = in.readString();
        owner = in.readString();
        lastModified = (Date) in.readSerializable();
    }


    /*
        Getters and Setters


     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.lastModified = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModified = new Date();
    }

    public int getPriceInCents() {  return priceInCents; }

    public void setPriceInCents(int p) {
        this.priceInCents = p;
        this.lastModified = new Date();
    }

    public String getEventLocationText() {
        return eventLocationText;
    }

    public void setEventLocationText(String eventLocationText) {
        this.eventLocationText = eventLocationText;
        this.lastModified = new Date();
    }

    public String getRegisterURL() {
        return registerURL;
    }

    public void setRegisterURL(String registerURL) {
        this.registerURL = registerURL;
        this.lastModified = lastModified;
    }

    public Date getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Date eventStartTime) {
        this.eventStartTime = eventStartTime;
        this.lastModified = lastModified;
    }

    public Date getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Date eventEndTime) {
        this.eventEndTime = eventEndTime;
        this.lastModified = lastModified;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        this.lastModified = lastModified;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
        this.lastModified = new Date();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.lastModified = new Date();
    }

    //    public Bitmap getImage() {
//        return image;
//    }
//
//    public void setImage(Bitmap image) {
//        this.image = image;
//    }
//
//    public User getEventHost() {
//        return eventHost;
//    }
//
//    public void setEventHost(User eventHost) {
//        this.eventHost = eventHost;
//    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        this.lastModified = new Date();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
        this.lastModified = new Date();
    }

    public Date getLastModified(){
        return lastModified;
    }

    public void setLastModified(Date date) {
        this.lastModified = lastModified;
    }

}