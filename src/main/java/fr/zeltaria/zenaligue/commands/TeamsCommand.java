package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Team;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TeamsCommand {

    private final SlashCommandInteractionEvent event;

    public TeamsCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void teams() {
        StringBuilder message = new StringBuilder("""
                **__Liste des équipes__**
                """);
        message.append("\n");
        for(Team t : SQLRequest.getTeams()){
            message.append(t.id()).append(" - ").append(t.name()).append(" ").append(t.logo().getEmoji()).append("\n");
        }
        event.getHook().sendMessage(message.toString()).queue();
    }
}
