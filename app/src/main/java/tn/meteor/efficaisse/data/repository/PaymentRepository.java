package tn.meteor.efficaisse.data.repository;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import tn.meteor.efficaisse.data.db.LogSystem;
import tn.meteor.efficaisse.model.Commande;
import tn.meteor.efficaisse.model.Commande_Table;
import tn.meteor.efficaisse.model.ContreBon;
import tn.meteor.efficaisse.model.LoungeTable;
import tn.meteor.efficaisse.model.Payment;
import tn.meteor.efficaisse.model.Payment_Table;

/**
 * Created by lilk on 10/02/2018.
 */

public class PaymentRepository {

    public Payment find(Payment payment) {
        return SQLite.select().from(Payment.class).where(Payment_Table.id.eq(payment.getId())).querySingle();

    }


    public List<Payment> findAll() {
        return SQLite.select().from(Payment.class).queryList();

    }

    public List<Payment> findPaymentsByCommande(Commande commande) {
        return SQLite.select().from(Payment.class).where(Payment_Table.commande.eq(commande)).queryList();

    }


    private void save(Payment payment) {
        if (payment != null) {
            payment.save();
        }
    }

    public void save(List<Payment> paymentList, Commande commande, List<ContreBon> contreBons) {
        Commande c = SQLite.select().from(Commande.class).where(Commande_Table.commandeNumber.eq(commande.getCommandeNumber())).querySingle();
        for (Payment payment : paymentList) {


            payment.setCommande(c);
            save(payment);

        }

        if (!contreBons.isEmpty()) {
            for (ContreBon contreBon : contreBons) {

                ContreBonRepository.update(contreBon);

            }
        }
        if (commande.getTableList() != null) {
LoungTablesRepository loungTablesRepository = new LoungTablesRepository();
            for (LoungeTable loungeTable : commande.getTableList()) {
loungeTable.setCommande_number(null);
                loungTablesRepository.update(loungeTable);

            }
        }
        c.setStatus(true);
        c.update();
        LogSystem.persist(c, "update", "Commande", false);
    }

    public void delete(Payment payment) {
        if (payment != null) {
            payment.delete();
        }
    }


}
