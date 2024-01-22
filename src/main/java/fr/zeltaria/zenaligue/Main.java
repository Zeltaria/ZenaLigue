package fr.zeltaria.zenaligue;

import fr.zeltaria.zenaligue.commands.CommandManager;
import fr.zeltaria.zenaligue.database.MySQL;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.security.auth.login.LoginException;

public class Main {

    private final ShardManager shardManager;
    private final Dotenv config;
    private MySQL mysql;
    private static Main main;

    public Main() throws LoginException {
        config = Dotenv.configure().load();
        main = this;
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"));
        initConnection();
        shardManager = builder.setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("la ZenaLigue"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS
                )
                .build();

        shardManager.addEventListener(new CommandManager());
        System.out.println("Bot connecté!");
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (LoginException e) {
            System.out.println("ERREUR: token invalide!");
        }
    }

    private void initConnection() {
        BasicDataSource connectionPool = new BasicDataSource();
        connectionPool.setDriverClassName(config.get("MYSQL_DRIVER"));
        connectionPool.setUsername(config.get("MYSQL_USER"));
        connectionPool.setPassword(config.get("MYSQL_PASSWORD"));
        connectionPool.setUrl("jdbc:mysql://" + config.get("MYSQL_HOST") +":"+config.get("MYSQL_PORT")+"/" + config.get("MYSQL_DATABASE") + "?autoReconnect=true");
        connectionPool.setInitialSize(1);
        connectionPool.setMaxTotal(10);
        mysql = new MySQL(connectionPool);
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public Dotenv getConfig() {
        return config;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public static Main getInstance() {
        return main;
    }
}