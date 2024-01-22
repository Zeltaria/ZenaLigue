package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class SetupCommand {

    private final SlashCommandInteractionEvent event;
    public SetupCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void setup() {
        TextChannel channel = null;
        if(SQLRequest.getTextChannelIdFromGuildId(event.getGuild().getId()) != null){
            try {
                channel = event.getGuild().getTextChannelById(SQLRequest.getTextChannelIdFromGuildId(event.getGuild().getId()));
            }catch (NullPointerException ignored){}
        }
        if(channel == null) {
            event.getGuild().createTextChannel("zenaligue")
                    .queue(textChannel -> {
                        event.getHook().sendMessage("Le channel a bien été créé !").queue();
                        SQLRequest.addGuild(event.getGuild().getId(), textChannel.getId());
                    });
        }else{
            event.getHook().sendMessage("Le channel existe déjà ! Pas besoin de setup à nouveau ! ").queue();
        }
    }
}
