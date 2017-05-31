package voto.ado.sainthannaz.votoseguro.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import voto.ado.sainthannaz.votoseguro.R;

/**
 * Created by Belal on 9/22/2015.
 */

public class CustomList extends ArrayAdapter<String> {
    private String[] ids;
    private String[] code_votes;
    private String[] votes;
    private String[] date_votes;
    private String[] results;
    private Activity context;

    public CustomList(Activity context, String[] ids, String[] code_votes, String[] votes, String[] date_votes) {
        super(context, R.layout.list_view_layout, ids);
        this.context = context;
        this.ids = ids;
        this.code_votes = code_votes;
        this.votes = votes;
        this.date_votes = date_votes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_view_layout, null, true);
        TextView textViewId = (TextView) listViewItem.findViewById(R.id.textViewId);
        TextView textViewCodeVote = (TextView) listViewItem.findViewById(R.id.textViewCodeVote);
        TextView textViewVote = (TextView) listViewItem.findViewById(R.id.textViewVote);
        TextView textViewDateVote = (TextView) listViewItem.findViewById(R.id.textViewDateVote);

        textViewId.setText(ids[position]);
        textViewCodeVote.setText(code_votes[position]);
        textViewVote.setText(votes[position]);
        textViewDateVote.setText(date_votes[position]);

        return listViewItem;
    }
}