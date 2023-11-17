package fr.zeltaria.zenaligue.classes;

import fr.zeltaria.zenaligue.enums.ZenaEmojis;

import java.util.List;

public record Team(int id, String name, String shortName, List<Player> players, ZenaEmojis logo) {
}
