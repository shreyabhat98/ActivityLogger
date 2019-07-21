package com.example.activityloggerv2.model;


import android.arch.lifecycle.ViewModel;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UserActivities extends ViewModel {

    private File file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activites.csv");
    private File log_file = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "activities_log.csv");
    private File current_activity = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS), "current_activity");
    private String current = "";

    private Instant start;
    public List<String> loaded_activities = new ArrayList<String>();



    public UserActivities(){
        load_current();
        load_activities();
    }

    private void write_file(File file,String writable, Boolean append){
        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file,append));
            BufferedWriter buffered_writer = new BufferedWriter(file_writer);
            buffered_writer.write(writable);
            buffered_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log_activity(){
        if (current != ""){
            String start_time = start.toString();
            int min = duration();
            String end = java.time.Instant.now().toString();
            String writable = current +","+ start_time+","+end+","+Integer.toString(min)+"\n";
            write_file(log_file ,writable,true);
        }

    }

    public void load_current(){
        File current_activity = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "current_activity");
        try{
            if(!current_activity.exists()){
                current_activity.createNewFile();
            }else{
                BufferedReader br = new BufferedReader(new FileReader(current_activity));
                String line = br.readLine();
                if (line != null && line.split(",").length == 2){
                    current = line.split(",")[0];
                    start = Instant.parse(line.split(",")[1]);
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load_activities(){
        loaded_activities.add(current);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.split(",")[0];
                if(!line.contentEquals(current))
                    loaded_activities.add(line);
            }
            br.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save_activities(String message){
        write_file(file ,message+"\n",true);
        loaded_activities.add(message.split(",")[0]);
    }

    public int duration(){
        Instant time_now = java.time.Instant.now().plusSeconds(330*60);
        if (start == null)
            start = java.time.Instant.now().plusSeconds(330*60);
        return (int) Duration.between(start,time_now).toMinutes();

    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String select){
        current = select;
        start = java.time.Instant.now().plusSeconds(330*60);
        write_file(current_activity,current+","+start.toString(),false);
    }
}

