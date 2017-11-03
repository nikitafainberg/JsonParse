package com.fineapp.jsonparse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private SQLite sqLite;
    private Intent intent;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this, ListActivity.class);
        sqLite = new SQLite(this);

        SQLiteDatabase database = sqLite.getReadableDatabase();
        Cursor cursor = database.query(SQLite.MOVIES, null, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            startActivity(intent);
        }else {
            new AsyncLoad().execute();
        }
        cursor.close();
        sqLite.close();
    }

    public class AsyncLoad extends AsyncTask<Void, String, String>{

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try {
                URL url = new URL("https://api.androidhive.info/json/movies.json");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }

                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String resoult) {
            super.onPostExecute(resoult);
            try {
                JSONArray array = new JSONArray(resoult);

                count = array.length();
                for (int i = 0; i < array.length(); i++){
                    JSONObject object = (JSONObject) array.get(i);

                    String title = object.getString("title");
                    String image = object.getString("image");
                    String rating = object.getString("rating");
                    int year = object.getInt("releaseYear");
                    String genre = object.getString("genre");

                    saveInSQLite(title,image, rating, year, genre);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void saveInSQLite(String title,String imageKey, String rating, int year, String genre){
            SQLiteDatabase database = sqLite.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(SQLite.KEY_TITLE, title);
            contentValues.put(SQLite.KEY_RATING, rating);
            contentValues.put(SQLite.KEY_YEAR, year);
            contentValues.put(SQLite.KEY_GENRE, genre);

            database.insert(SQLite.MOVIES, null, contentValues);
            sqLite.close();

            new AsyncImage().execute(imageKey, title);
        }
    }

    public class AsyncImage extends AsyncTask<String, Bitmap, Bitmap>{

        private String title;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            title = params[1];

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream stream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(stream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] array = stream.toByteArray();

            findSQLiteLine(array);
        }

        private void findSQLiteLine(byte[] image){
            count--;

            SQLiteDatabase database = sqLite.getReadableDatabase();
            Cursor cursor = database.query(SQLite.MOVIES, null, null, null, null, null, null);
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLite.KEY_IMAGE, image);
            database.update(SQLite.MOVIES, contentValues, SQLite.KEY_TITLE + " = ?", new String[]{title});
            cursor.close();
            sqLite.close();

            if (count == 0){
                startActivity(intent);
            }
        }
    }
}