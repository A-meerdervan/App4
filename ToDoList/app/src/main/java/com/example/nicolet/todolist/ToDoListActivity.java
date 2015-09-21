package com.example.nicolet.todolist;

import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDoListActivity extends AppCompatActivity {

    private ArrayList<String> ToDoList = new ArrayList<String>();
    // set up the list filling helper
   // private String[] arr = {"a","b"};
    private ArrayAdapter<String> adapter;
    //private String[] ToDoList = {"iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz","iets1","iets2 enzoooo", "iets3 enqualla jamannz"};
    // Create the file in the right place, so that android gives permission to write to it
    private final String FileName = "ToDoList.txt";
    //text to speech
    private TextToSpeech tts;
    private boolean SpeechReady = false; // true when engine done loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        adapter = new ArrayAdapter<String>(this, R.layout.listvieww, ToDoList);
        fillListAndListen();
        fillListWithFile();
        setupSpeech();
    }
    private void setupSpeech(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                SpeechReady = true;
            }
        });
    }
    private void writeMessageToFile(String Message) {
        // write to a file
        try {
            PrintStream out = null;
            out = new PrintStream(openFileOutput(FileName, MODE_APPEND));
            out.println(Message);
            out.close();
        } catch (FileNotFoundException e) {
            Log.d("test", "file was not found by writer");
            e.printStackTrace();
        }
    }

    private void fillListWithFile() {
        // Read from the file and fill the ToDoList
        try {
            Scanner scan = null;
            scan = new Scanner(openFileInput(FileName));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();

                //Add line to list array
                ToDoList.add(line);
            }
            adapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            Log.d("test", "File not found by reader");
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), "message saved", Toast.LENGTH_LONG).show();
    }
    public void addItem(View view){
        EditText et = (EditText) findViewById(R.id.UserInput);
        ToDoList.add(et.getText().toString());
        writeMessageToFile(et.getText().toString());
        et.setText("");
        //updateData();
        adapter.notifyDataSetChanged();
    }
   /* // miss nog nodig als ie moeilijk gaat doen met updaten van de list
    private void updateData() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
    */

    private void fillListAndListen() {
        // create adapter for the list
        ListView list = (ListView) findViewById(R.id.ListView);

        // this lines fills the list ofzo
        list.setAdapter(adapter);
        // Dit is voor wanneer er 1 keer geclicked word
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                handleClick(index);
            }
        });
        // Wanneer er langer geclicked word, verwijder iets
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) {
                handleLongClick(index);
                return true;
            }
        });

    }


    public void handleLongClick(int index){
        // remove item when a list item is clicked
        ToDoList.remove(index);
        adapter.notifyDataSetChanged();
        CharSequence iets = "removed item " + index;
        Toast toast = Toast.makeText(getApplicationContext(), iets, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void handleClick(int index) {
        // speak the list item that was clicked
        if (SpeechReady) {
            tts.speak(ToDoList.get(index), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //update the file
        // write to a file
        try {
            PrintStream out = null;
            out = new PrintStream(openFileOutput(FileName, MODE_PRIVATE));
            for (int i=0; i<ToDoList.size(); i++){
                out.println(ToDoList.get(i));
            }
            out.close();
        } catch (FileNotFoundException e) {
            Log.d("test", "file was not found by writer");
            e.printStackTrace();
        }

    }
    // This will save the data when the screen rotates
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("test", "komt in OnSaveInst...");
        super.onSaveInstanceState(outState);
        EditText et = (EditText)findViewById(R.id.UserInput);
        outState.putString("edittext", et.getText().toString());
    }
    // When the screen is flipped and the activity is made, restore variables
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("test", "komt in OnrestoreInst....");
        super.onRestoreInstanceState(savedInstanceState);

        EditText et = (EditText)findViewById(R.id.UserInput);
        et.setText(savedInstanceState.getString("edittext"));
    }
}
