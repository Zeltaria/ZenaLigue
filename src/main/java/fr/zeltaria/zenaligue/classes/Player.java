package fr.zeltaria.zenaligue.classes;

import fr.zeltaria.zenaligue.enums.PlayerRole;

public record Player(Integer id, String name, PlayerRole role, int jersey) {
}
