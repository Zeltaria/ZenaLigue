package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateMatchCommand {

    private final SlashCommandInteractionEvent event;

    public CreateMatchCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void createMatch() {
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est terminée !").queue();
            return;
        }
        if(SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La journée est terminée !").queue();
            return;
        }
        if(event.getOption("team1").getAsInt() == event.getOption("team2").getAsInt()){
            event.getHook().sendMessage("Les deux équipes ne peuvent pas être les mêmes !").queue();
            return;
        }
        if(SQLRequest.getTeamFromId(event.getOption("team1").getAsInt()) == null || SQLRequest.getTeamFromId(event.getOption("team2").getAsInt()) == null){
            event.getHook().sendMessage("Une des deux équipes n'existe pas !").queue();
            return;
        }
        if(SQLRequest.isMatchExistInLastDay(event.getOption("team1").getAsInt(), event.getOption("team2").getAsInt())){
            event.getHook().sendMessage("Ce match existe déjà !").queue();
            return;
        }
        if(SQLRequest.isTeamAlreadyPlayedInLastDay(event.getOption("team1").getAsInt()) || SQLRequest.isTeamAlreadyPlayedInLastDay(event.getOption("team2").getAsInt())){
            event.getHook().sendMessage("Une des deux équipes joue déjà un autre match dans cette journée !").queue();
            return;
        }
        SQLRequest.createNewMatch(event.getOption("team1").getAsInt(), event.getOption("team2").getAsInt());
        event.getHook().sendMessage("Le match %s - %s a bien été ajouté à la journée %s !".formatted(SQLRequest.getTeamFromId(event.getOption("team1").getAsInt()).name(), SQLRequest.getTeamFromId(event.getOption("team2").getAsInt()).name(), SQLRequest.getLastDayNumber())).queue();
    }
}
