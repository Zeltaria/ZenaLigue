package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.League;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StartNewLeagueCommand {

    private final SlashCommandInteractionEvent event;

    public StartNewLeagueCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void startNewLeague() {
        new League(SQLRequest.getLeagueId() /*+ 1*/);
    }
}
