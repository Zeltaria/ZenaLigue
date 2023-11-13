package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        String command = event.getName();
        Member member = event.getMember();
        if (command.equals("setup")) {
            if (member.hasPermission(Permission.ADMINISTRATOR)) {
                event.deferReply().queue();
                new SetupCommand(event).setup();
            } else {
                event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
            }
        } else {
            event.reply("Cette commande n'existe pas !").setEphemeral(true).queue();
        }
    }

    private void resetCommands(GuildReadyEvent event) {
        event.getGuild().updateCommands().queue();
        event.getJDA().updateCommands().queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        resetCommands(event);
        if (Arrays.asList(Main.getInstance().getConfig().get("DISCORD_SERVERS_IDS").split(",")).contains(event.getGuild().getId())) {
            event.getGuild().updateCommands().addCommands(commandsInit()).queue();
            //SetupCommand.start(event.getGuild());
        } else {
            try {
                NewsChannel channel = event.getGuild().getDefaultChannel().asNewsChannel();
                channel.sendMessage("Le bot n'est pas configuré pour ce serveur !").queue();
                channel.sendMessage("Pour les informations sur Zenavia, rejoignez notre discord : https://discord.gg/zenavia !").queue();
            } catch (Exception e) {
                try {
                    TextChannel channel = event.getGuild().getSystemChannel();
                    channel.sendMessage("Le bot n'est pas configuré pour ce serveur !").queue();
                    channel.sendMessage("Pour les informations sur Zenavia, rejoignez notre discord : https://discord.gg/zenavia !").queue();
                } catch (Exception f) {
                    event.getGuild().createTextChannel("ZENAVIA").queue(textChannel -> {
                        textChannel.sendMessage(event.getGuild().getOwner().getAsMention() + ", le bot n'est pas configuré pour votre serveur : " + event.getGuild().getName() + " !").queue();
                        textChannel.sendMessage("Pour les informations sur Zenavia, rejoignez notre discord : https://discord.gg/zenavia !").queue();
                    });
                }
            }
            event.getGuild().leave().complete();
        }
    }

    private List<CommandData> commandsInit() {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("setup", "setup le bot"));

        return commandData;
    }
}
