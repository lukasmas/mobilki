package com.example.project_235315;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    String CHANNEL_ID = "reminder";
    ArrayList<Task> tasks;
    Button btnNew;
    SQLiteDatabase db;
    SQLiteDatabase dbReader;

    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(this);

        dbReader = dbHelper.getReadableDatabase();
        db = dbHelper.getWritableDatabase();
        tasks = getTasksFromDB();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, tasks);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        btnNew = (Button) findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Long id = tasks.get(tasks.size() - 1).taskId + 1;
                Task tmp = new Task(id, "task" + (id + 1), false, new Date());
                if(putNewTaskInDB(tmp) != -1) {
                    tasks.add(tmp);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Adding tasks to DB has failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPause() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Tasks to do")
                .setContentText("Task_X is about to be done")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

        super.onPause();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteButtonClick(View view, int position) {
        Task deleted = tasks.get(position);
        tasks.remove(position);
        adapter.notifyDataSetChanged();

        String selection = BaseColumns._ID + " = " + deleted.taskId;
        dbReader.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, null);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private ArrayList<Task> getTasksFromDB(){
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DESC,
        };
        Cursor cursor = dbReader.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        ArrayList<Task> tasksFromDB = new ArrayList<>();
        while (cursor.moveToNext()) {
            String task = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESC));
            Long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            tasksFromDB.add(new Task(id, task, false, new Date()));
        }
        cursor.close();
        return tasksFromDB;
    }

    private Long putNewTaskInDB(Task task){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESC, task.taskDesc);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IS_DONE, task.isDone ? 1 : 0);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IS_DUE_DATE, task.dueDate.toString());

// Insert the new row, returning the primary key value of the new row
        return db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
    }

}