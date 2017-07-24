package cn.ccxxs.friendcalendar.Model;

import java.io.Serializable;

/**
 * Created by ccxxs on 2017/5/26.
 */

public class TheActivity implements Serializable {
    //活动的id
    private int id;
    private String username;
    private String title;
    private String starttime;
    private String endtime;
    private String desc;
    private String location;
    private String conversationid;
    private String remindtime;
    private String activityimg;
    private String lat;
    private String lng;
    private String isprivate;



    public TheActivity(int id, String username, String title, String starttime, String endtime, String desc, String location, String conversationid, String remindtime, String activityimg, String lat, String lng, String isprivate) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.starttime = starttime;
        this.endtime = endtime;
        this.desc = desc;
        this.location = location;
        this.conversationid = conversationid;
        this.remindtime = remindtime;
        this.activityimg = activityimg;
        this.lat = lat;
        this.lng = lng;
        this.isprivate = isprivate;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConversationid() {
        return conversationid;
    }

    public void setConversationid(String conversationid) {
        this.conversationid = conversationid;
    }
    public String getRemindtime() {
        return remindtime;
    }

    public void setRemindtime(String remindtime) {
        this.remindtime = remindtime;
    }
    public String getActivityimg() {
        return activityimg;
    }

    public void setActivityimg(String activityimg) {
        this.activityimg = activityimg;
    }
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
    public String getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(String isprivate) {
        this.isprivate = isprivate;
    }

}

