package fr.zeltaria.zenaligue.database;

import fr.zeltaria.zenaligue.Main;
import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.classes.Team;
import fr.zeltaria.zenaligue.enums.PlayerRole;
import fr.zeltaria.zenaligue.enums.ZenaEmojis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SQLRequest {

    public static void addGuild(String guildId, String textChannelId){
        Main.getInstance().getMySQL().update("INSERT INTO bot (guildId, zenaligueChannelId) VALUES (" + guildId + ", " + textChannelId + ")");
    }

    public static void removeGuild(String guildId){
        Main.getInstance().getMySQL().update("DELETE FROM bot WHERE guildId = " + guildId);
    }

    public static String getTextChannelIdFromGuildId(String guildId){
        return (String) Main.getInstance().getMySQL().query("SELECT * FROM bot WHERE guildId = " + guildId, rs -> {
            try {
                if (rs.next()) {
                    return rs.getString("zenaligueChannelId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static int getLeagueId(){
        return (int) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_ligue ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Boolean isLastLeagueFinished(){
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_match WHERE ligueId = (SELECT id FROM zenaligue_ligue ORDER BY id DESC LIMIT 1)", rs -> {
            try {
                while(rs.next()) {
                    if(!rs.getBoolean("isPlayed")){
                        return false;
                    }
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static List<Match> getMatchsFromLeagueId(int id){
        return (List<Match>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_match WHERE ligueId = " + id, rs -> {
            List<Match> matches = new ArrayList<>();
            try{
                while(rs.next()){
                    matches.add(new Match(getTeamFromId(rs.getInt("localTeamId")), getTeamFromId(rs.getInt("extTeamId")), rs.getTimestamp("startAt")));
                }
                return matches;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        });

    }

    public static Team getTeamFromId(int id){
        return (Team) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_team WHERE id = " + id, rs -> {
            try{
                if(rs.next()){
                    return new Team(id, rs.getString("name"), rs.getString("shortName"), getPlayersFromTeamId(id), getEmoji(rs));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static List<Player> getPlayersFromTeamId(int id){
        return (List<Player>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_player WHERE teamId = " + id, rs->{
            List<Player> players = new ArrayList<>();
            try{
                while(rs.next()){
                    PlayerRole role = switch (rs.getString("poste")){
                        case "GARDIEN" -> PlayerRole.GARDIEN;
                        case "MILIEU" -> PlayerRole.MILIEU;
                        case "DEFENSEUR" -> PlayerRole.DEFENSEUR;
                        case "ATTAQUANT" -> PlayerRole.ATTAQUANT;
                        default -> null;
                    };
                    players.add(new Player(rs.getString("name"), role, rs.getInt("note"), rs.getInt("number")));
                }
                return players;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static List<Team> getTeams(){
        return (List<Team>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_team", rs->{
            List<Team> teams = new ArrayList<>();
            try{
                while(rs.next()){
                    teams.add(new Team(rs.getInt("id"), rs.getString("name"), rs.getString("shortName"), getPlayersFromTeamId(rs.getInt("id")), getEmoji(rs)));
                }
                return teams;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        });
    }

    private static ZenaEmojis getEmoji(ResultSet rs) throws SQLException {
        return switch (rs.getString("logo")) {
            case "MERILAC" -> ZenaEmojis.MERILAC;
            case "GENRIO" -> ZenaEmojis.GENRIO;
            case "AGUERA" -> ZenaEmojis.AGUERA;
            case "SEACITY" -> ZenaEmojis.SEACITY;
            case "BERILAC" -> ZenaEmojis.BERILAC;
            case "MIRABOLA" -> ZenaEmojis.MIRABOLA;
            case "NARTA" -> ZenaEmojis.NARTA;
            case "ROSLEG_SPARTIA" -> ZenaEmojis.ROSLEG_SPARTIA;
            case "MARONIS" -> ZenaEmojis.MARONIS;
            case "NORDSTATD" -> ZenaEmojis.NORDSTATD;
            case "CIRA_EVO_CALGIO" -> ZenaEmojis.CIRA_EVO_CALGIO;
            case "DOMULONT" -> ZenaEmojis.DOMULONT;
            default -> null;
        };
    }

    public static void addMatch(int id, Team locale, Team ext) {
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_match (ligueId, localTeamId, extTeamId, isPlayed)" +
                " VALUES (" + id + ", " + locale.id() + ", " + ext.id() + ", 0)");
    }
}
