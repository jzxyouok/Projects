package cn.ccxxs.friendcalendar.CreateFun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ccxxs.friendcalendar.R;

public class ContentEdit extends AppCompatActivity {
    @Bind(R.id.contentEditDisplay)
    EditText contentEditDisplay;
    @Bind(R.id.contentcomplete)
    ImageView contentcomplete;
    @Bind(R.id.contentclose)
    ImageView contentclose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_edit);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.contentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String Content = intent.getStringExtra("editingContent");
        if(!Content.equals("活动内容")){
            contentEditDisplay.setText(Content);
            contentEditDisplay.setSelection(Content.length());
        }
    }

    @OnClick(R.id.contentcomplete)
    public void complete() {
        Intent returnIntent = new Intent();
        String content = contentEditDisplay.getText().toString();
        returnIntent.putExtra("contentresult",content);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnClick(R.id.contentclose)
    public void close() {
        Intent returnIntent = new Intent();
        String content = contentEditDisplay.getText().toString();
        returnIntent.putExtra("contentresult",content);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


}
