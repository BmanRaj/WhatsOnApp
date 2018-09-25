package xyz.whatson.android.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Parcelable {
    // Core fields
    private String title;
    private Date eventStartTime;
    private Date eventEndTime;
    private Date eventDate;
    private String description;
    private int priceInCents;
    private String eventLocationText;
    private String imageURL;
    private String category;
//    private Pair<Float, Float> eventLocationPin;
    private String registerURL;
    private String host;
    private String owner;
//    private Bitmap image;
//    private User eventHost;



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
        dest.writeString(title);
        dest.writeSerializable(eventDate);
        dest.writeSerializable(eventStartTime);
        dest.writeSerializable(eventEndTime);
        dest.writeString(description);
        dest.writeString(host);
        dest.writeString(imageURL);
        dest.writeString(category);
        //dest.writeInt(priceInCents);
        dest.writeString(eventLocationText);
//        dest.writeSerializable(eventLocationPin);
        dest.writeString(registerURL);
        dest.writeString(owner);
//        dest.writeSerializable(image);
//        dest.writeSerializable(eventHost);
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
        imageURL = in.readString();
        owner = in.readString();

    }




    /*
        Getters and Setters


     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceInCents() {  return priceInCents; }

    public void setPriceInCents(int p) {
        this.priceInCents = p;
    }

    public String getEventLocationText() {
        return eventLocationText;
    }

    public void setEventLocationText(String eventLocationText) {
        this.eventLocationText = eventLocationText;
    }

    public String getRegisterURL() {
        return registerURL;
    }

    public void setRegisterURL(String registerURL) {
        this.registerURL = registerURL;
    }

    public Date getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Date eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Date getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Date eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}