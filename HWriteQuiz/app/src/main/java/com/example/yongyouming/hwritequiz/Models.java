package com.example.yongyouming.hwritequiz;

import android.util.Log;

/**
 * Created by yongyouming on 2018/2/7.
 */

public class Models {
}
class CourseModel
{
    public int Id;
    public String Room;
    public String Name;

    @Override
    public String toString(){
        return Name;
    }
}
class VideoModel
{
    public int id ;
    public String Name;
    public String URL;
    @Override
    public String toString()
    {
        return Name  ;
    }
}
class QuizesModel
{
    public int id;
    public String Name;
    public String DateTime;
    public String remark;
    @Override
    public String toString()
    {
        String res =  "["+Name+"]";

        return res;
    }
}


class quizModel
{
    public int id;
    public String Name;
    public String DateTime;
    public String Image;
}
class answerModel
{
    public int id;
    public String Image;
    public int Score;
    public String  UsualMistake;
    public String remark;
    @Override
    public String toString(){return "["+id+"]分數 : "+Score;}
}
