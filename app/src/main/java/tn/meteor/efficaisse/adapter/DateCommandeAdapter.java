package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.utils.DateCommande;

/**
 * Created by lilk on 29/01/2018.
 */

public class DateCommandeAdapter extends RecyclerView.Adapter<DateCommandeAdapter.CommandeHolder> {


    private Context context;
    private List<DateCommande> dateCommandeList;
    public CommandeListener listener;
    private int row_index = 0;


    public DateCommandeAdapter(Context context, CommandeListener listener, List<DateCommande> dateCommandes) {
        this.context = context;
        this.listener = listener;
        this.dateCommandeList = dateCommandes;
    }


    @Override
    public CommandeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);

        return new CommandeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommandeHolder holder, int position) {
        DateCommande commande = dateCommandeList.get(position);

        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        holder.orderNumber.setText(commande.getDate().getUser().getName());
        holder.quantity.setText(commande.getCommandeList().size() + " commandes");
        holder.date.setText(formatter.format(commande.getDate().getDate()));
        if (commande.getDate().getClosed() != null)
            holder.dateClosed.setText(formatter.format(commande.getDate().getClosed()));
        else
            holder.dateClosed.setText("Maintenant");
        holder.sum.setText(String.format("%.3f",commande.getSum()) + " dt");
        holder.sum.setTextColor(Color.GREEN);
        holder.layout.setOnClickListener(view -> {
            row_index = position;
            notifyDataSetChanged();
            listener.onDateCommandSelected(dateCommandeList.get(position));
        });

        if (row_index == position) {
            holder.layout.setBackgroundResource(R.color.colorLightGray);
            listener.onDateCommandSelected(dateCommandeList.get(position));

        } else {
            holder.layout.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        return dateCommandeList.size();
    }


    public class CommandeHolder extends RecyclerView.ViewHolder {
        public TextView orderNumber, quantity, date, sum, dateClosed;
        public LinearLayout layout;

        public CommandeHolder(View view) {
            super(view);
            orderNumber =  view.findViewById(R.id.orderNumber);
            quantity =  view.findViewById(R.id.quantity);
            date =  view.findViewById(R.id.date);
            dateClosed = view.findViewById(R.id.dateclose);
            sum =  view.findViewById(R.id.sum);
            layout = view.findViewById(R.id.layout);

            layout.setOnClickListener(view1 -> listener.onDateCommandSelected(dateCommandeList.get(getAdapterPosition())));


        }
    }


    public interface CommandeListener {
        void onDateCommandSelected(DateCommande commande);


    }
}
