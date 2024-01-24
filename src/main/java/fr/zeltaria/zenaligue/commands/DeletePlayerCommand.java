package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Player;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DeletePlayerCommand {

    private final SlashCommandInteractionEvent event;

    public DeletePlayerCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void deletePlayer() {
        int id =event.getOption("id").getAsInt();
        Player player = SQLRequest.getPlayerFromId(id);
        if(player == null){
            event.getHook().sendMessage("Le joueur n'existe pas !").queue();
            return;
        }
        event.getHook().sendMessage("Le joueur **%s** a bien été supprimé !".formatted(player.name())).queue();
        SQLRequest.removePlayerFromId(id);
    }
}
