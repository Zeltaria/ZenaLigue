package fr.zeltaria.zenaligue.classes;

import java.util.List;

public record Team(String name, String shortName, List<Player> players, String logo) {
}
