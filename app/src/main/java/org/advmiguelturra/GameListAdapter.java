package org.advmiguelturra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Adaptador para la lista de partidos
 *
 * Created by nacho on 27/06/15.
 */
public class GameListAdapter extends BaseAdapter {

    private Context context;
    private List<Game> items;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gameActivity = new Intent(context,GameActivity.class);
            gameActivity.putExtra("adding", false);
            gameActivity.putExtra("game", items.get(v.getId()).getId());
            context.startActivity(gameActivity);
        }
    };

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            final Game game = items.get(view.getId());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CloudantTask cloudant = new CloudantTask(context);
                    cloudant.execute(CloudantTask.Action.DELETE, game);
                    String response = null;
                    Object obj = null;

                    try {
                        obj = cloudant.get();
                        response = (String) obj;
                    } catch (InterruptedException e) {
                        Log.e("ITEM", "Operation interrupted: " + e);
                    } catch (ExecutionException e) {
                        Log.e("ITEM", e.toString());
                    } catch (ClassCastException e) { // la respuesta no es lo que se esperaba (error)
                        Log.w("COMPETITIONS", obj.toString());
                        Toast t = Toast.makeText(context, "No se ha encontrado una conexión válida", Toast.LENGTH_LONG);
                        t.show();
                    }

                    Toast t = Toast.makeText(context, response, Toast.LENGTH_LONG);
                    t.show();
                    items.remove(game);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setMessage("¿Desea borrar este partido?\n" + game.toString());
            builder.setTitle("Borrar");
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
    };

    public GameListAdapter(Context context, List<Game> items) {
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
    public Game getItem(int position) {
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
        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.gamelist_item, parent, false);
        }

        // Set data into the view.
        ImageView icon = (ImageView) rowView.findViewById(R.id.listIcon);
        TextView local = (TextView) rowView.findViewById(R.id.listLocalTeam);
        TextView visitor = (TextView) rowView.findViewById(R.id.listVisitorTeam);
        TextView date = (TextView) rowView.findViewById(R.id.listDate);
        TextView time = (TextView) rowView.findViewById(R.id.listTime);
        TextView locScore = (TextView) rowView.findViewById(R.id.listLocalScore);
        TextView visScore = (TextView) rowView.findViewById(R.id.listVisitorScore);
        TextView phase = (TextView) rowView.findViewById(R.id.listPhase);

        Game item = this.items.get(position);
        local.setSingleLine(true);
        local.setEllipsize(TextUtils.TruncateAt.END);
        local.setText(item.getTeamA());

        visitor.setSingleLine(true);
        visitor.setEllipsize(TextUtils.TruncateAt.END);
        visitor.setText(item.getTeamB());

        time.setText(item.getTime());
        date.setText(item.getDateAsString());
        locScore.setText(String.valueOf(item.getWonA()));
        visScore.setText(String.valueOf(item.getWonB()));

        if(item.getPhase()!=Phase.LIGA)
            phase.setText(item.getPhase().toString());
        else
            phase.setText("");

        switch(item.getCategory()){
            case SUPERLIGA:
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.smv);
                else icon.setImageResource(R.drawable.sfv);
                break;
            case SUPERLIGA2:
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.sm2);
                else icon.setImageResource(R.drawable.sf2);
                break;
            case PRIMERA:
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.pdm);
                else icon.setImageResource(R.drawable.pdf);
                break;
            case SEGUNDA:
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.sdm);
                else icon.setImageResource(R.drawable.sdf);
                break;
            case JUVENIL:
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.jm);
                else icon.setImageResource(R.drawable.jf);
                break;
            case CADETE:
                if(item.getDivision()==Division.MASCULINA)
                    icon.setImageResource(R.drawable.cm);
                else icon.setImageResource(R.drawable.cf);
                break;
            case INFANTIL:
                if(item.getDivision()==Division.MASCULINA)
                    icon.setImageResource(R.drawable.im);
                else icon.setImageResource(R.drawable.ifem);
                break;
            case ALEVIN:
                if(item.getDivision()==Division.MASCULINA)
                    icon.setImageResource(R.drawable.am);
                else icon.setImageResource(R.drawable.af);
                break;
            case AFICIONADOS: //playa
                if(item.getDivision()==Division.MIXTO)
                    icon.setImageResource(R.drawable.afimix);
                else icon.setImageResource(R.drawable.afic);
                break;
            case PRO: //playa
                if(item.getDivision()==Division.MASCULINA) icon.setImageResource(R.drawable.promasc);
                else if(item.getDivision()==Division.FEMENINA) icon.setImageResource(R.drawable.profem);
                else icon.setImageResource(R.drawable.promix);

        }

        // make row clickable
        rowView.setClickable(true);
        rowView.setId(position);
        rowView.setOnClickListener(clickListener);
        if(Administrator.ADMIN_MODE) {
            rowView.setOnLongClickListener(longClickListener);
        }

        return rowView;
    }
}
