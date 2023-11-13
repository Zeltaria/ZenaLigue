package fr.zeltaria.zenaligue.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StartNewLeagueCommand {

    private final SlashCommandInteractionEvent event;

    public StartNewLeagueCommand(SlashCommandInteractionEvent event) {
        this.event = event;
        event.getHook().sendMessage("Commande en cours de développement !").queue();
    }

    public void startNewLeague() {

    }
}
