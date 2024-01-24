package fr.zeltaria.zenaligue.commands;

import fr.zeltaria.zenaligue.classes.Goal;
import fr.zeltaria.zenaligue.classes.Match;
import fr.zeltaria.zenaligue.database.SQLRequest;
import fr.zeltaria.zenaligue.enums.ZenaEmojis;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartDayCommand {

    private final SlashCommandInteractionEvent event;
    private static boolean isDayStarted = false;

    public StartDayCommand(SlashCommandInteractionEvent event) {
        this.event = event;
    }

    @SuppressWarnings("DuplicatedCode")
    public void startDay() {
        if(SQLRequest.isLastLeagueFinished()){
            event.getHook().sendMessage("La ligue est terminée !").queue();
            return;
        }
        if(SQLRequest.isLastDayFinished()){
            event.getHook().sendMessage("La dernière journée est déjà terminée !").queue();
            return;
        }
        if(isDayStarted){
            event.getHook().sendMessage("La journée est déjà en cours !").queue();
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(event.getOption("hour").getAsString(), formatter);
        long initialDelay = calculateInitialDelay(time);
        if(initialDelay == 0){
            event.getHook().sendMessage("L'heure de début du match est déjà passée !").queue();
            return;
        }
        isDayStarted = true;
        List<Match> matchs = SQLRequest.getMatchsFromLastDay();
        List<Goal> goals = new ArrayList<>();
        StringBuilder messagePrez = new StringBuilder("""
                ```Journée %s : début des matchs à %s```
                """.formatted(SQLRequest.getLastDayNumber(), time));
        messagePrez.append("**__Matchs du jour :__**\n\n");
        for (Match match : matchs) {
            goals.addAll(SQLRequest.getGoalsFromMatch(match.id()));
            messagePrez.append(" %s - %s \n".formatted((match.team1().name() + " " + match.team1().logo().getEmoji()), (match.team2().logo().getEmoji() + " " + match.team2().name())));
        }
        final int[] minute = {0};
        final int[] mi_temps = {1};
        long channelId = Long.parseLong(SQLRequest.getTextChannelIdFromGuildId(event.getGuild().getId()));
        TextChannel channel = event.getGuild().getTextChannelById(channelId);
        if(channel == null){
            event.getHook().sendMessage("Le channel n'existe pas ! \n Veuillez contacter un Admin afin de setup le bot !").queue();
            return;
        }
        channel.sendMessage(messagePrez.toString()).queue();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> CompletableFuture.runAsync(() -> {
            List<Goal> goalsAtMinute = getGoalsAtMinute(minute[0], goals);
            if(minute[0] > 45 && mi_temps[0] == 1){
                goalsAtMinute.clear();
            }
            if (minute[0] == 0) {
                channel.sendMessage("```Coup d'envoi```\n").queue();
                StringBuilder start = new StringBuilder();
                for (Match match : matchs) {
                    start.append(" %s 0 - 0 %s \n".formatted((match.team1().name() + " " + match.team1().logo().getEmoji()), (match.team2().logo().getEmoji() + " " + match.team2().name())));
                }
                channel.sendMessage(start.toString()).queue();
            }
            if (!goalsAtMinute.isEmpty()) {
                StringBuilder text = new StringBuilder();
                text.append(":clock1: %s' \n".formatted(minute[0]));
                for (Goal goal : goalsAtMinute) {
                    if(goal.csc() == 0){
                        text.append("But de %s pour l'équipe %s ! \n".formatted(SQLRequest.getPlayerFromId(goal.buteurId()).name(), SQLRequest.getTeamFromId(goal.teamId()).name()));
                    }
                    else {
                        text.append("But contre son camp de %s en faveur de %s ! \n".formatted(SQLRequest.getPlayerFromId(goal.buteurId()).name(), SQLRequest.getTeamFromId(goal.teamId()).name()));
                    }
                    for(Match m : matchs){
                        if(m.id() == goal.matchId()){
                            m.addBut(goal);
                            break;
                        }
                    }
                }
                channel.sendMessage(text.toString()).queue();
            }
            if(minute[0] == 45 && mi_temps[0] == 1){
                channel.sendMessage("```Mi-temps !```").queue();
                StringBuilder text_mi_temps = new StringBuilder();
                for (Match match : matchs) {
                    text_mi_temps.append(" %s **%s** - **%s** %s \n".formatted((match.team1().name() + " " + match.team1().logo().getEmoji()), match.scoret1(), match.scoret2(), (match.team2().logo().getEmoji() + " " + match.team2().name())));
                }
                channel.sendMessage(text_mi_temps.toString()).queue();
            }
            if(minute[0] == 60 && mi_temps[0] == 1){
                minute[0] = 45;
                mi_temps[0] = 2;
                channel.sendMessage("```Début de la seconde mi-temps !```").queue();
                StringBuilder text_mi_temps2 = new StringBuilder();
                for (Match match : matchs) {
                    text_mi_temps2.append(" %s **%s** - **%s** %s \n".formatted((match.team1().name() + " " + match.team1().logo().getEmoji()), match.scoret1(), match.scoret2(), (match.team2().logo().getEmoji() + " " + match.team2().name())));
                }
                channel.sendMessage(text_mi_temps2.toString()).queue();
            }
            if(minute[0] == 90){
                channel.sendMessage("```Fin des matchs !```").queue();
                StringBuilder text_fin = new StringBuilder();
                for (Match match : matchs) {
                    text_fin.append(" %s **%s** - **%s** %s \n".formatted((match.team1().name() + " " + match.team1().logo().getEmoji()), match.scoret1(), match.scoret2(), (match.team2().logo().getEmoji() + " " + match.team2().name())));
                    for (Goal goal : goals) {
                        if(goal.matchId() == match.id()){
                            if(goal.teamId() == match.team1().id()){
                                if(goal.csc() == 0){
                                    text_fin.append("%s %s (%s') \n".formatted(ZenaEmojis.BLUEBALL.getEmoji(), SQLRequest.getPlayerFromId(goal.buteurId()).name() ,goal.minute()));
                                }
                                else{
                                    text_fin.append("%s %s (%s') \n".formatted(ZenaEmojis.REDBALL.getEmoji(), SQLRequest.getPlayerFromId(goal.buteurId()).name() ,goal.minute()));
                                }
                            }else{
                                if (goal.csc() == 0) {
                                    text_fin.append("\t\t\t\t\t\t%s %s (%s') \n".formatted(ZenaEmojis.BLUEBALL.getEmoji(), SQLRequest.getPlayerFromId(goal.buteurId()).name(), goal.minute()));
                                } else {
                                    text_fin.append("\t\t\t\t\t\t%s %s (%s') \n".formatted(ZenaEmojis.REDBALL.getEmoji(), SQLRequest.getPlayerFromId(goal.buteurId()).name(), goal.minute()));
                                }
                            }
                        }
                    }
                    text_fin.append("\n");
                }
                channel.sendMessage(text_fin.toString()).queue();
                isDayStarted = false;
                for(Match match : matchs){
                    SQLRequest.updateMatch(match.id(), match.scoret1(), match.scoret2());
                }
                SQLRequest.finishDay();
                scheduler.shutdown();
            }
            minute[0]++;
        }), initialDelay, 1, TimeUnit.MINUTES);
        event.getHook().sendMessage("La journée a bien été lancée !").queue();
    }

    private List<Goal> getGoalsAtMinute(int i, List<Goal> goals) {
        List<Goal> goalsAtMinute = new ArrayList<>();
        for(Goal goal : goals){
            if(goal.minute() == i){
                goalsAtMinute.add(goal);
            }
        }
        return goalsAtMinute;
    }

    private static long calculateInitialDelay(LocalTime heureDebutMatch) {
        LocalTime heureActuelle = LocalTime.now();
        if (heureActuelle.isAfter(heureDebutMatch)) {
            return 0;
        }
        return LocalTime.now().until(heureDebutMatch, ChronoUnit.MINUTES);
    }

    public static boolean isDayStarted() {
        return isDayStarted;
    }
}

