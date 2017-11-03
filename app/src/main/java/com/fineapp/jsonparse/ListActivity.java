package com.fineapp.jsonparse;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ArrayList<Form> newOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListView list = (ListView) findViewById(R.id.list);
        ArrayList<Form> listForm = getInfo();
        newOrder = new ArrayList<>();

        ArrayList<Integer> years = new ArrayList<>();

        for (Form form : listForm){
            years.add(form.year);
        }

        Collections.sort(years);

        for (int i = years.size() - 1; i >= 0; i--){
            for (Form form : listForm){
                if (form.year == years.get(i)){
                    newOrder.add(form);
                    listForm.remove(form);
                    break;
                }
            }
        }

        ListAdapter adapter = new ListAdapter(this, newOrder);
        list.setAdapter(adapter);

        list.setOnItemClickListener(this);
    }

    private ArrayList<Form> getInfo() {
        ArrayList<Form> forms = new ArrayList<>();
        SQLite sqLite = new SQLite(this);
        SQLiteDatabase database = sqLite.getReadableDatabase();
        Cursor cursor = database.query(SQLite.MOVIES, null, null, null, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                int titleId = cursor.getColumnIndex(SQLite.KEY_TITLE);
                int imageId = cursor.getColumnIndex(SQLite.KEY_IMAGE);
                int ratingId = cursor.getColumnIndex(SQLite.KEY_RATING);
                int yearId = cursor.getColumnIndex(SQLite.KEY_YEAR);
                int genreId = cursor.getColumnIndex(SQLite.KEY_GENRE);
                do {
                    String title = cursor.getString(titleId);
                    byte[] image = cursor.getBlob(imageId);
                    String rating = cursor.getString(ratingId);
                    int year = cursor.getInt(yearId);

                    String genre = cursor.getString(genreId);
                    Form form = new Form(title, image, rating, year, genre);
                    forms.add(form);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        sqLite.close();

        return forms;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Form form = newOrder.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", form.title);
        bundle.putString("year", String.valueOf(form.year));
        bundle.putString("rating", form.rating);
        bundle.putString("genre", form.genre);

        Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}