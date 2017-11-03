package com.fineapp.jsonparse;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView title = (TextView) findViewById(R.id.title);
        TextView year = (TextView) findViewById(R.id.year);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView genre = (TextView) findViewById(R.id.genre);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Bundle getter = getIntent().getExtras();
        String titleText = getter.getString("title");
        String ratingText = getter.getString("rating");
        String yearText = getter.getString("year");
        String genreText = getter.getString("genre");

        title.setText(titleText);
        year.setText("was made in: " + yearText);
        rating.setText("Rating is: " + ratingText);
        genre.setText("Genre is: " + jsonParser(genreText));

        imageView.setImageBitmap(getImage(titleText));
    }

    private Bitmap getImage(String titleText) {
        SQLite sqLite = new SQLite(this);
        SQLiteDatabase database = sqLite.getReadableDatabase();
        Cursor cursor = database.query(SQLite.MOVIES, null, null, null, null, null, null);
        Bitmap img = null;

        if (cursor.moveToFirst()) {
            int titleId = cursor.getColumnIndex(SQLite.KEY_TITLE);
            int imageId = cursor.getColumnIndex(SQLite.KEY_IMAGE);
            do {
                String title = cursor.getString(titleId);

                if(title.equals(titleText)){
                    byte[] image = cursor.getBlob(imageId);
                    if(image != null) {
                        img = BitmapFactory.decodeByteArray(image, 0, image.length);
                    }
                }
            } while (cursor.moveToNext());
        }
        return img;
    }

    private String jsonParser(String text){
        String resoult = "";
        try {
            JSONArray array = new JSONArray(text);

            for (int i = 0; i < array.length(); i++){
                resoult += array.get(i);
                if (i != array.length() - 1){
                    resoult += ", ";
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resoult;
    }
}
