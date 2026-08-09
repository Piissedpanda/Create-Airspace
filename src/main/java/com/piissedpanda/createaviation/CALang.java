package com.piissedpanda.createaviation;

import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class CALang extends Lang {

    public static LangBuilder builder() {
        return new LangBuilder(CreateAviation.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }

}
