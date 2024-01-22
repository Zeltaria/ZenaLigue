package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CreateDayCommand {

    private final SlashCommandInteractionEvent event;

    public CreateDayCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void createDay() {
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est terminée !").queue();
            return;
        }
        if(!SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La journée précédente n'est pas terminée !").queue();
            return;
        }
        SQLRequest.createNewDay();
        event.getHook().sendMessage("La journée %s a bien été créée !".formatted(SQLRequest.getLastDayNumber())).queue();
    }
}
