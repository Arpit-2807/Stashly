package com.arpit.stashly;

import java.util.List;

public class CommandHandler {

    private final Store store;

    //Constructor injection
    public CommandHandler(Store store) {
        this.store = store;
    }

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

            case "SET": {
                if(command.size() != 3) {
                    return wrongArgs("set");
                }
                store.set(command.get(1), command.get(2));
                return RespWriter.simple("OK");
            }

            case "GET": {
                if(command.size() != 2) {
                    return wrongArgs("get");
                }
                return RespWriter.bulk(store.get(command.get(1))); // null -> $-1
            }

            case "DEL": {
                if(command.size() < 2) {
                    return wrongArgs("del");
                }
                int deleted=0;
                for(int i=1; i<command.size(); i++){
                    if(store.delete(command.get(i))) {
                        deleted++;
                    }
                }
                return RespWriter.integer(deleted);
            }

            case "EXISTS": {
                if(command.size() < 2) {
                    return wrongArgs("exists");
                }
                int found = 0;
                for(int i = 1; i < command.size(); i++) {
                    if(store.exists(command.get(i))) {
                        found++;
                    }
                }
                return RespWriter.integer(found);
            }

            default:
                return RespWriter.error("unknown command '" + name + "'");
        }
    }

    private String wrongArgs(String commandName) {
        return RespWriter.error("wrong number of arguments for '" + commandName + "' command");
    }
}
