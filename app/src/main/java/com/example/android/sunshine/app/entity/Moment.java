package com.example.android.sunshine.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mmahfouz on 1/27/2016.
 */
public class Moment implements Parcelable, Serializable{



    private int _id;
    private String moment;
    private String day;
    private int icon;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Moment> CREATOR = new Parcelable.Creator<Moment>() {
        @Override
        public Moment createFromParcel(Parcel source) {
            Moment moment = new Moment();
            moment.setMoment(source.readString());
            moment.setDay(source.readString());
            moment.setIcon(source.readInt());
            return moment;
        }

        @Override
        public Moment[] newArray(int size) {
            return new Moment[size];
        }
    };


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMoment());
        dest.writeString(getDay());
        dest.writeInt(getIcon());

    }

}
