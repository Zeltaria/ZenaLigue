package fr.zeltaria.zenaligue.classes;

import fr.zeltaria.zenaligue.enums.PlayerRole;

public record Player(String name, PlayerRole role, int note) {
}
