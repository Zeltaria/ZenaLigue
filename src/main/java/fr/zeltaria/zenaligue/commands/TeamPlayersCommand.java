package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.classes.Team;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class TeamPlayersCommand {

    private final SlashCommandInteractionEvent event;

    public TeamPlayersCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void teamPlayers() {
        int teamId = event.getOption("team").getAsInt();
        if(!SQLRequest.isTeamExist(teamId)){
            event.getHook().sendMessage("L'équipe n'existe pas !").queue();
            return;
        }

        Team team = SQLRequest.getTeamFromId(teamId);
        StringBuilder message = new StringBuilder("""
                **__Liste des joueurs de %s %s__**
                """.formatted(team.name(), team.logo().getEmoji()));
        message.append("\n");
        for(Player player : SQLRequest.getPlayersFromTeamId(teamId)){
            message.append(player.id()).append(" - ").append(player.name()).append("   ").append(player.jersey()).append("\n");
        }
        event.getHook().sendMessage(message.toString()).queue();
    }
}
