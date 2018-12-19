package tn.meteor.efficaisse.model;

/**
 * Created by Oussa on 22-Apr-18.
 */

public class Event {

    private String date;
    private String status;
    private String homeTeamName;
    private String awayTeamName;

    private String eventName;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public Event(String date, String status, String homeTeamName, String awayTeamName) {
        this.date = date;
        this.status = status;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
    }

    public Event(String eventName, String date, String status, String homeTeamName, String awayTeamName) {
        this.eventName = eventName;
        this.date = date;
        this.status = status;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
    }

    public Event() {
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayTeamName='" + awayTeamName + '\'' +
                '}';
    }
}
