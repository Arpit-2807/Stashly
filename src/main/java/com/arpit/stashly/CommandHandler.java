package com.arpit.stashly;

import java.util.List;

public class CommandHandler {

    public String run(List<String> command) {
        if(command.isEmpty()) {
            return RespWriter.error("empty command");
        }

        String name = command.get(0).toUpperCase();

        switch(name) {
            case "PING":
                if(command.size() == 1) {
                    return RespWriter.simple("PONG");
                }
                return RespWriter.bulk(command.get(1));

            case "ECHO":
                if(command.size() != 2) {
                    return RespWriter.error("wrong number of arguments for 'echo' command");
                }
                return RespWriter.bulk(command.get(1));

            default:
                return RespWriter.error("unknown command '" + name + "'");
        }
    }
}
