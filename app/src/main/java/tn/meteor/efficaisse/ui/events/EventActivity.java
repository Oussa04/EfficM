package tn.meteor.efficaisse.ui.events;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import tn.meteor.efficaisse.R;
import tn.meteor.efficaisse.adapter.EventAdapter;
import tn.meteor.efficaisse.model.Event;
import tn.meteor.efficaisse.service.ServiceEvent;
import tn.meteor.efficaisse.service.ServiceFactory;
import tn.meteor.efficaisse.ui.base.BaseActivity;
import tn.meteor.efficaisse.utils.TicketDividerItemDecoration;

/**
 * Created by Oussa on 22-Apr-18.
 */

public class EventActivity extends BaseActivity implements  EventAdapter.EventAdapterListener {

    private RecyclerView events;
    private EventAdapter eventAdapter;
    public Event event;
    public  ArrayList<Event> allEvents = new ArrayList<Event>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        events = findViewById(R.id.events);
        getFixturesForWC();
        getFixturesForCL();
        FixedEvents();
        eventAdapter = new EventAdapter(getApplicationContext(), this, allEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        events.setLayoutManager(mLayoutManager);
        events.setItemAnimator(new DefaultItemAnimator());
        events.addItemDecoration(new TicketDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));

        events.setAdapter(eventAdapter);



    }




    public void getFixturesForWC(){

        ServiceEvent serviceEvent = ServiceFactory.getApiClient().create(ServiceEvent.class);
        Observable<JsonObject> getFixtures = serviceEvent.getWorldcupFixtures();

        getFixtures.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JsonObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JsonObject jsonObject) {
                   // Log.e("event",jsonObject.toString());

                JsonArray fixturesWC = jsonObject.getAsJsonArray("fixtures");

                Log.e("FIXTURE WC",fixturesWC.toString());
                for (int i=0; i<fixturesWC.size(); i++) {

                    JsonObject fixture = fixturesWC.get(i).getAsJsonObject();
                    if(!fixture.get("status").toString().contains("FINISHED") ){
                        event = new Event();
                        event.setEventName("World Cup");
                        event.setDate(fixture.get("date").toString().replace("T", " ").replace("Z",""));
                        event.setStatus(fixture.get("status").toString());
                        if(fixturesWC.get(i).getAsJsonObject().get("homeTeamName").toString().equals("\"\"") ){
                            event.setHomeTeamName("Inconnue");
                            event.setAwayTeamName("Inconnue");
                        }
                        else {
                            event.setHomeTeamName(fixture.get("homeTeamName").toString());
                            event.setAwayTeamName(fixture.get("awayTeamName").toString());
                        }
                        allEvents.add(event);
                    }



                }

                Log.e("FIX NULL ",fixturesWC.get(55).toString());
                Log.e("FIX 55 NULL ",allEvents.get(55).toString());

            }

            @Override
            public void onError(Throwable e) {
               // Log.e("event",e.getMessage());
            }

            @Override
            public void onComplete() {




            }
        });


    }

    public void getFixturesForCL(){
        ServiceEvent serviceEvent = ServiceFactory.getApiClient().create(ServiceEvent.class);
        Observable<JsonObject> getCLFixtures = serviceEvent.getChampionsLeagueFixtures();

        getCLFixtures.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<JsonObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JsonObject jsonObject) {
                JsonArray fixturesCL = jsonObject.getAsJsonArray("fixtures");

                Log.e("FIXTURE CL",fixturesCL.toString());

                for (int i=0; i<fixturesCL.size(); i++) {
                    JsonObject fixture = fixturesCL.get(i).getAsJsonObject();

                    if(!fixture.get("status").toString().contains("FINISHED") ){
                        event = new Event();
                        event.setEventName("Champions League");
                        event.setDate(fixture.get("date").toString().replace("T", " ").replace("Z",""));
                        event.setStatus(fixture.get("status").toString());
                        event.setHomeTeamName(fixture.get("homeTeamName").toString());
                        event.setAwayTeamName(fixture.get("awayTeamName").toString());

                        allEvents.add(event);

                    }
                }
            }

            @Override
            public void onError(Throwable e) {
               // Log.e("event",e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e("GET FIXTURES  = ", allEvents.toString());
            }
        });
    }

    public void FixedEvents(){
        /*event = new Event("Saint Valentin", "14/02/2019 15:00:00","A venir");
        event = new Event("Saint Valentin", "14/02/2020 15:00:00","A venir");
        event = new Event("Saint Valentin", "14/02/2021 15:00:00","A venir");
        allEvents.add(event);*/
    }


    @Override
    public void onEventSelected(Event event) {

    }

    @Override
    public void onEventLongClickSelected(Event event) {

    }
}
