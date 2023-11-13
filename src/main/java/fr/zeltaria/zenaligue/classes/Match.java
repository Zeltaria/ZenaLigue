package fr.zeltaria.zenaligue.classes;

import fr.zeltaria.zenaligue.database.SQLRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Match {

    private final int ligueId;
    private final Team locale;
    private final Team visiteur;
    private final List<But> butsLocale;
    private final List<But> butsVisiteur;
    private boolean isPlayed = false;
    private final Timestamp date;
    private int minute = 0;

    public Match(Team locale, Team visiteur, Timestamp date) {
        this.ligueId = SQLRequest.getLeagueId();
        this.locale = locale;
        this.visiteur = visiteur;
        this.butsLocale = new ArrayList<>();
        this.butsVisiteur = new ArrayList<>();
        this.date = date;
    }

    public void addMinute() {
        this.minute++;
    }

    public int getMinute() {
        return this.minute;
    }

    public void addButLocale(But but) {
        this.butsLocale.add(but);
    }

    public void addButVisiteur(But but) {
        this.butsVisiteur.add(but);
    }

    public int getLigueId() {
        return ligueId;
    }

    public Team getLocale() {
        return locale;
    }

    public Team getVisiteur() {
        return visiteur;
    }

    public List<But> getButsLocale() {
        return butsLocale;
    }

    public List<But> getButsVisiteur() {
        return butsVisiteur;
    }

    public int getScoreLocale() {
        return butsLocale.size();
    }

    public int getScoreVisiteur() {
        return butsVisiteur.size();
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public Timestamp getDate() {
        return date;
    }
}
