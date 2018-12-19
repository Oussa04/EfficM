package tn.meteor.efficaisse.ui.lounge;

import java.util.List;

import tn.meteor.efficaisse.data.repository.LoungTablesRepository;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.ui.base.BasePresenter;


public class LoungePresenter extends BasePresenter implements LoungeContract.Presenter {


    private LoungeContract.View view;
    private LoungTablesRepository loungTablesRepository;

    public LoungePresenter(LoungeContract.View view) {

        loungTablesRepository = new LoungTablesRepository();

        this.view = view;


    }


    @Override
    public void initTables(List<LoungeTable> tables) {
        tables.clear();


        LoungeTable loungeTable = new LoungeTable();
        loungeTable.setCommande_number(null);
        loungeTable.setLibelle("Comptoir");
        loungeTable.setDescr("Comptoir");
        loungeTable.setId(-1);
        tables.add(loungeTable);
        tables.addAll(loungTablesRepository.findAll());
        view.updateUI();
    }

}
