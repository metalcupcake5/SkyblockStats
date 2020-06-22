package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.common.base.Preconditions;

import java.awt.*;

public enum Symbols {
    HEALTH("❤"),
    DEFENSE("❈"),
    STRENGTH("❁"),
    INTELLIGENCE("✎"),
    CRIT_CHANCE("☣"),
    CRIT_DAMAGE("☠"),
    SEA_CREATURE_CHANCE("α"),
    MAGIC_FIND("✯"),
    PET_LUCK("♣");

    private final String symbol;

    Symbols(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
