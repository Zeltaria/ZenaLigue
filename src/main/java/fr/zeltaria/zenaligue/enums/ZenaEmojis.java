package fr.zeltaria.zenaligue.enums;

import fr.zeltaria.zenaligue.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public enum ZenaEmojis {

    AGUERA(1172285814747697183L),
    SEACITY(1172285481355055156L),
    BERILAC(1172285484463030424L),
    MIRABOLA(1172285473591406622L),
    NARTA(1172285462606528513L),
    ROSLEG_SPARTIA(1172285459221725297L),
    MARONIS(1172285469577465957L),
    NORDSTATD(1172285464594628820L),
    CIRA_EVO_CALGIO(1172286278436409415L),
    GENRIO(1172285476183478282L),
    DOMULONT(1172285478494552114L),
    MERILAC(1172285466729533461L);

    private final RichCustomEmoji emoji ;
    private final Guild guild = Main.getInstance().getShardManager().getGuildById(815988245933457470L);

    ZenaEmojis(long emoji){
        this.emoji = guild.getEmojiById(emoji);
    }

    public String getEmoji() {
        return emoji.getAsMention();
    }
}
