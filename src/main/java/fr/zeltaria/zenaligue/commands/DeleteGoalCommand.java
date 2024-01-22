package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Goal;
import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DeleteGoalCommand {

    private final SlashCommandInteractionEvent event;

    public DeleteGoalCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void deleteGoal() {
        int goalId = event.getOption("goal").getAsInt();
        Goal goal = SQLRequest.getGoalFromId(goalId);
        if(goal == null){
            event.getHook().sendMessage("Ce but n'existe pas !").queue();
            return;
        }
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est terminée !").queue();
            return;
        }
        if(SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La dernière journée est terminée !").queue();
            return;
        }

        boolean matchExist = false;
        for (Match m : SQLRequest.getMatchsFromLastDay()){
            if (m.id() == goal.matchId()) {
                matchExist = true;
                break;
            }
        }
        if(!matchExist){
            event.getHook().sendMessage("Ce match à déjà été joué !").queue();
            return;
        }
        SQLRequest.deleteGoal(goalId);
        event.getHook().sendMessage("Le but de %s pour %s a bien été supprimé !".formatted(SQLRequest.getPlayerFromId(goal.buteurId()).name(),SQLRequest.getTeamFromId(goal.teamId()).name())).queue();
    }
}
