package cn.ccxxs.friendcalendar.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.Activity.AddActivity;
import cn.ccxxs.friendcalendar.Adapter.RecycleViewAdapter;
import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.NetWork.NetUtils;
import cn.ccxxs.friendcalendar.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static cn.ccxxs.friendcalendar.Constants.activityListConstant;

public class ActivityFragment extends Fragment {
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout freshlayout;
    @Bind(R.id.addActivityBtn)
    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecycleViewAdapter recyclerViewAdapter;
    private List<TheActivity> activityList;
    ArrayList<TheActivity> dataFromNet = new ArrayList<>();
    ArrayList<TheActivity> searchResult = new ArrayList<>();

    Handler handler;
    //线程标志
    int refresh = 0;
    public ActivityFragment() {
        // Required empty public constructor
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_activity, container, false);
        ButterKnife.bind(this,view);
        freshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新列表
                getActivityList();
            }
        });
        initActivityData();
        initView(view);
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.main_tb_toolbar);
        toolbar.inflateMenu(R.menu.menu_activity_fragment);
        toolbar.setTitle("我的活动");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.search) {
                    Toast.makeText(getContext() , "this is search" , Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        SearchView searchView = (SearchView)toolbar.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Search(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Search(newText.trim());
                return false;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivityList();
    }

    private void initView(View view) {
        //RecyleView设置
        recyclerView= (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerViewAdapter = new RecycleViewAdapter(activityList,getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
    @OnClick(R.id.addActivityBtn)
    public void addActivity() {
        Intent intent=new Intent(getContext(),AddActivity.class);
        startActivity(intent);
    }

    private void initActivityData() {
        activityList =new ArrayList<>();
//        (int id, String title, long starttime, long endtime, String desc, String location, int photoId)
        activityList.add(new TheActivity(1,"ccxxs",getString(R.string.title),"1496210455641","1496214055641",getString(R.string.desc),getString(R.string.location),"1111","1496214055641","","0","0","1"));
    }

    public void updateView() {
        Collections.reverse(dataFromNet);
        activityList.clear();
        activityList.addAll(dataFromNet);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
        freshlayout.setRefreshing(false);
    }
    public void updateSearch() {
//        Collections.reverse(dataFromNet);
        activityList.clear();
        activityList.addAll(searchResult);
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(0);
//        freshlayout.setRefreshing(false);
    }

    //刷新活动列表
    public void getActivityList() {
        SharedPreferences sp = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        int userid = sp.getInt("userid",0);
        String useridStr = Integer.toString(userid);
        NetUtils.getActivity(NetUtils.getGetActivityUrl(), useridStr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载失败，请冲新打开应用",Toast.LENGTH_LONG);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String respdata = response.body().string().trim();
                JsonArray jsonA = (JsonArray)(new JsonParser().parse(respdata));
                Iterator iterator = jsonA.iterator();
                dataFromNet.clear();
                while (iterator.hasNext()){
                    JsonObject jsonObject = (JsonObject) iterator.next();
                    try {
                        int id = jsonObject.get("id").getAsInt();
                        String username = jsonObject.get("username").getAsString();
                        String title = jsonObject.get("title").getAsString();
                        String starttime = jsonObject.get("starttime").getAsString();
                        String endtime = jsonObject.get("endtime").getAsString();
                        String desc = jsonObject.get("content").getAsString();
                        String location = jsonObject.get("location").getAsString();
                        String conversationid = jsonObject.get("conversationid").getAsString();
                        String remindtime = jsonObject.get("remindtime").getAsString();
                        String activityimg = "";
                        if (jsonObject.get("activityimg")!=null){
                            activityimg = jsonObject.get("activityimg").getAsString();
                        }
                        String lat = "0";
                        String lng = "0";
                        if (jsonObject.get("lat")!=null||jsonObject.get("lng")!=null){
                            lat = jsonObject.get("lat").getAsString();
                            lng = jsonObject.get("lng").getAsString();
                        }
                        dataFromNet.add(new TheActivity(id,username,title,starttime,endtime,desc,location,conversationid,remindtime,activityimg,lat,lng,"1"));
                    } catch (JsonIOException e) {
                        e.printStackTrace();
                    }
                }
                activityListConstant = dataFromNet;
                Log.w("datafromnet", "onResponse: "+dataFromNet.size() );
                handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        });
    }

    public void Search(String keyword) {
        searchResult.clear();
        for (TheActivity activity:dataFromNet){
            if (activity.getTitle().contains(keyword)||activity.getDesc().contains(keyword)||activity.getLocation().contains(keyword)){
                searchResult.add(activity);
            }
        }
        handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateSearch();
            }
        });
    }
}
