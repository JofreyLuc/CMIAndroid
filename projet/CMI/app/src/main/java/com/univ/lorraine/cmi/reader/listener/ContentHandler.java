package com.univ.lorraine.cmi.reader.listener;

import com.skytree.epub.ContentListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by alexis on 13/05/2016.
 */
public class ContentHandler implements ContentListener {
    // you should return the length of file.
    public long getLength(String baseDirectory,String contentPath) {
        String path = baseDirectory + "/" + contentPath;
        File file = new File(path);
        if (file.exists()) return file.length();
        else return 0;
    }

    // You should return whether the file exists or not.
    public boolean isExists(String baseDirectory,String contentPath) {
        String path = baseDirectory + "/" + contentPath;
        File file = new File(path);
        if (file.exists()) return true;
        else return false;
    }

    // LastModified information should be returned to the engine.
    public long getLastModified(String baseDirectory,String contentPath) {
        String path = baseDirectory + "/" + contentPath;
        File file = new File(path);
        if (file.exists()) return file.lastModified();
        else return 0;
    }

    // you should deliver the requested file through the InputStream.
    // In this sample, FileInputStream is used.
    public InputStream getInputStream(String baseDirectory,String contentPath) {
        String path = baseDirectory + "/" + contentPath;
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            return fis;
        }catch(Exception e) {
            return null;
        }
    }
}