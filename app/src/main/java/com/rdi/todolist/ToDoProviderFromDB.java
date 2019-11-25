package com.rdi.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Toast;

import com.rdi.todolist.models.ToDo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class ToDoProviderFromDB {

    enum State {LOADING, DELETE, UPDATE, WRITE}

    private static WeakReference<MainActivity> mActivityRef;

    static State mState;

    private String mToDoText;


    ToDoProviderFromDB(MainActivity mainActivity) {
        mActivityRef = new WeakReference<>(mainActivity);
    }

    public void writeInDB(String toDoText) {
        mState = State.WRITE;
        mToDoText = toDoText;
        new ToDoWorkWithDBAsync().execute(0);
    }

    public void loadDataFromDB() {
        mState = State.LOADING;
        new ToDoWorkWithDBAsync().execute(0);
    }

    public void updateDataFromDB(int toDoIDForUpdate, int isChecked) {
        mState = State.UPDATE;
        new ToDoWorkWithDBAsync().execute(toDoIDForUpdate, isChecked);
    }

    public void deleteToDoFromDB(int toDoIDForDelete) {
        mState = State.DELETE;
        new ToDoWorkWithDBAsync().execute(toDoIDForDelete);
    }

    private class ToDoWorkWithDBAsync extends AsyncTask<Integer, Void, List<ToDo>> {
        @Override
        protected void onPreExecute() {
            switch (mState) {
                case LOADING:
                    setLoadingView();
            }
        }

        @Override
        protected List<ToDo> doInBackground(Integer... params) {
            switch (mState) {
                case UPDATE:
                    return updateDBInAcync(params[0], params[1]);
                case DELETE:
                    return deleteFromDBInAsync(params[0]);
                case WRITE:
                    return writeInDBInAsync();

                default:
                    return readFromDBInAsync();
            }
        }

        @Override
        protected void onPostExecute(List<ToDo> toDoList) {
            updateMainActivity(toDoList);
        }
    }


    private List<ToDo> deleteFromDBInAsync(Integer toDoIDForDelete) {
        MainActivity mainActivity = mActivityRef.get();
        SQLiteDatabase db = new ToDoListDbHelper(mainActivity).getWritableDatabase();

        // 2. Определяем, какие строки нам нужно удалить
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(toDoIDForDelete)};
        // 3. Удаляем и получаем количество измененных строк
        int count = db.delete(
                DBContracts.ToDoListTable.NAME,
                selection,
                selectionArgs);
        db.close();
        return readFromDBInAsync();
    }


    private List<ToDo> writeInDBInAsync() {
        MainActivity mainActivity = mActivityRef.get();
        SQLiteDatabase db = new ToDoListDbHelper(mainActivity).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContracts.ToDoListTable.Cols.TO_DO_TEXT, mToDoText);
        values.put(DBContracts.ToDoListTable.Cols.TO_DO_IS_DONE, 0);

        long newRowId = db.insert(DBContracts.ToDoListTable.NAME, null, values);
        mToDoText = null;
        db.close();
        return readFromDBInAsync();
    }


    private List<ToDo> updateDBInAcync(Integer toDoIDForUpdate, Integer isChecked) {
        MainActivity mainActivity = mActivityRef.get();
        SQLiteDatabase db = new ToDoListDbHelper(mainActivity).getWritableDatabase();

        // 2. Задаем значения, на которые следует обновить данные
        ContentValues values = new ContentValues();
        values.put(DBContracts.ToDoListTable.Cols.TO_DO_IS_DONE, isChecked);

        // 3. Определяем, какую строку нам нужно обновить
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(toDoIDForUpdate)};

        // 4. Обновляем данные и получаем количество измененных строк
        int count = db.update(
                DBContracts.ToDoListTable.NAME,
                values,
                selection,
                selectionArgs);
        db.close();
        return readFromDBInAsync();
    }

    private List<ToDo> readFromDBInAsync() {
        MainActivity mainActivity = mActivityRef.get();
        SQLiteDatabase db = new ToDoListDbHelper(mainActivity).getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                DBContracts.ToDoListTable.Cols.TO_DO_TEXT,
                DBContracts.ToDoListTable.Cols.TO_DO_IS_DONE
        };
        Cursor cursor = db.query(
                DBContracts.ToDoListTable.NAME, // Название таблиы
                projection, // Какие колонки нужны - columns
                null, // Условие
                null, // Значения условия
                null, // группировка – groupBy
                null, // фильтр (после группировки) - having
                null // сортировка
        );

        List<ToDo> toDoList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {

                int ToDoID = cursor.getInt(
                        cursor.getColumnIndex(BaseColumns._ID));

                String toDoText = cursor.getString(
                        cursor.getColumnIndex(DBContracts.ToDoListTable.Cols.TO_DO_TEXT));

                int ToDoIsDone = cursor.getInt(
                        cursor.getColumnIndex(DBContracts.ToDoListTable.Cols.TO_DO_IS_DONE));
                toDoList.add(new ToDo(ToDoIsDone == 1 ? true : false, toDoText, ToDoID));
            }
        } finally {
            cursor.close();
        }
        db.close();
        return toDoList;
    }


    private void setLoadingView() {
        MainActivity mainActivity = mActivityRef.get();
        if (mainActivity != null) {
            mainActivity.mProgressBar.setVisibility(View.VISIBLE);
            mainActivity.layoutMainView.setVisibility(View.GONE);
        }
    }

    private void updateMainActivity(List<ToDo> toDoList) {
        MainActivity mainActivity = mActivityRef.get();
        if (mainActivity == null) {
            return;
        }
        mainActivity.mProgressBar.setVisibility(View.GONE);
        mainActivity.layoutMainView.setVisibility(View.VISIBLE);
        if (toDoList == null) {
            Toast.makeText(mainActivity.getBaseContext(), "ничего нет в базе данных", Toast.LENGTH_SHORT).show();
        } else {
            mainActivity.mToDoList = toDoList;
            mainActivity.setRecyclerViewAdapter();
        }
    }
}
