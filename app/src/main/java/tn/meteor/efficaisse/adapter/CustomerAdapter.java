package tn.meteor.efficaisse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.model.Customer;
import tn.meteor.efficaisse.utils.SquareImageView;


public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {

    private Context mContext;

    private List<Customer> customerList;
    public CustomerAdpaterListener listener;


    public class CustomerHolder extends RecyclerView.ViewHolder {

        public ImageView overflow;
        public ProgressBar mProgressBar;
        public IconTextView line1, line2;
        public SquareImageView thumbnail;
        public ImageView gradient;
        public TextView firstLetter;


        public CustomerHolder(View view) {
            super(view);
            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
            line1 = (IconTextView) view.findViewById(R.id.line1);
            line2 = (IconTextView) view.findViewById(R.id.line2);
            gradient = (ImageView) view.findViewById(R.id.gradient);
            firstLetter = (TextView) view.findViewById(R.id.firstLetter);


            gradient.setOnClickListener(view1 -> listener.onCustomerSelected(customerList.get(getAdapterPosition())));

        }
    }

    public CustomerAdapter(Context mContext, CustomerAdpaterListener listener, List<Customer> customerList) {
        this.mContext = mContext;
        this.listener = listener;
        this.customerList = customerList;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);

        return new CustomerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerHolder holder, int position) {


        Customer customer = customerList.get(position);
        holder.line1.setVisibility(View.GONE);
        holder.line2.setText(customer.getName());
        holder.firstLetter.setText(customer.getName().substring(0,1));








    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }


    public interface CustomerAdpaterListener {
        void onCustomerSelected(Customer customer);

        void onCustomerLongClick(Customer customer);
    }



}
