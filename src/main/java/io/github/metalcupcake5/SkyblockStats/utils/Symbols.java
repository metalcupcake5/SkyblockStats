package io.github.metalcupcake5.SkyblockStats.utils;

import com.google.common.base.Preconditions;

import java.awt.*;

public enum Symbols {
    HEALTH("❤"),
    DEFENSE("❈"),
    STRENGTH("❁"),
    INTELLIGENCE("✎"),
    CRIT_CHANCE("☣"),
    CRIT_DAMAGE("☠");

    private final String symbol;

    Symbols(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }


}
