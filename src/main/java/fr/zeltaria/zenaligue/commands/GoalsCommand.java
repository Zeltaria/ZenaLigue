package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Goal;
import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.database.SQLRequest;
import fr.zeltaria.zenaligue.enums.ZenaEmojis;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class GoalsCommand {

    private final SlashCommandInteractionEvent event;

    public GoalsCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void goals() {
        int matchId = event.getOption("match").getAsInt();
        Match match = SQLRequest.getMatchFromId(matchId);
        if(match == null){
            event.getHook().sendMessage("Ce match n'existe pas !").queue();
            return;
        }
        List<Goal> buts = SQLRequest.getGoalsFromMatch(matchId);
        if(buts.isEmpty()){
            event.getHook().sendMessage("Il n'y a pas de buts dans ce match !").queue();
            return;
        }
        StringBuilder message = new StringBuilder("""
                **__Liste des buts du match %s - %s__**
                """.formatted(match.team1().name(), match.team2().name()));
        message.append("\n");
        for(Goal but : buts){
            Player buteur = SQLRequest.getPlayerFromId(but.buteurId());
            if(but.teamId() == match.team1().id()){
                if(but.csc() == 1){
                    message.append(but.id()).append(" - ").append(ZenaEmojis.REDBALL.getEmoji()).append(" ").append(buteur.name()).append(" (%s')".formatted(but.minute())).append("\n");
                }
                else {
                    message.append(but.id()).append(" - ").append(ZenaEmojis.BLUEBALL.getEmoji()).append(" ").append(buteur.name()).append(" (%s')".formatted(but.minute())).append("\n");
                }
            }
            else {
                if(but.csc() == 1){
                    message.append(but.id()).append(" - ").append("\t\t\t\t\t\t").append(ZenaEmojis.REDBALL.getEmoji()).append(" ").append(buteur.name()).append(" (%s')".formatted(but.minute())).append("\n");
                }
                else {
                    message.append(but.id()).append(" - ").append("\t\t\t\t\t\t").append(ZenaEmojis.BLUEBALL.getEmoji()).append(" ").append(buteur.name()).append(" (%s')".formatted(but.minute())).append("\n");
                }
            }
        }
        event.getHook().sendMessage(message.toString()).queue();
    }
}
