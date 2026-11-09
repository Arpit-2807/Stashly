# Stashly

A Redis-compatible in-memory database built from scratch in Java.
(Compatible means it follows the same message format, called RESP, that Redis clients use.)

> Status: work in progress, Week 1 of a 4-week build.

## What works today

| Command | What it does |
|---|---|
| `PING` | replies `PONG` |
| `ECHO text` | replies with the same text |
| `SET key value` | saves a value |
| `GET key` | reads a value (null if missing) |
| `DEL key [key ...]` | deletes keys, replies how many were deleted |
| `EXISTS key [key ...]` | replies how many of the keys exist |

Also: unknown commands and wrong arguments get proper error replies, and the parser and commands are covered by unit tests.

## How it works

```
client -> TCP server (port 6380) -> RespParser -> CommandHandler -> Store
                                                       |
client <------------- RespWriter <---------------------+
```

- **RespParser** reads a command from the network and turns it into a list.
- **CommandHandler** decides what to do for each command.
- **Store** keeps the data in memory.
- **RespWriter** builds the reply in the RESP format.

## Run it

Requirements: Java 21 and the included Maven wrapper.

1. Run the tests: `.\mvnw.cmd test`
2. Start the server: run the `StashlyMain` class in IntelliJ. It listens on port 6380.
3. Try it from PowerShell:

```
. .\scripts\stashly-tools.ps1
Stash SET name arpit
Stash GET name
```

## Roadmap

- [x] TCP server and RESP parser
- [x] Replies, `PING`, `ECHO`
- [x] `SET`, `GET`, `DEL`, `EXISTS`
- [ ] `INCR`, `DECR`, `KEYS`, `DBSIZE`, `FLUSHALL`
- [ ] Many clients at once
- [ ] Faster server with a Java NIO event loop
- [ ] Key expiry (`EXPIRE`, `TTL`)
- [ ] Lists and hashes
- [ ] Saving data to disk (append-only file)
- [ ] Pub/Sub
- [ ] Benchmark against real Redis
- [ ] Web dashboard (Spring Boot)

## Notes

This is a learning project and is not affiliated with Redis.