package com.example.android.todolist;

import android.content.Context;
import android.content.DialogInterface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        readTasks();

        adapter.setData(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete task")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.remove(position);
                                adapter.setData(list);
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                        dialog.show();
            }});

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
                            list.add(taskInput.getText().toString());
                            adapter.setData(list);
                        }
                    })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }});
    }

    @Override
    protected void onPause(){
        super.onPause();;
        saveTasks();
    }

        private void saveTasks(){
        try{
            File saveFile = new File(this.getFilesDir(), "saved");

            FileOutputStream FOut = new FileOutputStream(saveFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(FOut));

            for (int i = 0; i<list.size(); i++){
                writer.write(list.get(i));
                writer.newLine();;
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
        void setData(List<String> mList){
            list.clear();
            list.addAll(mList);
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
            LayoutInflater inflater =(LayoutInflater)
                    MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item, parent, false);
            TextView textView = rowView.findViewById(R.id.task);
            textView.setText(list.get(position));
            return rowView;
        }
    }
}
