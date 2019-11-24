package com.rdi.todolist;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.rdi.todolist.models.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ToDo> mToDos;


    public void setToDos(List<ToDo> toDos) {
        mToDos = toDos == null ? null : new ArrayList<>(toDos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new ToDoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        ToDo toDo = mToDos.get(position);
        ((ToDoHolder) holder).mToDo.setText(toDo.getToDoText());
        ((ToDoHolder) holder).mCheckBoxIsDone.setChecked(toDo.getToDoIsDone());

        ((ToDoHolder) holder).mToDo.setClickable(true);
        ((ToDoHolder) holder).mToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ToDoHolder) holder).mCheckBoxIsDone.setChecked(
                        ((ToDoHolder) holder).mCheckBoxIsDone.isChecked() ? false : true
                );
            }
        });
        ((ToDoHolder) holder).mToDo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(view.getContext(), ((ToDoHolder) holder).mToDo);
                //inflating menu from xml resource
                popup.inflate(R.menu.popupmenu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                ToDoProviderFromDB.deleteToDoFromDB(mToDos.get(position).getToDoID());
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        });

        ((ToDoHolder) holder).mCheckBoxIsDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToDoProviderFromDB.updateDataFromDB(mToDos.get(position).getToDoID(), isChecked == true ? 1 : 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mToDos.size();
    }

    static class ToDoHolder extends RecyclerView.ViewHolder {
        private final TextView mToDo;
        private final CheckBox mCheckBoxIsDone;


        public ToDoHolder(@NonNull View itemView) {
            super(itemView);
            mToDo = itemView.findViewById(R.id.text_view_to_do);
            mCheckBoxIsDone = itemView.findViewById(R.id.checkbox_to_do_is_done);
        }
    }
}
