package com.example.yarden.hotshot.Client;


import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class DataSaveLocaly {

    private File file;
    private String fileName = "GetWifi";
    private StringBuilder res = null;
    private Context context;
    private boolean emptyFile = true;

    public DataSaveLocaly(Context _context){
        context = _context;
    }


    public String getReadData(){
        readFromFile();
        if(emptyFile)
            return "0";
        else
            return res.toString();
    }

    private void readFromFile() // if we work wirless we want to save how much mb the client can use
    {
        try {
            String filePath = context.getFilesDir().getPath().toString() + "/"+ fileName +".txt";
            file = new File(filePath);
            if(!file.exists()) // first time its used
            {
                    file.createNewFile();
            }
            else  // file exist
            {
                 res = new StringBuilder();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        res.append(line);
                        emptyFile = false;

                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String mb) // in the end of use update how mb left
    {
        String filePath = context.getFilesDir().getPath().toString() + "/"+ fileName +".txt";
        file = new File(filePath);
        if(!file.exists()) // first time its used
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            emptyFile = false;
            FileOutputStream out = new FileOutputStream(file, false);
            byte[] contents = mb.getBytes();
            out.write(contents);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
