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
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.model.DetailCommande;


public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketHolder> {

    private Context mContext;
    private List<DetailCommande> detailCommandeList;
    public TicketListener listener;
    public boolean showButtons;
    private Customer customer;


    public class TicketHolder extends RecyclerView.ViewHolder {
        public TextView productname, total, singleprice;

        public CircleButton plus, minus, remove;
        public TextView quantity;

        public TicketHolder(View view) {
            super(view);
            productname = (TextView) view.findViewById(R.id.productname);
            total = (TextView) view.findViewById(R.id.total);
            singleprice = (TextView) view.findViewById(R.id.singleprice);
            quantity = (TextView) view.findViewById(R.id.quantity);
            plus = (CircleButton) view.findViewById(R.id.plus);
            minus = (CircleButton) view.findViewById(R.id.minus);
            remove = (CircleButton) view.findViewById(R.id.remove);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onTicketSelected(detailCommandeList.get(getAdapterPosition()));
//                }
//            });
//
//
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    listener.onTicketLongClickSelected(detailCommandeList.get(getAdapterPosition()));
//                    return false;
//                }
//            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPlusSelected(detailCommandeList.get(getAdapterPosition()));
                }
            });
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMinusSelected(detailCommandeList.get(getAdapterPosition()));
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemDeleted(detailCommandeList.get(getAdapterPosition()));
                }
            });

            if (!showButtons) {
                plus.setVisibility(View.GONE);
                minus.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);


            }

        }
    }

    public TicketAdapter(Context mContext, TicketListener listener, List<DetailCommande> detailCommandeList, boolean showButtons) {
        this.mContext = mContext;
        this.listener = listener;
        this.detailCommandeList = detailCommandeList;
        this.showButtons = showButtons;
    }

    @Override
    public TicketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);

        return new TicketHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TicketHolder holder, int position) {
        DetailCommande ticket = detailCommandeList.get(position);
        holder.productname.setText(ticket.getProduct().getName() + " - x" + ticket.getQuantity());
        holder.singleprice.setText( String.format("%.3f",ticket.getProduct().getDiscounted(customer)) + " DT.");
        holder.total.setText(String.format("%.3f",ticket.getProduct().getDiscounted(customer) * ticket.getQuantity()) + " DT.");

        holder.quantity.setText(ticket.getQuantity() + "");
        if (!showButtons){
            holder.quantity.setText("Quantité: "+ticket.getQuantity() + "");

        }
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int getItemCount() {
        return detailCommandeList.size();
    }


    public interface TicketListener {
        void onTicketSelected(DetailCommande detailCommande);

        void onTicketLongClickSelected(DetailCommande detailCommande);

        void onPlusSelected(DetailCommande detailCommande);

        void onMinusSelected(DetailCommande detailCommande);

        void onItemDeleted(DetailCommande detailCommande);
    }

}
