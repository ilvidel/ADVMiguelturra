package org.advmiguelturra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adaptador para la lista de partidos
 *
 * Created by nacho on 27/06/15.
 */
public class CompetitionListAdapter extends BaseAdapter {

    private Context context;
    private List<Competition> items;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent calendarActivity = new Intent(context, DayActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("competition", items.get(v.getId()));
            calendarActivity.putExtras(bundle);
            context.startActivity(calendarActivity);
        }
    };

    public CompetitionListAdapter(Context context, List<Competition> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Competition getItem(int position) {
        return items.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View compButton = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            compButton = inflater.inflate(R.layout.competitionlist_item, parent, false);
        }

        // Set data into the view.
        ImageView icon = (ImageView) compButton.findViewById(R.id.competitionIcon);
        TextView name = (TextView) compButton.findViewById(R.id.competitionName);
        TextView date = (TextView) compButton.findViewById(R.id.competitionDate);

        Competition competition = items.get(position);
        name.setText(competition.getName());
        date.setText(competition.getDuration());

        // Set icon according to competition type
        switch (competition.getType()){
            case RFEVB:
                icon.setImageResource(R.drawable.rfevb); //official RFEVB
                break;
            case FVCM:
                icon.setImageResource(R.drawable.fvcm); //official FVCM
                break;
            case LEAGUE:
                icon.setImageResource(R.drawable.liga); // liga regular
                break;
            case BEACH:
                icon.setImageResource(R.drawable.beach); // playa
                break;
            case TROPHY:
                icon.setImageResource(R.drawable.trophy); //torneo
                break;
            case JCCM:
                icon.setImageResource(R.drawable.jccm); //liga escolar
                break;
            default:
                icon.setImageResource(R.drawable.competitions_default);
                break;
        }

        // make row clickable
        compButton.setClickable(true);
        compButton.setId(position);
        compButton.setOnClickListener(clickListener);

        return compButton;
    }
}
