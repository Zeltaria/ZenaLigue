package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DeletePlayerCommand {

    private final SlashCommandInteractionEvent event;

    public DeletePlayerCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void deletePlayer() {
        int teamId = event.getOption("team").getAsInt();
        int jersey = event.getOption("jersey").getAsInt();
        if(!SQLRequest.isTeamExist(teamId)){
            event.getHook().sendMessage("L'équipe n'existe pas !").queue();
            return;
        }
        if(!SQLRequest.isPlayerExist(teamId, jersey)){
            event.getHook().sendMessage("Le joueur n'existe pas !").queue();
            return;
        }
        event.getHook().sendMessage("Le joueur **%s** a bien été supprimé !".formatted(SQLRequest.getPlayer(teamId, jersey).name())).queue();
        SQLRequest.removePlayerFromTeam(teamId, jersey);
    }
}
