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
    private Date eventStart;
    private Date eventEnd;
    private String description;
    private int priceInCents;
    private String eventLocationText;
//    private Pair<Float, Float> eventLocationPin;
    private String registerURL;
//    private Bitmap image;
//    private User eventHost;



    public Event(String title, Date eventStart, String eventLocationText) {
        this.title = title;
        this.eventStart = eventStart;
        this.eventEnd = eventStart;
        this.description = "";
        this.priceInCents = 0;
        this.eventLocationText = "";
//        this.eventLocationPin = null;
        this.registerURL = null;
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
        dest.writeSerializable(eventStart);
        dest.writeSerializable(eventEnd);
        dest.writeString(description);
        dest.writeInt(priceInCents);
        dest.writeString(eventLocationText);
//        dest.writeSerializable(eventLocationPin);
        dest.writeString(registerURL);
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
        eventStart = (Date) in.readSerializable();
        eventEnd = (Date) in.readSerializable();
        description = in.readString();
        priceInCents = in.readInt();
        eventLocationText = in.readString();
        registerURL = in.readString();

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

    public Date getEventStart() {
        return eventStart;
    }

    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }

    public Date getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
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

}