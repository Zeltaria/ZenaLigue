package fr.zeltaria.zenaligue.classes;

import fr.zeltaria.zenaligue.database.SQLRequest;
import org.javatuples.Pair;

import java.sql.Timestamp;
import java.util.*;

public class League {

    private final int id;
    private final List<Match> matchs;
    public League(int id){
        this.id = id;
        this.matchs = SQLRequest.getMatchsFromLeagueId(id);
        if(matchs.isEmpty()){
            createMatches(id);
        }
    }

    private void createMatches(int id) {
        List<Team> teams = SQLRequest.getTeams();
        List<Pair<Team,Team> > matches = new ArrayList<>();
        boolean[][] played = new boolean[teams.size()][teams.size()];
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < teams.size(); j++) {
                if (i != j && !played[i][j]) {
                    matches.add(new Pair<>(teams.get(i), teams.get(j)));
                    played[i][j] = true;
                }
            }
        }
        for (Pair<Team, Team> match : matches) {
            SQLRequest.addMatch(id, match.getValue0(), match.getValue1());
        }

    }

    public int getId() {
        return id;
    }

    public List<Match> getMatchs() {
        return matchs;
    }
}
