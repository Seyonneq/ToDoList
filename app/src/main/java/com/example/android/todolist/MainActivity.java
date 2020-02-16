package com.example.android.todolist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<>();
    int[] backgroundColors;
    int[] textColors;
    int[] selectedBackgroundColors;
    int[] selectedTasks;
    int selectedTasksCounter = 0;

    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        int maxTasks = 50;
        backgroundColors = new int[maxTasks];
        textColors = new int[maxTasks];
        selectedBackgroundColors = new int[maxTasks];
        selectedTasks = new int[maxTasks];

        for (int i = 0; i<maxTasks; i++) {
            if(i%2 == 0) {
                backgroundColors[i] = Color.WHITE;
                textColors[i] = Color.BLACK;
                selectedTasks[i] = -1;
            } else {
                backgroundColors[i] = Color.GRAY;
                textColors[i] = Color.BLACK;
                selectedTasks[i] = -1;
            }
        }

        readTasks();

        for (int i = 0; i <list.size(); i++) {
            if(list.get(i).startsWith("!!")){
                backgroundColors[i] = Color.RED;
            }else if(list.get(i).startsWith("!")){
                backgroundColors[i] = Color.CYAN;
            }
        }

        adapter.setData(list, backgroundColors, textColors);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                if(isSelected){
                    int selectedTasksFound =0;
                    int i =0;
                    while (selectedTasksFound<selectedTasksCounter) {
                        if(selectedTasks[i]==position){
                            selectedBackgroundColors[position] = backgroundColors[position];
                            adapter.setData(list, selectedBackgroundColors, textColors);
                            selectedTasksCounter--;
                            selectedTasks[i] = -1;
                            if(selectedTasksCounter==0){
                                isSelected =false;
                            }
                            return;
                        }
                        if(selectedTasks[i]!=-1){
                            selectedTasksFound++;
                        }
                        i++;
                    }
                    return;
                }
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.remove(position);
                                adapter.setData(list, backgroundColors, textColors);
                                saveTasks();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                        dialog.show();
            }});

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                for(int i = 0; i<list.size(); i++) {
                    if(selectedBackgroundColors[i]!=Color.LTGRAY){
                    selectedBackgroundColors[i]= backgroundColors[i];
                }}
                isSelected = true;
                selectedTasks[selectedTasksCounter] = position;
                selectedTasksCounter++;
                selectedBackgroundColors[position]=Color.LTGRAY;
                adapter.setData(list, selectedBackgroundColors, textColors);
                return true;
            }
        }
        );

        final Button newTaskButton = findViewById(R.id.newTaskButton);

        newTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText taskInput = new EditText(MainActivity.this);
                taskInput.setSingleLine();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Add a new task")
                    .setMessage("What is your new task?")
                    .setView(taskInput)
                    .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String task = taskInput.getText().toString();
                            if(task.startsWith("!!")) {
                                int taskCounter = list.size();
                                list.add(" ");
                                while (taskCounter > 0) {
                                    list.set(taskCounter, list.get(taskCounter - 1));
                                    backgroundColors[taskCounter] = backgroundColors[taskCounter - 1];
                                    textColors[taskCounter] = textColors[taskCounter - 1];
                                    taskCounter--;
                                }
                                list.set(0, task);
                                backgroundColors[0] = Color.RED;
                                textColors[0] = Color.BLACK;
                            } else if (task.startsWith("!")) {
                                int taskCounter = list.size();
                                list.add(" ");
                                int importantTasksCounter = 0;
                                while (importantTasksCounter < taskCounter &&
                                        list.get(importantTasksCounter).startsWith("!!")) {
                                    importantTasksCounter++;
                                }
                                while (taskCounter > importantTasksCounter) {
                                    list.set(taskCounter, list.get(taskCounter - 1));
                                    backgroundColors[taskCounter] = backgroundColors[taskCounter - 1];
                                    textColors[taskCounter] = textColors[taskCounter - 1];
                                    taskCounter--;
                                }
                                list.set(importantTasksCounter, task);
                                backgroundColors[0] = Color.CYAN;
                                textColors[0] = Color.BLACK;
                            } else {
                                list.add(task);
                            }
                            adapter.setData(list, backgroundColors, textColors);
                            saveTasks();
                        }
                    })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }});

        final Button deleteAllButton = findViewById(R.id.deleteAllButton);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete All Taska?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.clear();
                                adapter.setData(list, backgroundColors, textColors);
                                saveTasks();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .create();
                dialog.show();
            }
        });
    }

        private void saveTasks(){
        try{
            File saveFile = new File(this.getFilesDir(), "saved");

            FileOutputStream FOut = new FileOutputStream(saveFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(FOut));

            for (int i = 0; i<list.size(); i++){
                writer.write(list.get(i));
                writer.newLine();
            }
            FOut.close();
            writer.close();
        }
        catch(Exception a){
            a.printStackTrace();
        }}

        private void readTasks(){
            File saveFile = new File(this.getFilesDir(), "readed");
            if(!saveFile.exists()){
                return;
            }

            try{
                FileInputStream input = new FileInputStream(saveFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                while(line!=null){
                    list.add(line);
                    line=reader.readLine();
                }
            }
            catch (Exception a){
                a.printStackTrace();
            }
            }

    class TextAdapter extends BaseAdapter{

        List<String> list = new ArrayList<>();

        int[] backgroundColors;

        void setData(List<String> mList, int[] mBackgroundColors, int []mtextColors){
            list.clear();
            list.addAll(mList);
            backgroundColors = new int[list.size()];
            textColors =  new int[list.size()];
            for(int i = 0; i<list.size(); i++){
                backgroundColors[i] = mBackgroundColors[i];
                textColors[i] = mtextColors[i];
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int posiiton){
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater)
                        MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

            final TextView textView = convertView.findViewById(R.id.task);

            textView.setText(list.get(position));
            textView.setBackgroundColor(backgroundColors[position]);

                return convertView;
            }
    }
}
