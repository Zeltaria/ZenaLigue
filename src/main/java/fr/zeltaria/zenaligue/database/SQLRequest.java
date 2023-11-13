package fr.zeltaria.zenaligue.database;

import fr.zeltaria.zenaligue.Main;

import java.sql.SQLException;


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
        return (Boolean) Main.getInstance().getMySQL().query("SELECT * FROM zenaligue_match WHERE ligueId = (SELECT * FROM zenaligue_ligue ORDER BY id DESC LIMIT 1)", rs -> {
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
}
