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
        icons= new Hashtable<>();
        icons.put("pdf",R.drawable.pdf);
        icons.put("apk",R.drawable.android);
        icons.put("c",R.drawable.c);
        icons.put("cpp",R.drawable.cpp);
        icons.put("csv",R.drawable.csv);
        icons.put("docx",R.drawable.doc);
        icons.put("doc",R.drawable.doc);
        icons.put("mp3",R.drawable.audio);
        icons.put("mp4",R.drawable.audio);
        icons.put("m4a",R.drawable.audio);
        icons.put("wav",R.drawable.audio);
        icons.put("flac",R.drawable.audio);
        icons.put("fev",R.drawable.audio);
        icons.put("omg",R.drawable.audio);
        icons.put("igp",R.drawable.audio);
        icons.put("wow",R.drawable.audio);
        icons.put("vlc",R.drawable.audio);
        icons.put("ogg",R.drawable.audio);
        icons.put("ac3",R.drawable.audio);
        icons.put("amf",R.drawable.audio);
        icons.put("java",R.drawable.java);
        icons.put("jpg",R.drawable.jpg);
        icons.put("jpeg",R.drawable.jpg);
        icons.put("png",R.drawable.png);
        icons.put("json",R.drawable.json);
        icons.put("py",R.drawable.python);
        icons.put("txt",R.drawable.txt);
        icons.put("xlsx",R.drawable.xls);
        icons.put("mov",R.drawable.video);
        icons.put("wmv",R.drawable.video);
        icons.put("flv",R.drawable.video);
        icons.put("avi",R.drawable.video);
        icons.put("webm",R.drawable.video);
        icons.put("m4p",R.drawable.video);
        icons.put("m4v",R.drawable.video);
        icons.put("avchd",R.drawable.video);
        icons.put("kt",R.drawable.kotlin);
        icons.put("html",R.drawable.html);
        icons.put("css",R.drawable.css);
        icons.put("js",R.drawable.javascript);
        icons.put("rar",R.drawable.rar);
        icons.put("vbs",R.drawable.vb);
        icons.put("xml",R.drawable.xml);
        icons.put("zip",R.drawable.zip);
        icons.put("sh",R.drawable.sh);
    }

}
