package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseIndex extends AppCompatActivity {

    Intent it_prev;
    String token;
    int cid;
    WebApi webapi;

    ////View objects
    Button btn_video,btn_pastquiz,btn_quizlist;
    TextView txt_hello;

    ListView previewList;
    ArrayAdapter<String> listadapter;
    List<QuizesModel> QuizList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);
        setTitle("行動學習作答-"+ConfigFile.version+"版");
        webapi = new WebApi();
        setParameters();
        btn_pastquiz=(Button)findViewById(R.id.pastQuiz);
        btn_video=(Button)findViewById(R.id.videos);
        btn_quizlist=(Button)findViewById(R.id.qlist_btn);

        setButtonListener();

        txt_hello = (TextView)findViewById(R.id.helloText);
        String userName=webapi.POST("AccountApi/getUserName","TokenString="+token);
        txt_hello.setText("Hello!"+userName);


        TextView message = (TextView)findViewById(R.id.textView7);

        QuizList = new ArrayList<QuizesModel>();
        AddtoList(webapi.GET("QuizsApi/GetQuizPart?status=1&qid=&token=&cid="));
        if(QuizList.size()==0)
        {
            message.setText("尚無進行中試題");
            message.setTextColor(Color.BLACK);
        }
        else
        {
            message.setText("有進行中試題!   請點選\"前往作答\"");
            message.setTextColor(Color.RED);
        }
        previewList =(ListView)findViewById(R.id.previewQuiz);
        listadapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,QuizList);
        previewList.setAdapter(listadapter);
    }
    void AddtoList(String jsonstring) {
        try {
            JSONArray jsonArray = new JSONArray(jsonstring);

            for (int i = 0; i < jsonArray.length(); i++) {
                QuizesModel qui = new QuizesModel();
                JSONObject quiz = jsonArray.getJSONObject(i);
                qui.id = Integer.parseInt(quiz.getString("id"));
                qui.Name = quiz.getString("Name");
                qui.DateTime = quiz.getString("DateTime");
                qui.remark = quiz.getString("remark");
                QuizList.add(qui);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(CourseIndex.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void setButtonListener()
    {
        btn_pastquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(0);
            }
        });
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(1);
            }
        });
        btn_quizlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(2);
            }
        });
    }
    void goFeature(int feature)
    {
        Intent it_go=new Intent();
        it_go.putExtra("token",token);
        it_go.putExtra("cid",cid);

        switch (feature)
        {
            case 0:
                it_go.setClass(CourseIndex.this,QuizHistoryList.class);
                startActivity(it_go);
                break;
            case 1:
                it_go.setClass(CourseIndex.this,VideoList.class);
                startActivity(it_go);
                break;
            case 2:
                it_go.setClass(CourseIndex.this,QuizList.class);
                startActivity(it_go);
                break;
        }
    }

    void setParameters()
    {
        it_prev=getIntent();

        token=it_prev.getStringExtra("token");
        cid=it_prev.getIntExtra("cid",-1);

    }
}
