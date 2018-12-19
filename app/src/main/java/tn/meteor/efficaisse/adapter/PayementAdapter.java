package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.markushi.ui.CircleButton;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Payment;

/**
 * Created by lilk on 29/01/2018.
 */

public class PayementAdapter extends RecyclerView.Adapter<PayementAdapter.PayementHolder> {


    private Context context;
    private List<Payment> payementList;
    public PayementListener listener;


    public PayementAdapter(Context context, PayementListener listener, List<Payment> payements) {
        this.context = context;
        this.listener = listener;
        this.payementList = payements;
    }


    @Override
    public PayementAdapter.PayementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payement, parent, false);

        return new PayementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PayementAdapter.PayementHolder holder, int position) {
        Payment payement = payementList.get(position);


        switch (payement.getType()) {
            case "BON":
                holder.name.setText(payement.getType().toString() + " - " + String.format("%.3f",payement.getMontant()) + " dt");
                holder.description.setText("x " + payement.getQuantity());
                holder.value.setText(String.format("%.3f",payement.getQuantity() * payement.getMontant())+" dt");
                break;
            case "CONTREBON":
                holder.name.setText(payement.getType().toString() + " - " +  String.format("%.3f",payement.getMontant()) + " dt");
                holder.description.setText("");
                holder.value.setText(String.format("%.3f",payement.getQuantity() * payement.getMontant())+" dt");
                break;
            case "CHEQUE":
                holder.name.setText(payement.getType().toString());
                holder.description.setText("N°: " + payement.getCommentaire());
                holder.value.setText(String.format("%.3f",payement.getMontant()) +" dt");
                break;
            case "ESPECE":
                holder.name.setText(payement.getType().toString());
                holder.value.setText(String.format("%.3f",payement.getMontant() )+ " dt");
                holder.description.setText("");
                break;


        }
    }

    @Override
    public int getItemCount() {
        return payementList.size();
    }


    public class PayementHolder extends RecyclerView.ViewHolder {
        public TextView name, value, description;

        public CircleButton remove;

        public PayementHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            value = (TextView) view.findViewById(R.id.value);
            description = (TextView) view.findViewById(R.id.description);

            remove = (CircleButton) view.findViewById(R.id.remove);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPayementItemSelected(payementList.get(getAdapterPosition()));
                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    listener.onPayementItemSelected(payementList.get(getAdapterPosition()));
                    return false;
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteSelected(payementList.get(getAdapterPosition()));
                }
            });

        }
    }


    public interface PayementListener {
        void onPayementItemSelected(Payment payement);

        void onPayementItemLongClickSelected(Payment payement);

        void onDeleteSelected(Payment payement);

    }
}
