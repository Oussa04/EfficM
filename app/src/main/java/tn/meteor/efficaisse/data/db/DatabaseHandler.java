package tn.meteor.efficaisse.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tn.meteor.efficaisse.data.network.Statistics;


/**
 * Created by SKIIN on 10/03/2018
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "efficaisse-db.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }




    public int commandeNotPayed(){


        int ticketNotPayed = 0;
        String selectQuery = "SELECT COUNT(commandeNumber) AS number FROM Commande WHERE status=0 GROUP BY date";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ticketNotPayed = cursor.getInt(cursor.getColumnIndex("number"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ticketNotPayed;

    }

    public List<Statistics> tableProductQunatity(){

        List<Statistics> statistics = new ArrayList<>();
        String selectQuery = "SELECT SUM(quantity)*price AS montant,SUM(quantity) as quantity,DetailCommande.productName as product,DetailCommande.productId as id " +
        "FROM DetailCommande GROUP BY trim(DetailCommande.productName);";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {



                Statistics statistic = new Statistics();
                statistic.setProductName(cursor.getString(cursor.getColumnIndex("product")));
                statistic.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                statistic.setMontant(cursor.getFloat(cursor.getColumnIndex("montant")));
                statistic.setId(cursor.getInt(cursor.getColumnIndex("id")));
                statistics.add(statistic);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return statistics;

    }





    public int ticketNumber(){

        int ticketNumber = 0;
        String selectQuery = "SELECT COUNT(DISTINCT commande) AS number FROM Payment";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ticketNumber = cursor.getInt(cursor.getColumnIndex("number"));
            } while (cursor.moveToNext());
        }
        cursor.close();
       return ticketNumber;
    }



    public List<Statistics> lineChartRecette(){

        List<Statistics> statistics = new ArrayList<>();
        String selectQuery = "SELECT SUM(montant*quantity) as montant,Commande.date as dateRecette " +
                "FROM Payment INNER JOIN Commande ON Payment.commande = Commande.commandeNumber GROUP BY " +
                "Commande.date ORDER BY Commande.date ASC;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Statistics statistic = new Statistics();
                statistic.setMontant(cursor.getFloat(cursor.getColumnIndex("montant")));
                statistic.setDate(new Date(cursor.getLong(cursor.getColumnIndex("dateRecette"))));
                statistics.add(statistic);

            } while (cursor.moveToNext());
        }
        cursor.close();
      return statistics;
    }


    public List<Statistics> pieChartCategory() {
        List<Statistics> statistics = new ArrayList<>();
        String selectQuery = "SELECT SUM(quantity) AS quantity,Product.category as category " +
                "FROM DetailCommande INNER JOIN Commande ON DetailCommande.commandeNumber = Commande.commandeNumber INNER JOIN  " +
                "Product ON trim(DetailCommande.productName)=trim(Product.name) " +
                "GROUP BY trim(Product.category);";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Statistics statistic = new Statistics();
                statistic.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                statistic.setCategory(cursor.getString(cursor.getColumnIndex("category")));
                statistics.add(statistic);
                 } while (cursor.moveToNext());
        }
                  cursor.close();
                  return statistics;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}