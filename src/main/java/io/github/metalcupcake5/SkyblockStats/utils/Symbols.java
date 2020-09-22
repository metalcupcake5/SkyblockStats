package io.github.metalcupcake5.SkyblockStats.utils;

public enum Symbols {
    HEALTH("\u2764"),
    DEFENSE("\u2748"),
    STRENGTH("\u2741"),
    INTELLIGENCE("\u270E"),
    CRIT_CHANCE("\u2623"),
    CRIT_DAMAGE("\u2620"),
    SEA_CREATURE_CHANCE("\u03B1"),
    MAGIC_FIND("\u272F"),
    PET_LUCK("\u2663");

    private final String symbol;

    Symbols(String symbol){
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}
