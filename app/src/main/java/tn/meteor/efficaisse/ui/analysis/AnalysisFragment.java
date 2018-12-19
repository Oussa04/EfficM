

package tn.meteor.efficaisse.ui.analysis;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.AnalysisAdapter;
import tn.meteor.efficaisse.model.Prediction;
import tn.meteor.efficaisse.ui.base.BaseFragment;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

public class AnalysisFragment extends BaseFragment implements AnalysisContract.View {
    private AnalysisContract.Presenter presenter;
    private RecyclerView recyclerView;
    private List<Prediction> predictions;
    private AnalysisAdapter adapter;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        presenter = new AnalysisPresenter(this);
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        recyclerView = view.findViewById(R.id.list);
        predictions = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new TicketDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 0));
        adapter = new AnalysisAdapter(predictions);
        recyclerView.setAdapter(adapter);
        searchView = view.findViewById(R.id.search);
        presenter.getPredictions();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return view;
    }

    @Override
    public void updateUI(List<Prediction> pred) {
        this.predictions.clear();
        this.predictions.addAll(pred);
        adapter.notifyData();
    }
}

