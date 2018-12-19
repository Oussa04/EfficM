package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.data.repository.DiscountRepository;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.model.Product;
import tn.meteor.efficaisse.utils.Constants;

/**
 * Created by lilk on 29/01/2018.
 */

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.CommandeHolder> {


    private Context context;
    private List<Discount> discountList;
    public DiscountListener listener;


    public DiscountAdapter(Context context, DiscountListener listener, List<Discount> discountList) {
        this.context = context;
        this.listener = listener;
        this.discountList = discountList;
    }


    @Override
    public CommandeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discount, parent, false);

        return new CommandeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommandeHolder holder, int position) {
        Discount discount = discountList.get(position);
        DiscountRepository discountRepository = new DiscountRepository();
        holder.name.setText(discount.getName());
        DateFormat df2 = new SimpleDateFormat(Constants.DATE_FORMAT);
        holder.dateBegin.setText("de "+df2.format(discount.getDatebegin())+" jusqu'au " + df2.format(discount.getDateend()));

        holder.discount.setText("-" + String.format("%.2f",discount.getDiscount()) + "%");
        holder.dateEnd.setVisibility(View.GONE);


        DateFormat df3 = new SimpleDateFormat(Constants.DATE_FORMAT);

        String inputDateString = df3.format(discount.getDateend());
        String inputDateString2 = df3.format(discount.getDatebegin());
        Calendar calCurr = Calendar.getInstance();
        Calendar dayEnd = Calendar.getInstance();
        Calendar dayBegin = Calendar.getInstance();
        try {
            dayEnd.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(inputDateString));
            dayBegin.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(inputDateString2));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dayEnd.after(calCurr)) {
            holder.etat.setText("En cours");
        } else {
            holder.etat.setText("Finie");
        }
        if (dayBegin.after(calCurr)) {
            holder.etat.setText("Pas encore");


        }
        if (discount.getProductList() != null) {
            String s = "";
            int i = 0;
            int j = 0;
            for (Product product : discount.getProductList()) {
                i = i + 1;
                if (i < 3) {
                    s = s + product.getName() + ", ";
                } else {
                    j = j + 1;
                }
            }
            if (j > 0) {
                holder.categories.setText(s + "... et " + j + " autres.");
            } else {

                holder.categories.setText(s );
            }
        } else {
            holder.categories.setVisibility(View.GONE);


        }
    }


    @Override
    public int getItemCount() {
        return discountList.size();
    }


    public class CommandeHolder extends RecyclerView.ViewHolder {
        public TextView etat, name, discount, dateBegin, dateEnd, categories;


        public CommandeHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            discount = (TextView) view.findViewById(R.id.discount);
            dateBegin = (TextView) view.findViewById(R.id.dateBegin);
            dateEnd = (TextView) view.findViewById(R.id.dateEnd);
            categories = (TextView) view.findViewById(R.id.categories);
            etat = (TextView) view.findViewById(R.id.etat);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onCommandeSelected(discountList.get(getAdapterPosition()));
                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    listener.onCommandeSelected(discountList.get(getAdapterPosition()));
                    return false;
                }
            });


        }
    }


    public interface DiscountListener {
        void onCommandeSelected(Discount discount);

        void onCommandeLongClicked(Discount discount);


    }
}
