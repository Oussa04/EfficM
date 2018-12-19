package tn.meteor.efficaisse.ui.ingredientDetails;

import com.annimon.stream.DoubleStream;
import com.annimon.stream.Stream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tn.meteor.efficaisse.data.repository.DetailCommandeIngredientRepository;
import tn.meteor.efficaisse.model.DetailCommandeIngredient;
import tn.meteor.efficaisse.model.Ingredient;
import tn.meteor.efficaisse.model.Prediction;
import tn.meteor.efficaisse.ui.base.BasePresenter;


/**
 * Created by lilk on 27/01/2018.
 */

public class IngredientDetailsPresenter extends BasePresenter implements IngredientDetailsContract.Presenter{

    private IngredientDetailsContract.View view;

    public IngredientDetailsPresenter(IngredientDetailsContract.View view) {
        this.view = view;
    }



    @Override
    public Prediction getPredictionByIngredient(Ingredient ingredient) {
        List<DetailCommandeIngredient> ldti = DetailCommandeIngredientRepository.findByIngredient(ingredient);

        DateFormat formatterDate = new SimpleDateFormat("yyyy/MM/dd");


        DateFormat formatterMonth = new SimpleDateFormat("yyyy/MM");
        DateFormat formatterDayOfTheWeek = new SimpleDateFormat("EEEE" ,Locale.FRANCE );
        DateFormat formatterWeek = new SimpleDateFormat("yyyy-'W'ww");
        List<Map.Entry<String, List<DetailCommandeIngredient>>> byDate = Stream.of(ldti).groupBy(detailCommandeIngredient -> formatterDate.format(detailCommandeIngredient.getCommande().getDate())).toList();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date tommorow = calendar.getTime();
        Prediction prediction;
        //*******ByDaysoftheMonth****************
        List<Map.Entry<String, List<DetailCommandeIngredient>>> currentMonth = Stream.of(byDate).filter(entry -> formatterMonth.format(entry.getValue().get(0).getCommande().getDate()).equals(formatterMonth.format(new Date()))).toList();
        int countMonth = currentMonth.size();
        Prediction precByMonth = predict(currentMonth);
        //*******ByDaysoftheWeek****************
        List<Map.Entry<String, List<DetailCommandeIngredient>>> dayOdTheWeek = Stream.of(byDate).filter(entry -> formatterDayOfTheWeek.format(entry.getValue().get(0).getCommande().getDate()).equals(formatterDayOfTheWeek.format(tommorow))).toList();
        int countDayOfTheWeek = dayOdTheWeek.size();
        Prediction precDay = predict(dayOdTheWeek);
        //*******ByDaysoftheWeek****************
        List<Map.Entry<String, List<DetailCommandeIngredient>>> currentWeek = Stream.of(byDate).filter(entry -> formatterWeek.format(entry.getValue().get(0).getCommande().getDate()).equals(formatterWeek.format(new Date()))).toList();
        int countweek = dayOdTheWeek.size();
        Prediction precWeek = predict(currentWeek);


        //********* pick the best***********************

        int count;
        if (((countMonth + 1.0) / (precByMonth.getCount() + 1.0)) < ((countDayOfTheWeek + 1.0) / (precDay.getCount() + 1.0)) && precByMonth.getCount()>2) {
            count = countMonth;
            prediction = precByMonth;
            prediction.setCriteria("l'utilisation du mois courant");
        } else {
            count = countDayOfTheWeek;
            prediction = precDay;

            DateFormat nameday = new SimpleDateFormat("EEE", Locale.FRENCH);

            prediction.setCriteria("l'utilisation des "+ nameday.format(tommorow));
        }
        if ((count + 1.0) / (prediction.getCount() + 1.0) > (countweek + 1.0) / (precWeek.getCount() + 1.0)  && precWeek.getCount()>2) {

            prediction = precWeek;
            prediction.setCriteria("l'utilisation du semaine courante");
        }
        prediction.setIngredient(ingredient);

        return prediction;

    }

    private Prediction predict(List<Map.Entry<String, List<DetailCommandeIngredient>>> array) {
        boolean test = false;

        Prediction prediction = new Prediction();
        prediction.setCount(0);
        prediction.setPrediction(0);


        if (array.isEmpty()) {

            return prediction;
        }
        List<Double> list = new ArrayList<>();
        for (Map.Entry<String, List<DetailCommandeIngredient>> entry :
                array) {
            list.add(getQuantity(entry));
        }
        list = Stream.of(list).sortBy(aDouble -> aDouble).toList();
        Double max = list.get(list.size() - 1);
        Double min = list.get(0);
        if (min.equals(max)) {
            prediction.setCount(list.size());
            prediction.setPrediction(max);
            return prediction;
        }
        double avg = Stream.of(list).mapToDouble(i -> i).sum() / list.size();

        if (errorMargin(avg, max) > 0.5) {
            list.remove(list.size() - 1);
            test = true;
        }
        if (errorMargin(avg, min) > 0.5) {
            list.remove(0);
            test = true;
        }
        if (test) {
            return filter(list);
        }
        prediction.setCount(list.size());
        prediction.setPrediction(max);
        return prediction;


    }

    private Prediction filter(List<Double> array) {
        boolean test = false;
        Prediction prediction = new Prediction();

        prediction.setCount(0);
        prediction.setPrediction(0);
        if (array.isEmpty()) {
            prediction.setCount(0);
            prediction.setPrediction(0);
            return prediction;
        }
        Double max = array.get(array.size() - 1);
        Double min = array.get(0);
        if (min.equals(max)) {
            prediction.setCount(array.size());
            prediction.setPrediction(max);
            return prediction;
        }
        double avg = Stream.of(array).mapToDouble(i -> i).sum() / array.size();

        if (errorMargin(avg, max) > 0.5) {
            array.remove(array.size() - 1);
            test = true;
        }
        if (errorMargin(avg, min) > 0.5) {
            array.remove(0);
            test = true;
        }
        if (test) {
            return filter(array);
        }
        prediction.setCount(array.size());
        prediction.setPrediction(max);
        return prediction;


    }

    private double errorMargin(double avg, double value) {
        return Math.abs(value - avg) / avg;
    }

    private Double getQuantity(Map.Entry<String, List<DetailCommandeIngredient>> entry) {

        return Stream.of(entry.getValue()).flatMapToDouble(detailCommandeIngredient -> DoubleStream.of(Double.valueOf(detailCommandeIngredient.getQuantity()))).sum();
    }


}
