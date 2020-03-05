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
    int[] selectedBackgroundColors;
    int[] selectedTasks;
   // int selectedTasksCounter = 0;
    int selectedTasksIndex = 0;

    boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        final int maxTasks = 50;
        backgroundColors = new int[maxTasks];
        selectedBackgroundColors = new int[maxTasks];
        selectedTasks = new int[maxTasks];

        for (int i = 0; i < maxTasks; i++) {
            if (i % 2 == 0) {
                backgroundColors[i] = Color.WHITE;
                selectedTasks[i] = -1;
            }
        }

        readTasks();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).startsWith("!!")) {
                backgroundColors[i] = Color.RED;
            } else if (list.get(i).startsWith("!")) {
                backgroundColors[i] = Color.CYAN;
            }
        }

        adapter.setData(list, backgroundColors);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                if (isSelected) {
                    for(int i =0; i<selectedTasksIndex; i++){
                        if(selectedTasks[i] == position) {
                            selectedTasks[i] = -1;
                            selectedBackgroundColors[position] = backgroundColors[position];
                            adapter.setData(list, selectedBackgroundColors);
                            for(int j = 0; j< selectedTasksIndex;j++){
                                if(selectedTasks[j] != -1){
                                    return;
                                }
                            }
                            isSelected = false;
                            findViewById(R.id.deleteSelectedButton).setVisibility(View.GONE);
                            adapter.setData(list, backgroundColors);
                            selectedTasksIndex = 0;
                            return;
                        }
                    }
                        return;
                }
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                list.remove(position);
                                for (int i = 0; i < maxTasks; i++) {
                                        backgroundColors[i] = Color.WHITE;
                                }
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).startsWith("!!")) {
                                        backgroundColors[i] = Color.RED;
                                    } else if (list.get(i).startsWith("!")) {
                                        backgroundColors[i] = Color.CYAN;
                                    }
                                }
                                adapter.setData(list, backgroundColors);
                                saveTasks();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                @Override
                                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                    for (int i = 0; i < list.size(); i++) {
                                                        if (selectedBackgroundColors[i] != Color.LTGRAY) {
                                                            selectedBackgroundColors[i] = backgroundColors[i];
                                                        }
                                                    }
                                                    isSelected = true;
                                                    selectedTasks[selectedTasksIndex] = position;
                                                    selectedTasksIndex++;
                                                    selectedBackgroundColors[position] = Color.LTGRAY;
                                                    adapter.setData(list, selectedBackgroundColors);
                                                    findViewById(R.id.deleteSelectedButton).setVisibility(View.VISIBLE);
                                                    return true;
                                                }
                                            }
        );

        final Button newTaskButton = findViewById(R.id.newTaskButton);

        newTaskButton.setOnClickListener(new View.OnClickListener() {
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
                                if (task.startsWith("!!")) {
                                    int taskCounter = list.size();
                                    list.add(" ");
                                    while (taskCounter > 0) {
                                        list.set(taskCounter, list.get(taskCounter - 1));
                                        backgroundColors[taskCounter] = backgroundColors[taskCounter - 1];
                                        taskCounter--;
                                    }
                                    list.set(0, task);
                                    backgroundColors[0] = Color.RED;
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
                                        taskCounter--;
                                    }
                                    list.set(importantTasksCounter, task);
                                    backgroundColors[0] = Color.CYAN;
                                } else {
                                    list.add(task);
                                }
                                adapter.setData(list, backgroundColors);
                                saveTasks();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });

        final Button deleteAllButton = findViewById(R.id.deleteAllButton);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete All Taska?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                list.clear();
                                for(int i=0; i<maxTasks; i++){
                                    backgroundColors[i]=Color.WHITE;
                                }
                                adapter.setData(list, backgroundColors);
                                saveTasks();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .create();
                dialog.show();
            }
        });

        final Button deleteSelectedButton = findViewById(R.id.deleteSelectedButton);
        deleteSelectedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = selectedTasksIndex-1; i>=0; i--){
                    if(selectedTasks[i] != -1) {
                        list.remove(selectedTasks[i]);
                        selectedTasks[i] = -1;
                    }
                }
                for (int i = 0; i < maxTasks; i++) {
                    backgroundColors[i] = Color.WHITE;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).startsWith("!!")) {
                        backgroundColors[i] = Color.RED;
                    } else if (list.get(i).startsWith("!")) {
                        backgroundColors[i] = Color.CYAN;
                    }
                }
                adapter.setData(list, backgroundColors);
                findViewById(R.id.deleteSelectedButton).setVisibility(View.GONE);
                isSelected = false;
                selectedTasksIndex = 0;
                saveTasks();
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
        int[] adapterBackgroundColors;

        void setData(List<String> mList, int[] mBackgroundColors){
            list.clear();
            list.addAll(mList);
            adapterBackgroundColors = new int[list.size()];
            for(int i = 0; i<list.size(); i++){
                adapterBackgroundColors[i] = mBackgroundColors[i];
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return list.size();
        }

        @Override
        public Object getItem(int position){
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
