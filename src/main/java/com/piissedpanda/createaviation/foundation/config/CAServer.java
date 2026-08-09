package com.piissedpanda.createaviation.foundation.config;

import net.createmod.catnip.config.ConfigBase;

public class CAServer extends ConfigBase {

    public final ConfigInt maxBarrierPostLength = i(5, 3, "maxBarrierPostLength", Comments.maxBarrierPostLength);
    public final ConfigInt maxBarrierPostConnections = i(4, 1, "maxBarrierPostConnections",
            Comments.maxBarrierPostConnections);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String maxBarrierPostLength = "Maximum length in blocks of barrier post connections.";
        static String maxBarrierPostConnections = "Maximum amount of connections each barrier post can have.";
    }

}
