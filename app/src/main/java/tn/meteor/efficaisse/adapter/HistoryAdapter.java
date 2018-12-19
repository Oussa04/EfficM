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
import tn.meteor.efficaisse.model.History;

/**
 * Created by lilk on 29/01/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {


    private Context context;
    private List<History> historyList;
    public HistoryListener listener;
    public String unit;
    private int row_index = 0;


    public HistoryAdapter(Context context, HistoryListener listener, List<History> historyList, String unit) {
        this.context = context;
        this.listener = listener;
        this.historyList = historyList;
        this.unit = unit;
    }


    @Override
    public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryHolder holder, int position) {
        History history = historyList.get(position);
        holder.quantity.setTextColor(Color.GREEN);
        if (history.getQuantity() > 0) {
            holder.quantity.setText("+" + String.format("%.2f",history.getQuantity()) + " " + unit);
        } else {
            holder.quantity.setText(String.format("%.2f",history.getQuantity()) + " " + unit);
            holder.quantity.setTextColor(Color.RED);
        }

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText(formatter.format(history.getDate()));
        holder.libelle.setText(history.getUsername());
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }


    public class HistoryHolder extends RecyclerView.ViewHolder {
        public TextView libelle, quantity, date;
        public LinearLayout layout;

        public HistoryHolder(View view) {
            super(view);
            libelle = (TextView) view.findViewById(R.id.libelle);
            quantity = (TextView) view.findViewById(R.id.quantity);
            date = (TextView) view.findViewById(R.id.date);
            layout = (LinearLayout) view.findViewById(R.id.layout);

            layout.setOnClickListener(view1 -> listener.onHistorySelected(historyList.get(getAdapterPosition())));


        }
    }


    public interface HistoryListener {
        void onHistorySelected(History history);


    }
}
