package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FinishLeagueCommand {

    private final SlashCommandInteractionEvent event;

    public FinishLeagueCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void finishLeague() {
        if(SQLRequest.isLastLeagueFinished() == null){
            event.getHook().sendMessage("Une erreur est survenue !").queue();
            return;
        }
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est déjà terminée !").queue();
            return;
        }
        if(!SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La dernière journée n'est pas terminée !").queue();
            return;
        }
        SQLRequest.finishLeague();
        event.getHook().sendMessage("La ligue %s a bien été terminée !".formatted(SQLRequest.getLastLeagueName())).queue();
    }
}
