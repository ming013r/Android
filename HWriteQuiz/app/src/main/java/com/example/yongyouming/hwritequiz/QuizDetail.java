package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class QuizDetail extends AppCompatActivity {
    WebApi webapi;
    int qid,cid;
    String token;

    answerModel ansmodel;
    quizModel quizmodel;

    ImageView myans;
    Button toOriginBtn,toMyAnsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        setTitle("行動學習作答-"+ConfigFile.version+"版");
        setParas();




         quizmodel=new quizModel();
         ansmodel=new answerModel();
        try{
            String JsonSourceQuiz =webapi.GET("QuizsApi/GetQuiz?id="+qid);
            JSONObject qui =new JSONObject(JsonSourceQuiz);

            quizmodel.id=Integer.parseInt(qui.getString("id"));
            quizmodel.DateTime=qui.getString("Time_close");
            quizmodel.Image=qui.getString("image");
            quizmodel.Name=qui.getString("Name");

            String JsonSourceAns = webapi.GET("QuizsApi/GetAnswer?token="+token+"&qid="+qid);
            JSONObject ans = new JSONObject(JsonSourceAns);
            ansmodel.Image=ans.getString("file");
            ansmodel.Score=Integer.parseInt(ans.getString("Score"));
            ansmodel.UsualMistake=ans.getString("UsualMistake");
            ansmodel.remark = ans.getString("remark");



        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(QuizDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        TextView tv= (TextView)findViewById(R.id.pastQuizName);
        tv.setText(quizmodel.Name);
        TextView score =(TextView)findViewById(R.id.tv_myScore);
        score.setText("得分 : "+ansmodel.Score);
        TextView mistake =(TextView)findViewById(R.id.tv_myMistake);
        mistake.setText("作答建議 : "+ansmodel.UsualMistake);
        TextView suggestion = (TextView)findViewById(R.id.tv_suggest);
        suggestion.setVisibility(View.INVISIBLE);
        if(ansmodel.remark!=null && ansmodel.remark!="")
        {
            suggestion.setVisibility(View.VISIBLE);
            suggestion.setText("評語 : "+ansmodel.remark);
        }




        //將ImageView圖片改為試卷檔
        toOriginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=Constants.dURL+GetImage(qid);
                Glide.with(QuizDetail.this).load(Constants.dURL+GetImage(qid)).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        myans.setImageBitmap(resource);
                    }
                });
            }
        });
        //將ImageView圖片改為學生的檔案
        toMyAnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=Constants.dURL+ansmodel.Image;
                Glide.with(QuizDetail.this).load(Constants.dURL+ansmodel.Image).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        myans.setImageBitmap(resource);
                    }
                });
            }
        });



         myans =(ImageView)findViewById(R.id.img_myans);
        Glide.with(this).load(Constants.dURL+ansmodel.Image).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                myans.setImageBitmap(resource);
            }
        });
        //zoom
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(myans);
        pAttacher.update();





        Button btn = (Button)findViewById(R.id.btn_goSample);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSample();
            }
        });

    }
    void gotoSample()
    {
        Intent it =new Intent();
        it.putExtra("qid",qid);
        it.putExtra("cid",cid);
        it.putExtra("token",cid);

        it.setClass(QuizDetail.this,SampleList.class);
        startActivity(it);
    }
    public String GetImage(int qid)
    {
        String Image="";
        try{
            JSONArray jsonArray =new JSONArray(webapi.GET("QuizsApi/GetQuizPart?status=1&qid="+qid+"&token=&cid="));
            JSONObject currentQuiz=jsonArray.getJSONObject(0);
            Image=currentQuiz.getString("path");
        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(QuizDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return Image;
    }
    void setParas()
    {
        webapi=new WebApi();

        Intent it_prev=getIntent();
        qid=it_prev.getIntExtra("qid",-1);
        cid=it_prev.getIntExtra("cid",-1);
        token =it_prev.getStringExtra("token");



        toMyAnsBtn =(Button)findViewById(R.id.toMyAns);
        toOriginBtn=(Button)findViewById(R.id.toOrigin);
    }
}
