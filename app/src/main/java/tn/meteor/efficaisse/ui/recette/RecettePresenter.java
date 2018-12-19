package tn.meteor.efficaisse.ui.recette;

import java.util.List;

import tn.meteor.efficaisse.data.repository.DetailCommandeRepository;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.ui.base.BasePresenter;

/**
 * Created by lilk on 20/01/2018.
 */

public class RecettePresenter extends BasePresenter implements RecetteContract.Presenter {


    private RecetteContract.View view;

    public RecettePresenter(RecetteContract.View view) {


        this.view = view;


    }


    @Override
    public void initPaidList(List<Commande> paidList) {
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        paidList.clear();
        paidList.addAll(detailCommandeRepository.findWithStatus(true));
        view.updateUI();
    }

    @Override
    public void initNotPaidList(List<Commande> notPaidList) {
        DetailCommandeRepository detailCommandeRepository = new DetailCommandeRepository();
        notPaidList.clear();
        notPaidList.addAll(detailCommandeRepository.findWithStatus(false));
        view.updateUI();
    }
}
