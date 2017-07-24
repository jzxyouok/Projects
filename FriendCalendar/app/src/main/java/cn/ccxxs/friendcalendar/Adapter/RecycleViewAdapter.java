package cn.ccxxs.friendcalendar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ccxxs.friendcalendar.Activity.DetailOfActivity;
import cn.ccxxs.friendcalendar.MainActivity;
import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.R;

/**
 * Created by ccxxs on 2017/5/26.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<TheActivity> activities;
    private Context context;
    public RecycleViewAdapter(List<TheActivity> activities, Context context) {
        this.activities = activities;
        this.context = context;
    }

    //自定义ViewHolder类
    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageView activities_photo;
        TextView activities_title;
        TextView activities_desc;
        ImageView date_icon;
        ImageView location_icon;
        TextView activities_date;
        TextView activities_location;
        public ViewHolder(final View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.card_view);
            activities_photo= (ImageView) itemView.findViewById(R.id.activity_photo);
            activities_title= (TextView) itemView.findViewById(R.id.activity_title);
            activities_desc= (TextView) itemView.findViewById(R.id.activity_desc);
            date_icon = (ImageView)itemView.findViewById(R.id.date_icon);
            location_icon = (ImageView)itemView.findViewById(R.id.location_icon);
            activities_date= (TextView) itemView.findViewById(R.id.activity_date);
            activities_location= (TextView) itemView.findViewById(R.id.activity_location);
            //设置TextView背景为半透明
            activities_title.setBackgroundColor(Color.argb(40, 0, 0, 0));
        }
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.activity_item,viewGroup,false);
        ViewHolder nvh=new ViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder personViewHolder, int i) {
        final int j=i;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        personViewHolder.activities_title.setText(activities.get(i).getTitle());
        personViewHolder.activities_desc.setText(activities.get(i).getDesc());
        personViewHolder.date_icon.setImageResource(R.drawable.clock);
        personViewHolder.location_icon.setImageResource(R.drawable.location);
        String starttime = simpleDateFormat.format(new Date(Long.parseLong(activities.get(i).getStarttime())));
        String endtime = simpleDateFormat.format(new Date(Long.parseLong(activities.get(i).getEndtime())));
        String date = starttime+" - "+endtime;
        personViewHolder.activities_date.setText(date);
        personViewHolder.activities_location.setText(activities.get(i).getLocation());
        //图片加载
        String activityimg = activities.get(i).getActivityimg().trim();
        Glide.with(MainActivity.main).load(activityimg).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).error(R.drawable.defaultimg).into(personViewHolder.activities_photo);
//        Glide.with(MainActivity.main).load(activityimg).error(R.drawable.defaultimg).into(personViewHolder.activities_photo);
        //为btn_share btn_readMore cardView设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,DetailOfActivity.class);
                intent.putExtra("activity",activities.get(j));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

}
