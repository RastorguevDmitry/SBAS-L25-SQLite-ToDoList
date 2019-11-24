package com.rdi.todolist;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ToDoListDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ToDoList.db";

    public ToDoListDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Создание БД, если она была открыта впервые
        sqLiteDatabase.execSQL("create table " + DBContracts.ToDoListTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DBContracts.ToDoListTable.Cols.TO_DO_TEXT + " text, " +
                DBContracts.ToDoListTable.Cols.TO_DO_IS_DONE + " integer)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}