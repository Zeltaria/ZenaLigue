package fr.zeltaria.zenaligue.database;

import fr.zeltaria.zenaligue.Main;
import fr.zeltaria.zenaligue.classes.Goal;
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
        if(!isGuildRegistered(guildId)){
            Main.getInstance().getMySQL().update("INSERT INTO bot (guildId, zenaligueChannelId) VALUES (" + guildId + ", " + textChannelId + ")");
        }
        else {
            Main.getInstance().getMySQL().update("UPDATE bot SET zenaligueChannelId = " + textChannelId + " WHERE guildId = " + guildId);
        }
    }

    public static void removeGuild(String guildId){
        Main.getInstance().getMySQL().update("DELETE FROM bot WHERE guildId = " + guildId);
    }

    public static Boolean isGuildRegistered(String guildId){
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM bot WHERE guildId = " + guildId, rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
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

    @SuppressWarnings("unchecked")
    public static List<Team> getTeams() {
        return (List<Team>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_teams", rs -> {
            List<Team> teams = new ArrayList<>();
            try {
                while (rs.next()) {
                    teams.add(new Team(rs.getInt("id"), rs.getString("name"), getEmoji(rs)));
                }
                return teams;
            } catch (SQLException e) {
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
            case "NORDSTADT" -> ZenaEmojis.NORDSTADT;
            case "CIRA_EVO_CALGIO" -> ZenaEmojis.CIRA_EVO_CALGIO;
            case "DOMULONT" -> ZenaEmojis.DOMULONT;
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    public static List<Player> getPlayersFromTeamId(int teamId) {
        return (List<Player>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_players WHERE teamId = " + teamId, rs -> {
            List<Player> players = new ArrayList<>();
            try {
                while (rs.next()) {
                    PlayerRole role = switch (rs.getString("role")){
                        case "GARDIEN" -> PlayerRole.GARDIEN;
                        case "MILIEU" -> PlayerRole.MILIEU;
                        case "DEFENSEUR" -> PlayerRole.DEFENSEUR;
                        case "ATTAQUANT" -> PlayerRole.ATTAQUANT;
                        default -> null;
                    };
                    players.add(new Player(rs.getInt("id"), rs.getString("name"), role, rs.getInt("jersey")));
                }
                return players;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void addPlayerInTeam(int teamId, Player player) {
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_players (teamId, name, role, jersey) VALUES (" + teamId + ", '" + player.name() + "', '" + player.role().name() + "', " + player.jersey() + ")");
    }

    public static void removePlayerFromTeam(int teamId, int jersey) {
        Main.getInstance().getMySQL().update("DELETE FROM zenaligue_players WHERE teamId = " + teamId + " AND jersey = " + jersey);
    }

    public static Team getTeamFromId(int teamId) {
        return (Team) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_teams WHERE id = " + teamId, rs -> {
            try {
                if (rs.next()) {
                    return new Team(rs.getInt("id"), rs.getString("name"), getEmoji(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static boolean isTeamExist(int teamId) {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_teams WHERE id = " + teamId, rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public static boolean isPlayerExist(int teamId, int jersey) {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_players WHERE teamId = " + teamId + " AND jersey = " + jersey, rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public static Boolean isLastLeagueFinished() {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_ligue ORDER BY id DESC LIMIT 1", rs -> {
            try {
                while(rs.next()) {
                    if(!rs.getBoolean("finished")){
                        return false;
                    }
                }
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    public static int getLastLeagueId() {
        return (Integer) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_ligue ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                else {
                    return 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void createLeague(String name, int id) {
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_ligue VALUES ('" + id + "', '" + name + "', '0')");
    }

    public static void finishLeague() {
        Main.getInstance().getMySQL().update("UPDATE zenaligue_ligue SET finished = 1 WHERE id = " + getLastLeagueId());
    }

    public static String getLastLeagueName() {
        return (String) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_ligue ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getString("name");
                }
                else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Player getPlayer(int teamId, int jersey) {
        return (Player) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_players WHERE teamId = " + teamId + " AND jersey = " + jersey, rs -> {
            try {
                if (rs.next()) {
                    PlayerRole role = switch (rs.getString("role")){
                        case "GARDIEN" -> PlayerRole.GARDIEN;
                        case "MILIEU" -> PlayerRole.MILIEU;
                        case "DEFENSEUR" -> PlayerRole.DEFENSEUR;
                        case "ATTAQUANT" -> PlayerRole.ATTAQUANT;
                        default -> null;
                    };
                    return new Player(rs.getInt("id"), rs.getString("name"), role, rs.getInt("jersey"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Integer getLastDayNumber() {
        return (Integer) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_days ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("number");
                }
                else {
                    return 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public static List<Match> getMatchsFromLastDay() {
        return (List<Match>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_matchs WHERE dayId = " + getLastDayId(), rs -> {
            List<Match> matchs = new ArrayList<>();
            try {
                while (rs.next()) {
                    matchs.add(new Match(rs.getInt("id"), getTeamFromId(rs.getInt("teamLocal")), getTeamFromId(rs.getInt("teamExt")),  rs.getInt("scoreLoc"), rs.getInt("scoreExt")));
                }
                return matchs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Boolean isLastDayFinished() {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_days ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getBoolean("finished");
                }
                else {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Boolean isFirstDayFromLeagueId(int leagueId){
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_days WHERE ligueId = " + leagueId + " ORDER BY id DESC LIMIT 1", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void createNewDay() {
        if(!isFirstDayFromLeagueId(getLastLeagueId())){
            Main.getInstance().getMySQL().update("INSERT INTO zenaligue_days (ligueId, number, finished) VALUES ("+ getLastLeagueId() + ", 1, 0)");
            return;
        }
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_days (ligueId, number, finished) VALUES ("+ getLastLeagueId() + ", " + (getLastDayNumber() + 1) + ", 0)");
    }

    public static void createNewMatch(int team1, int team2) {
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_matchs (teamLocal, teamExt, scoreLoc, scoreExt, dayId) VALUES ("+ team1 + ", "+ team2 +", 0, 0, " + getLastDayId() + ")");
    }

    public static Boolean isMatchExistInLastDay(int team1, int team2) {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_matchs WHERE dayId = " + getLastDayId() + " AND (teamLocal = " + team1 + " AND teamExt = " + team2 + ") OR (teamLocal = " + team2 + " AND teamExt = " + team1 + ")", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Integer getLastDayId() {
        return (Integer) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_days ORDER BY id DESC LIMIT 1", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                else {
                    return 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Boolean isTeamAlreadyPlayedInLastDay(int team1) {
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_matchs WHERE dayId = " + getLastDayId() + " AND (teamLocal = " + team1 + " OR teamExt = " + team1 + ")", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Match getMatchFromId(int match) {
        return (Match) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_matchs WHERE id = " + match, rs -> {
            try {
                if (rs.next()) {
                    return new Match(rs.getInt("id"), getTeamFromId(rs.getInt("teamLocal")), getTeamFromId(rs.getInt("teamExt")),  rs.getInt("scoreLoc"), rs.getInt("scoreExt"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void deleteMatch(int match) {
        Main.getInstance().getMySQL().update("DELETE FROM zenaligue_matchs WHERE id = " + match);
    }

    public static Integer getDayMatchIdIsPlayed(int id) {
        return (Integer) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_matchs WHERE id = " + id, rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("dayId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Player getPlayerFromId(int player) {
        return (Player) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_players WHERE id = " + player, rs -> {
            try {
                if (rs.next()) {
                    PlayerRole role = switch (rs.getString("role")){
                        case "GARDIEN" -> PlayerRole.GARDIEN;
                        case "MILIEU" -> PlayerRole.MILIEU;
                        case "DEFENSEUR" -> PlayerRole.DEFENSEUR;
                        case "ATTAQUANT" -> PlayerRole.ATTAQUANT;
                        default -> null;
                    };
                    return new Player(rs.getInt("id"), rs.getString("name"), role, rs.getInt("jersey"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Integer getPlayerTeamId(Integer id) {
        return (Integer) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_players WHERE id = " + id, rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("teamId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void addGoal(int matchId, int teamId, Player buteur, int minute, int csc) {
        Main.getInstance().getMySQL().update("INSERT INTO zenaligue_goals (matchId, teamId, playerId, minute, csc) VALUES (" + matchId + ", " + teamId + ", " + buteur.id() + ", " + minute + ", " + csc + ")");
    }

    @SuppressWarnings("unchecked")
    public static List<Goal> getGoalsFromMatch(int matchId) {
        return (List<Goal>) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_goals WHERE matchId = " + matchId + " ORDER BY minute ASC", rs -> {
            List<Goal> goals = new ArrayList<>();
            try {
                while (rs.next()) {
                    goals.add(new Goal(rs.getInt("id"), rs.getInt("matchId"), rs.getInt("teamId"), rs.getInt("playerId"), rs.getString("minute"), rs.getInt("csc")));
                }
                return goals;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static Goal getGoalFromId(int goalId) {
        return (Goal) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_goals WHERE id = " + goalId, rs -> {
            try {
                if (rs.next()) {
                    return new Goal(rs.getInt("id"), rs.getInt("matchId"), rs.getInt("teamId"), rs.getInt("playerId"), rs.getString("minute"), rs.getInt("csc"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public static void deleteGoal(int goalId) {
        Main.getInstance().getMySQL().update("DELETE FROM zenaligue_goals WHERE id = " + goalId);
    }
}
