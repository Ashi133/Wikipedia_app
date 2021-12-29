package com.android.documentsharing;

import java.util.Dictionary;
import java.util.Hashtable;

public class IconsHolder {
    static Dictionary<String, Integer> icons;
    public static int getIcon(String icon){
        initialize();
        return icons.get(icon);
    }

    public static void initialize() {
        icons=new Hashtable<String, Integer>();
        icons.put("pdf",R.drawable.pdf);
        icons.put("apk",R.drawable.android);
        icons.put("c",R.drawable.c);
        icons.put("cpp",R.drawable.cpp);
        icons.put("csv",R.drawable.csv);
        icons.put("docx",R.drawable.doc);
        icons.put("mp3",R.drawable.audio);
        icons.put("mp4",R.drawable.audio);
        icons.put("m4a",R.drawable.audio);
        icons.put("wav",R.drawable.audio);
        icons.put("java",R.drawable.java);
        icons.put("jpg",R.drawable.jpg);
        icons.put("png",R.drawable.png);
        icons.put("json",R.drawable.json);
        icons.put("py",R.drawable.python);
        icons.put("txt",R.drawable.txt);
        icons.put("xlsx",R.drawable.xls);
        icons.put("mov",R.drawable.video);
        icons.put("wmv",R.drawable.video);
        icons.put("flv",R.drawable.video);
        icons.put("avi",R.drawable.video);
    }

}
