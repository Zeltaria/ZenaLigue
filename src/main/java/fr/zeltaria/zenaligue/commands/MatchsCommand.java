package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class MatchsCommand {

    private final SlashCommandInteractionEvent event;

    public MatchsCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void matchs() {
        Integer journee = SQLRequest.getLastDayNumber();
        if(journee == 0){
            event.getHook().sendMessage("Aucune journée n'a été créée !").queue();
            return;
        }
        StringBuilder message = new StringBuilder("""
                **__Liste des matchs de la journée %s__**
                """.formatted(journee));
        message.append("\n");
        for(Match m : SQLRequest.getMatchsFromLastDay()){
            message.append(m.id()).append(" - ").append(m.team1().name()).append(" ").append(m.team1().logo().getEmoji()).append(" - ").append(m.team2().logo().getEmoji()).append(" ").append(m.team2().name()).append("\n");
        }
        event.getHook().sendMessage(message.toString()).queue();

    }
}
