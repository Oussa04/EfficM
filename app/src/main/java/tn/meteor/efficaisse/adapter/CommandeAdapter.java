package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by lilk on 29/01/2018.
 */

public class CommandeAdapter extends RecyclerView.Adapter<CommandeAdapter.CommandeHolder> {


    private Context context;
    private List<Commande> commandeList;
    public CommandeListener listener;


    public CommandeAdapter(Context context, CommandeListener listener, List<Commande> commandes) {
        this.context = context;
        this.listener = listener;
        this.commandeList = commandes;
    }


    @Override
    public CommandeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_commande, parent, false);

        return new CommandeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommandeHolder holder, int position) {
        Commande commande = commandeList.get(position);
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        holder.orderNumber.setText("Commande N° " + commande.getCommandeNumber());
        DateFormat df2 = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
        holder.date.setText(df2.format(commande.getDate()));
        holder.quantity.setText(detailCommandeRepository.getCommandeQuantity(commande) + " elements");
        holder.sum.setText(String.format("%.3f", detailCommandeRepository.getCommandeSumdiscounted(commande)) + " dt");

        if (!commande.isStatus()) {
            holder.sum.setTextColor(Color.RED);
        }else {
            holder.sum.setTextColor(Color.GREEN);
        }
    }


    @Override
    public int getItemCount() {
        return commandeList.size();
    }


public class CommandeHolder extends RecyclerView.ViewHolder {
    public TextView orderNumber, quantity, date, sum;


    public CommandeHolder(View view) {
        super(view);
        orderNumber = view.findViewById(R.id.orderNumber);
        quantity = view.findViewById(R.id.quantity);
        date = view.findViewById(R.id.date);
        sum = view.findViewById(R.id.sum);

        view.setOnClickListener(view1 -> listener.onCommandeSelected(commandeList.get(getAdapterPosition())));


        view.setOnLongClickListener(v -> {

            listener.onCommandeSelected(commandeList.get(getAdapterPosition()));
            return false;
        });


    }
}


public interface CommandeListener {
    void onCommandeSelected(Commande commande);

    void onCommandeLongClicked(Commande commande);


}
}
