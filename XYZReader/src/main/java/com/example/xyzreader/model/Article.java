package com.example.xyzreader.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.xyzreader.data.ArticleLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by radsen on 6/15/17.
 */

public class Article implements Parcelable {

    private static final String TAG = Article.class.getSimpleName();

    public static final String KEY = Article.class.getName() + ".key";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private long id;
    private String title;
    private String author;
    private String body;
    private String photoUrl;
    private String publishedDate;
    private int position;

    public Article(Parcel in) {
        id = in.readLong();
        title = in.readString();
        author = in.readString();
        body = in.readString();
        photoUrl = in.readString();
        publishedDate = in.readString();
        position = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public Article() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public Spanned getByLine(){

        Date publishedDate = parsePublishedDate();
        Spanned value;
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            value = Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + author);
        } else {
            value = Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + author);
        }

        return value;
    }

    private Date parsePublishedDate() {
        try {
            return dateFormat.parse(publishedDate);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        } catch (NullPointerException ex){
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(body);
        parcel.writeString(photoUrl);
        parcel.writeString(publishedDate);
        parcel.writeInt(position);
    }

    public static Article create(Cursor cursor){
        Article article = new Article();
        article.setId(cursor.getLong(ArticleLoader.Query._ID));
        article.setTitle(cursor.getString(ArticleLoader.Query.TITLE));
        article.setAuthor(cursor.getString(ArticleLoader.Query.AUTHOR));
        article.setPhotoUrl(cursor.getString(ArticleLoader.Query.PHOTO_URL));
        article.setPublishedDate(cursor.getString(ArticleLoader.Query.PUBLISHED_DATE));
        return article;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
