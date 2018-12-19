package tn.meteor.efficaisse.ui.discounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.DiscountAdapter;
import tn.meteor.efficaisse.model.Discount;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.ui.discountAdd.DiscountAddActivity;
import tn.meteor.efficaisse.ui.discountDetails.DiscountDetailsFragment;
import tn.meteor.efficaisse.utils.ItemOffsetDecoration;

/**
 * Created by lilk on 28/01/2018.
 */

public class DiscountsFragment extends BaseFragment implements DiscountsContract.View, DiscountAdapter.DiscountListener{


    private DiscountsContract.Presenter discountPresenter;
    private RecyclerView discounts;
    private List<Discount> discountList;
    private LinearLayout addDiscountBar;
    private DiscountAdapter discountAdapter;
    private LinearLayout empty;

    public static DiscountsFragment newInstance() {
        DiscountsFragment fragment = new DiscountsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discount, container, false);
        discountPresenter = new DiscountsPresenter(this);
        discounts = v.findViewById(R.id.discounts);
        addDiscountBar = v.findViewById(R.id.addDiscountBar);
        empty = v.findViewById(R.id.empty);
        discountList = new ArrayList<>();


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(v.getContext(), R.dimen.item_product_offset);

        discountAdapter = new DiscountAdapter(getActivity(), this, discountList);
        discounts.addItemDecoration(itemDecoration);
        discounts.setLayoutManager(staggeredGridLayoutManager);
        discounts.setAdapter(discountAdapter);

        discountPresenter.initDicountList(discountList);

        addDiscountBar.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), DiscountAddActivity.class));

        });
        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        discountPresenter.initDicountList(discountList);

    }

    @Override
    public void updateUI() {
        empty.setVisibility(View.GONE);
        discounts.setVisibility(View.VISIBLE);

        if (discountList.isEmpty()) {

            empty.setVisibility(View.VISIBLE);
            discounts.setVisibility(View.GONE);
        }

        discountAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDiscountDetails(Discount discount) {
        DiscountDetailsFragment discountDetailsFragment = new DiscountDetailsFragment();
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String json = gson.toJson(discount);

        bundle.putString("discount", json);

        discountDetailsFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.content_main, discountDetailsFragment).addToBackStack(null).commit();


    }


    @Override
    public void onCommandeSelected(Discount discount) {
        showDiscountDetails(discount);

    }

    @Override
    public void onCommandeLongClicked(Discount discount) {

    }
}
