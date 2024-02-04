package fr.zeltaria.zenaligue.commands;

import com.mysql.cj.MessageBuilder;
import fr.zeltaria.zenaligue.Main;
import fr.zeltaria.zenaligue.database.SQLRequest;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;

public class SayCommand {

        private final SlashCommandInteractionEvent event;

        public SayCommand(SlashCommandInteractionEvent event) {
            this.event = event;
        }

        public void say() {
            if(event.getOption("message") == null && event.getOption("image") == null)
            {
                event.getHook().sendMessage("Vous devez spécifier un message ou une image !").queue();
                return;
            }
            String message = null;
            Attachment image = null;
            if(event.getOption("message") != null){
                message = event.getOption("message").getAsString();
            }
            if(event.getOption("image") != null){
                image = event.getOption("image").getAsAttachment();
            }
            long channelId = Long.parseLong(SQLRequest.getTextChannelIdFromGuildId(event.getGuild().getId()));
            TextChannel channel = event.getGuild().getTextChannelById(channelId);
            if(channel == null){
                event.getHook().sendMessage("Le channel n'existe pas ! \n Veuillez contacter un Admin afin de setup le bot !").queue();
                return;
            }
            MessageCreateAction messageCreateAction = channel.sendMessage("");
            if(message != null)
            {
                messageCreateAction.addContent(message);
            }
            if(image != null)
            {
                messageCreateAction.addContent("\n" + image.getUrl());
            }
            messageCreateAction.queue();
            event.getHook().sendMessage("Message envoyé !\nTu peux aller le voir sur " + channel.getAsMention()).queue();
        }
}
