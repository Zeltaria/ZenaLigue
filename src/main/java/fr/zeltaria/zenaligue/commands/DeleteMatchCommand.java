package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DeleteMatchCommand {

    private final SlashCommandInteractionEvent event;

    public DeleteMatchCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void deleteMatch() {
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est terminée !").queue();
            return;
        }
        if(SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La journée est terminée !").queue();
            return;
        }
        Match match = SQLRequest.getMatchFromId(event.getOption("match").getAsInt());
        if(match == null){
            event.getHook().sendMessage("Ce match n'existe pas !").queue();
            return;
        }
        if(SQLRequest.getDayMatchIdIsPlayed(match.id()) != SQLRequest.getLastDayId()){
            event.getHook().sendMessage("Ce match a déjà été joué !").queue();
            return;
        }
        SQLRequest.deleteMatch(event.getOption("match").getAsInt());
        event.getHook().sendMessage("Le match %s et tous les buts associés ont bien été supprimés de la journée %s !".formatted((match.team1().name() + " - " + match.team2().name()), SQLRequest.getLastDayNumber())).queue();
    }
}
