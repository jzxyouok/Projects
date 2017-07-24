package cn.ccxxs.friendcalendar.Fragment;


import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.github.lzyzsd.randomcolor.RandomColor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import cn.ccxxs.friendcalendar.Model.TheActivity;
import cn.ccxxs.friendcalendar.R;

import static android.R.attr.id;
import static cn.ccxxs.friendcalendar.Constants.activityListConstant;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    TextView textView;
    Handler mhandler = new Handler();
    private WeekView mWeekView;
    RandomColor randomColor = new RandomColor();
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    public CalendarFragment() {
        // Required empty public constructor
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this,view);
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_calendar);
        toolbar.setTitle("我的日历");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setupDateTimeInterpreter(id == R.id.action_week_view);
                int id = item.getItemId();
                switch (id){
                    case R.id.action_today:
                        mWeekView.goToToday();
                        return true;
                    case R.id.action_day_view:
                        if (mWeekViewType != TYPE_DAY_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_DAY_VIEW;
                            mWeekView.setNumberOfVisibleDays(1);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        }
                        return true;
                    case R.id.action_three_day_view:
                        if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_THREE_DAY_VIEW;
                            mWeekView.setNumberOfVisibleDays(3);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        }
                        return true;
                    case R.id.action_week_view:
                        if (mWeekViewType != TYPE_WEEK_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_WEEK_VIEW;
                            mWeekView.setNumberOfVisibleDays(7);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                        }
                        return true;
                }
                return true;
            }
        });
        setHasOptionsMenu(true);
        mWeekView = (WeekView) view.findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
           @Override
           public void onEventClick(WeekViewEvent event, RectF eventRect) {

           }
        });
        mWeekView.setWeekViewLoader(new WeekViewLoader() {
            @Override
            public double toWeekViewPeriodIndex(Calendar instance) {
                return 0;
            }

            @Override
            public List<? extends WeekViewEvent> onLoad(int periodIndex) {
                return null;
            }
        });
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
                if (activityListConstant != null){
                    Log.w("onMonthChange", "onMonthChange: "+activityListConstant.size());
                    for (TheActivity activity: activityListConstant){
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(Long.parseLong(activity.getStarttime()));
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(Long.parseLong(activity.getEndtime()));
                        if ((newMonth - startTime.get(Calendar.MONTH) == 1)){
                            WeekViewEvent event = new WeekViewEvent(1, activity.getTitle(), startTime, endTime);
//                            event.setColor(getResources().getColor(R.color.colorPrimary));
                            event.setColor(randomColor.randomColor());
                            events.add(event);
                        }
                    }
                }
                return events;
            }
        });
//        mWeekView.setEventLongPressListener(mEventLongPressListener);
        mWeekView.getParent().requestDisallowInterceptTouchEvent(true);
        return view;
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }
}
