package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.database.SQLRequest;
import fr.zeltaria.zenaligue.enums.PlayerRole;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreatePlayerCommand {

    private final SlashCommandInteractionEvent event;

    public CreatePlayerCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void createPlayer() {
        int teamId = event.getOption("team").getAsInt();
        int jersey = event.getOption("jersey").getAsInt();
        if(!SQLRequest.isTeamExist(teamId)){
            event.getHook().sendMessage("L'équipe n'existe pas !").queue();
            return;
        }
        for(Player pp : SQLRequest.getPlayersFromTeamId(teamId)){
            if(pp.jersey() == jersey){
                event.getHook().sendMessage("Il existe déjà un joueur avec ce numéro de maillot !").queue();
                return;
            }
        }
        PlayerRole role = switch (event.getOption("role").getAsString()) {
            case "GARDIEN" -> PlayerRole.GARDIEN;
            case "DEFENSEUR" -> PlayerRole.DEFENSEUR;
            case "MILIEU" -> PlayerRole.MILIEU;
            case "ATTAQUANT" -> PlayerRole.ATTAQUANT;
            default -> throw new IllegalStateException("Unexpected value: " + event.getOption("role").getAsString());
        };
        Player player = new Player(null, event.getOption("name").getAsString().replace("'","\\'"), role, jersey);
        SQLRequest.addPlayerInTeam(teamId, player);
        event.getHook().sendMessage("Le joueur %s a bien été ajouté à l'équipe %s !".formatted(player.name(), SQLRequest.getTeamFromId(teamId).name())).queue();
    }

}
