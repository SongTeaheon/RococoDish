package com.rococodish.front_ui.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.rococodish.front_ui.R;
import java.util.ArrayList;

public class SearchRecordFragment extends Fragment {
    private final String TAG = "TAGSearchRecordFrag";
    ListView listView;
    TextView deleteAllRecord_tv;
    ArrayList<RecordData> recordList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_record, container, false);
        listView = rootView.findViewById(R.id.listview_record);
        recordList = ((SubSearchPage)getActivity()).getRecordList();
        final CustomUsersAdapter adapter = new CustomUsersAdapter(getActivity(), recordList);
        deleteAllRecord_tv = rootView.findViewById(R.id.textAllDelete);
        deleteAllRecord_tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //delete all record
                SubSearchPage.dbHandler.deleteAll();
                recordList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);
        return rootView;
    }

}

class CustomUsersAdapter extends ArrayAdapter {
    Context mContext;
    ArrayList<RecordData> records;
    public CustomUsersAdapter(Context context, ArrayList records) {
        super(context, 0, records);
        this.mContext = context;
        this.records = records;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_last_search_row, parent, false);
        }

        // Get the data item for this position
        final RecordData record = (RecordData)getItem(position);


        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.lastStoreTv);
        ImageView ivCancel = convertView.findViewById(R.id.imageViewCancel);
        ConstraintLayout constraintLayout = convertView.findViewById(R.id.constraint_record);
        // Populate the data into the template view using the data object
        tvName.setText(record.getRecord());
        constraintLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((SubSearchPage)mContext).searchButtonClicked(record.getRecord()); //버튼 클릭시 이동
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                SubSearchPage.dbHandler.delete(record.getId());
                records.remove(position);
                notifyDataSetChanged();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}
