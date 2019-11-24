package com.rdi.todolist.models;

public class ToDo {
    private final boolean mToDoIsDone;
    private final String mToDoText;
    private final int mToDoID;

    public ToDo(boolean toDoIsDone, String toDoText, int toDoID) {
        mToDoIsDone = toDoIsDone;
        mToDoText = toDoText;
        mToDoID = toDoID;
    }

    public int getToDoID() {
        return mToDoID;
    }

    public boolean getToDoIsDone() {
        return mToDoIsDone;
    }

    public String getToDoText() {
        return mToDoText;
    }
}
