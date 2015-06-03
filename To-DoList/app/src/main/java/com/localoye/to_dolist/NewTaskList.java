package com.localoye.to_dolist;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Harsh on 26-05-2015.
 */
public class NewTaskList extends ListFragment {

    private String tasks[][];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch all task belonging to new category
        MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
        tasks =dbHandler.fetchList("new");

        // Display in listview
        ArrayAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks[0]);
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {

        // AlertDialog for viewing the details of task on click
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Details");
        final TextView textTitle = new TextView(getActivity());
        textTitle.setText("     Title: "+ tasks[0][position]);
        textTitle.setTextSize(18);
        textTitle.setTextColor(Color.BLACK);
        final TextView textDesc = new TextView(getActivity());
        textDesc.setText("     Description: "+ tasks[1][position]);
        textDesc.setTextSize(18);
        textDesc.setTextColor(Color.BLACK);
        final TextView textDate = new TextView(getActivity());
        textDate.setText("     Time: " + tasks[2][position]);
        textDate.setTextSize(18);
        textDate.setTextColor(Color.BLACK);

        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textTitle);
        linearLayout.addView(textDesc);
        linearLayout.addView(textDate);

        builder.setView(linearLayout);
        builder.setNegativeButton("Dismiss",null);
        builder.create().show();
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        // On long press of a listview display two options: started and delete
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int position, long id) {
                
                final int pos=position;
                final String[] option = new String[] { "Mark as Started", "Delete" };
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.select_dialog_item, option);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Options");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int positionSelected) {
                        // TODO Auto-generated method stub
                        // If 2nd position (Delete) is selected
                        if(positionSelected==1){
                            // Delete the task from DB
                            MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
                            boolean result=dbHandler.deleteList(tasks[0][pos], tasks[1][pos]);
                            // If delete is successful, refresh the list
                            if(result){
                                Toast.makeText(getActivity(), "Task Deleted", Toast.LENGTH_SHORT).show();
                                MyDBHandler dbHandler1 = new MyDBHandler(getActivity(), null, null, 1);
                                tasks =dbHandler1.fetchList("new");
                                ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks[0]);
                                setListAdapter(listAdapter);
                            }
                        }
                        // If 1st position (Started) is selected
                        else{
                            // Change the category of task in DB
                            MyDBHandler dbHandler = new MyDBHandler(getActivity(), null, null, 1);
                            dbHandler.updateList("inprogress", tasks[0][pos]);
                            Toast.makeText(getActivity(), "Task Marked as Started", Toast.LENGTH_SHORT).show();
                            // Refresh the list
                            MyDBHandler dbHandler1 = new MyDBHandler(getActivity(), null, null, 1);
                            tasks =dbHandler1.fetchList("new");
                            ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks[0]);
                            setListAdapter(listAdapter);
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }
}
