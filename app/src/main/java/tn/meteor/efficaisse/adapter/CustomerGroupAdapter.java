package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.CustomerGroup;
import tn.meteor.efficaisse.utils.SquareImageView;


public class CustomerGroupAdapter extends RecyclerView.Adapter<CustomerGroupAdapter.CustomerGroupHolder> {

    private Context mContext;
    private int row_index = 0;
    private List<CustomerGroup> customerGroupList;
    public CustomerGroupAdapterListener listener;
    int selected_position = 0;

    public class CustomerGroupHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ProgressBar mProgressBar;
        public IconTextView line1, line2;
        public SquareImageView thumbnail;
        public ImageView gradient;
        public TextView firstLetter;


        public CustomerGroupHolder(View view) {
            super(view);
            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
            line1 = (IconTextView) view.findViewById(R.id.line1);
            line2 = (IconTextView) view.findViewById(R.id.line2);
            gradient = (ImageView) view.findViewById(R.id.gradient);
            firstLetter = view.findViewById(R.id.firstLetter);
            gradient.setOnClickListener(view1 -> listener.onGroupSelected(customerGroupList.get(getAdapterPosition())));

        }
    }

    public CustomerGroupAdapter(Context mContext, CustomerGroupAdapterListener listener, List<CustomerGroup> customerGroupList) {
        this.mContext = mContext;
        this.listener = listener;
        this.customerGroupList = customerGroupList;
    }

    @Override
    public CustomerGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);

        return new CustomerGroupHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerGroupHolder holder, int position) {


        CustomerGroup customerGroup = customerGroupList.get(position);
        holder.line1.setText(customerGroup.getName());
        holder.line2.setText(String.format("%.2f",customerGroup.getDiscount()) + "%");
//        holder.thumbnail.setBackgroundColor(getRandomMaterialColor("400"));
        holder.firstLetter.setText(customerGroup.getName().substring(0, 1));
        holder.gradient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                listener.onGroupSelected(customerGroupList.get(position));
            }
        });


        if (customerGroup.getName().equals(mContext.getResources().getString(R.string.sans_groupe))) {
            holder.firstLetter.setVisibility(View.GONE);
            Picasso.with(mContext).load(R.drawable.circumference).fit().into(holder.thumbnail);

            holder.line2.setVisibility(View.GONE);
        }


        if (row_index == position) {
            holder.gradient.setBackgroundResource(R.color.colorAccent);
            listener.onGroupSelected(customerGroupList.get(position));

        } else {
            holder.gradient.setBackgroundColor(Color.TRANSPARENT);
        }


    }

    @Override
    public int getItemCount() {
        return customerGroupList.size();
    }


    public interface CustomerGroupAdapterListener {
        void onGroupSelected(CustomerGroup customerGroup);

        void onGroupLongClickSelected(CustomerGroup customerGroup);
    }


}
