package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateLeagueCommand {

    private final SlashCommandInteractionEvent event;

    public CreateLeagueCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void createLeague() {
        if(SQLRequest.isLastLeagueFinished() == null){
            event.getHook().sendMessage("Une erreur est survenue !").queue();
            return;
        }
        if(!SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue précédente n'est pas encore terminée !").queue();
        }
        else {
            String name = event.getOption("nom").getAsString().replace("'", "\\'");
            int id = SQLRequest.getLastLeagueId() + 1;
            SQLRequest.createLeague(name, id);
            event.getHook().sendMessage("La ligue **%s** a bien été créée avec l'id : %s !".formatted(name, id)).queue();
        }
    }
}
