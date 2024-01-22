package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private void resetCommands(GuildReadyEvent event) {
        event.getGuild().updateCommands().queue();
        event.getJDA().updateCommands().queue();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
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

    private boolean memberCanUseCommand(Member member, SlashCommandInteractionEvent event){
        if(!event.getGuild().getId().equals(Main.getInstance().getConfig().get("DISCORD_SERVER_MASTER"))){
            return member.hasPermission(Permission.ADMINISTRATOR);
        }
        return (member.hasPermission(Permission.ADMINISTRATOR) || member.getRoles().contains(event.getGuild().getRoleById(Main.getInstance().getConfig().get("ROLE_ZENALIGUE"))));
    }

    private List<CommandData> commandsInit() {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("setup", "setup le bot"));
        commandData.add(Commands.slash("teams", "affiche les équipes"));

        OptionData playerNameOption = new OptionData(OptionType.STRING, "name", "Nom du joueur", true);
        OptionData playerRoleOption = new OptionData(OptionType.STRING, "role", "Role du joueur", true)
                .addChoice("GARDIEN", "GARDIEN")
                .addChoice("DEFENSEUR", "DEFENSEUR")
                .addChoice("MILIEU", "MILIEU")
                .addChoice("ATTAQUANT", "ATTAQUANT");
        OptionData playerJerseyOption = new OptionData(OptionType.INTEGER, "jersey", "Numéro du maillot", true);
        OptionData playerTeamOption = new OptionData(OptionType.INTEGER, "team", "Id de l'équipe", true);
        commandData.add(Commands.slash("create_player", "Créer un joueur").addOptions(playerTeamOption, playerNameOption, playerRoleOption, playerJerseyOption));

        OptionData playerTeamOption2 = new OptionData(OptionType.INTEGER, "team", "Id de l'équipe", true);
        OptionData playerJerseyOption2 = new OptionData(OptionType.INTEGER, "jersey", "Numéro du maillot", true);
        commandData.add(Commands.slash("delete_player", "Supprimer un joueur").addOptions(playerTeamOption2, playerJerseyOption2));

        OptionData teamOption = new OptionData(OptionType.INTEGER, "team", "Id de l'équipe", true);
        commandData.add(Commands.slash("players", "Liste des joueurs d'une équipe").addOptions(teamOption));

        OptionData leagueNameOption = new OptionData(OptionType.STRING, "nom", "Nom de la ligue", true);
        commandData.add(Commands.slash("create_league", "Créer une nouvelle ligue").addOptions(leagueNameOption));

        commandData.add(Commands.slash("finish_league", "Finir la ligue en cours"));

        commandData.add(Commands.slash("matchs", "Liste des matchs de la prochaine journée"));

        commandData.add(Commands.slash("create_day", "Créer une nouvelle journée"));

        OptionData matchTeam1Option = new OptionData(OptionType.INTEGER, "team1", "Id de l'équipe 1", true);
        OptionData matchTeam2Option = new OptionData(OptionType.INTEGER, "team2", "Id de l'équipe 2", true);
        commandData.add(Commands.slash("create_match", "Ajouter un match").addOptions(matchTeam1Option, matchTeam2Option));

        OptionData deleteMatchOption = new OptionData(OptionType.INTEGER, "match", "Id du match", true);
        commandData.add(Commands.slash("delete_match", "Supprimer un match").addOptions(deleteMatchOption));

        OptionData matchOption = new OptionData(OptionType.INTEGER, "match", "Id du match", true);
        OptionData teamOption2 = new OptionData(OptionType.INTEGER, "team", "Id de l'équipe", true);
        OptionData minuteOption = new OptionData(OptionType.INTEGER, "minute", "Minute du but", true);
        OptionData playerOption = new OptionData(OptionType.INTEGER, "buteur", "Id du buteur", true);
        commandData.add(Commands.slash("create_goal", "Ajouter un but").addOptions(matchOption, teamOption2, minuteOption, playerOption));

        OptionData matchOption2 = new OptionData(OptionType.INTEGER, "match", "Id du match", true);
        commandData.add(Commands.slash("goals", "Liste des buts d'un match").addOptions(matchOption2));

        OptionData goalOption = new OptionData(OptionType.INTEGER, "goal", "Id du but", true);
        commandData.add(Commands.slash("delete_goal", "Supprimer un but").addOptions(goalOption));

        return commandData;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        Member member = event.getMember();
        switch (command) {
            case "setup" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new SetupCommand(event).setup();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "teams" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new TeamsCommand(event).teams();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "create_player" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new CreatePlayerCommand(event).createPlayer();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "delete_player" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new DeletePlayerCommand(event).deletePlayer();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "players" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new TeamPlayersCommand(event).teamPlayers();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "create_league" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new CreateLeagueCommand(event).createLeague();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "finish_league" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new FinishLeagueCommand(event).finishLeague();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "matchs" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new MatchsCommand(event).matchs();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "create_day" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new CreateDayCommand(event).createDay();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "create_match" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new CreateMatchCommand(event).createMatch();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "delete_match" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new DeleteMatchCommand(event).deleteMatch();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "create_goal" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new CreateGoalCommand(event).createGoal();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "goals" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new GoalsCommand(event).goals();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            case "delete_goal" -> {
                if(memberCanUseCommand(member, event)){
                    event.deferReply().queue();
                    new DeleteGoalCommand(event).deleteGoal();
                } else {
                    event.reply("Cette commande est réservée aux administrateurs du serveur!").setEphemeral(true).queue();
                }
            }
            default -> event.reply("Cette commande n'existe pas !").setEphemeral(true).queue();
        }
    }
}
