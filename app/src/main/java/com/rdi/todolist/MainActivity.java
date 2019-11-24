package com.rdi.todolist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rdi.todolist.models.ToDo;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mButton;
    private EditText editText;
    private LinearLayout viewAlertDialog;
    ProgressBar mProgressBar;
    LinearLayout layoutMainView;

    private ToDoProviderFromDB mToDoProviderFromDB;

    private RecyclerView recyclerView;
    private ToDoRecyclerViewAdapter mToDoRecyclerViewAdapter;

    List<ToDo> mToDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToDoProviderFromDB = new ToDoProviderFromDB(this);

        initViews();

        mToDoProviderFromDB.loadDataFromDB();

        initRecyclerView();
        setRecyclerViewAdapter();

    }

    private void initRecyclerView() {
        mToDoRecyclerViewAdapter = new ToDoRecyclerViewAdapter();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    void setRecyclerViewAdapter() {
        mToDoRecyclerViewAdapter.setToDos(mToDoList);
        recyclerView.setAdapter(mToDoRecyclerViewAdapter);
    }

    private void initViews() {

        mButton = findViewById(R.id.to_add_btn);
        mProgressBar = findViewById(R.id.progress_circular);
        layoutMainView = findViewById(R.id.layout_main_view);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        viewAlertDialog = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.dialog_maket, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewAlertDialog);
        editText = viewAlertDialog.findViewById(R.id.to_do_text_in_dialog);
        builder.setTitle("Введите задачу");
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mToDoProviderFromDB.writeInDB(String.valueOf(editText.getText()));
            }
        });
        builder.show();
    }
}
