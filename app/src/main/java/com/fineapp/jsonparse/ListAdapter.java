package com.fineapp.jsonparse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private List<Form> appViews;
    private Context context;

    public ListAdapter(Context context, List<Form> appViews){
        this.context = context;
        this.appViews = appViews;
    }

    @Override
    public int getCount() {
        return appViews.size();
    }

    @Override
    public Object getItem(int position) {
        return appViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item, parent, false);
        }
        Form form = getForm(position);

        TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView txtGenre = (TextView) convertView.findViewById(R.id.txtGenre);
        TextView txtRating = (TextView) convertView.findViewById(R.id.txtRating);
        ImageView img = (ImageView) convertView.findViewById(R.id.img);

        txtTitle.setText(form.title + " (" + form.year + ")");
        txtRating.setText(form.rating);
        txtGenre.setText(jsonParser(form.genre));

        Bitmap bitmap = BitmapFactory.decodeByteArray(form.imageKey, 0, form.imageKey.length);
        img.setImageBitmap(bitmap);
        return convertView;
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

    public Form getForm(int position){
        return (Form)getItem(position);
    }
}
