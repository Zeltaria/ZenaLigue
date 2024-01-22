package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateGoalCommand {

    private final SlashCommandInteractionEvent event;

    public CreateGoalCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void createGoal() {
        int matchId = event.getOption("match").getAsInt();
        int teamId = event.getOption("team").getAsInt();
        int minute = event.getOption("minute").getAsInt();
        Player buteur = SQLRequest.getPlayerFromId(event.getOption("buteur").getAsInt());
        if(buteur == null){
            event.getHook().sendMessage("Ce joueur n'existe pas !").queue();
            return;
        }
        Match match = SQLRequest.getMatchFromId(matchId);
        if(match == null){
            event.getHook().sendMessage("Ce match n'existe pas !").queue();
            return;
        }
        if(match.team1().id() != teamId && match.team2().id() != teamId){
            event.getHook().sendMessage("Cette équipe ne joue pas dans ce match !").queue();
            return;
        }
        if(SQLRequest.getPlayerTeamId(buteur.id()) != match.team1().id() && SQLRequest.getPlayerTeamId(buteur.id()) != match.team2().id()){
            event.getHook().sendMessage("Ce joueur ne joue pas dans ce match !").queue();
            return;
        }
        int csc;
        if(SQLRequest.getPlayerTeamId(buteur.id()) != teamId){
            csc = 1;
        }
        else {
            csc = 0;
        }
        SQLRequest.addGoal(matchId, teamId, buteur, minute, csc);
        if(csc == 1){
            event.getHook().sendMessage("Le but a bien été ajouté pour l'équipe %s par %s en csc !".formatted(SQLRequest.getTeamFromId(teamId).name(), buteur.name())).queue();
            return;
        }
        event.getHook().sendMessage("Le but a bien été ajouté pour l'équipe %s par %s !".formatted(SQLRequest.getTeamFromId(teamId).name(), buteur.name())).queue();
    }
}
