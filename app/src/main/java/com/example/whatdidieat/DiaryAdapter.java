package com.example.whatdidieat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    static ArrayList<DiaryItem> items = new ArrayList<DiaryItem>();
    static private Context context;
    static private Activity activity;
    static private ImageConverter converter = new ImageConverter();

    public DiaryAdapter(Context context, Activity activity) {
        setHasStableIds(true);
        this.context = context;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Intent intent;
        Intent getIntent;
        RecyclerView diary;
        ImageView image;
        TextView date;
        TextView title;
        TextView calories;
        String diaryID;
        String userID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.thumbnail);
            diary = itemView.findViewById(R.id.diary);
            date = itemView.findViewById(R.id.date);
            title = itemView.findViewById(R.id.title);
            calories = itemView.findViewById(R.id.calories);
            title.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    intent = new Intent(context.getApplicationContext(), Edit.class);
                    getIntent = activity.getIntent();
                    intent.putExtra("userName", getIntent.getStringExtra("userName"));
                    intent.putExtra("userID", getIntent.getStringExtra("userID"));
                    intent.putExtra("diaryID", items.get(position).diaryID);
                    intent.putExtra("foodName", items.get(position).foodName);
                    intent.putExtra("calories", Integer.toString(items.get(position).calories));
                    intent.putExtra("title", items.get(position).title);
                    intent.putExtra("text", items.get(position).text);
                    if (items.get(position).image.data.length == 0) {
                        intent.putExtra("image", "");
                    } else {
                        intent.putExtra("image", converter.byteArrayToString(items.get(position).image.data));
                    }
                    context.startActivity(intent);
//                    String temp = new String(items.get(position).image.data);
//                    Log.d("tag1", converter.byteArrayToString(items.get(position).image.data));
                }
            });
        }

        public void setItem(DiaryItem item) {
            diaryID = item.diaryID;
            userID = item.userID;
            String tempDate = item.date.substring(2, 4) + ".";
            if (item.date.substring(5, 7).charAt(0) == '0') {
                tempDate += item.date.substring(6, 7) + ".";
            } else {
                tempDate += item.date.substring(5, 7) + ".";
            }
            if (item.date.substring(8, 10).charAt(0) == '0') {
                tempDate += item.date.substring(9, 10);
            } else {
                tempDate += item.date.substring(8, 10);
            }
            date.setText(tempDate);
            title.setText(item.title);
            calories.setText(Integer.toString(item.calories) + "kcal");
//            String temp = new String(item.image.data);
//            Log.d("tag1", item.image.data.getClass().getSimpleName());
            if (item.image.data.length == 0) {
                image.setImageResource(R.drawable.button_upload_image);
            } else {
                image.setImageBitmap(converter.stringToBitmap(converter.byteArrayToString(item.image.data)));
            }
        }
    }

    public void updateList(ArrayList<DiaryItem> list) {
        items.clear();
        items.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        DiaryItem item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(DiaryItem item) {
        items.add(item);
    }
}
