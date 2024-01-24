package fr.zeltaria.zenaligue.classes;

public class  Match{

    private final int id;
    private final Team team1;
    private final Team team2;
    private int scoret1;
    private int scoret2;

    public Match(int id, Team team1, Team team2, int scoret1, int scoret2) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.scoret1 = scoret1;
        this.scoret2 = scoret2;
    }

    public int id() {
        return id;
    }

    public Team team1() {
        return team1;
    }

    public Team team2() {
        return team2;
    }

    public int scoret1() {
        return scoret1;
    }

    public int scoret2() {
        return scoret2;
    }

    public void addBut(Goal but) {
        if (but.teamId() == team1.id()) {
            scoret1++;
        } else {
            scoret2++;
        }
    }


}
