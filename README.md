# Stashly

A Redis-compatible in-memory database built from scratch in Java.
(Compatible means it follows the same message format, called RESP, that Redis clients use.)

> Status: work in progress, Week 1 of a 4-week build.

## What works today

| Command | What it does |
|---|---|
| `PING [text]` | replies `PONG` (or the text you send) |
| `ECHO text` | replies with the same text |
| `SET key value` | saves a value |
| `GET key` | reads a value (null if the key is missing) |
| `DEL key [key ...]` | deletes keys, replies how many were deleted |
| `EXISTS key [key ...]` | replies how many of the keys exist |
| `INCR key` | adds 1 to a number (a missing key starts at 0) |
| `DECR key` | subtracts 1 from a number |
| `DBSIZE` | replies how many keys exist |
| `KEYS pattern` | lists keys that match, for example `KEYS *` or `KEYS user*` |
| `FLUSHALL` | deletes everything |

Also: unknown commands, wrong arguments, and `INCR` on a non-number get proper error replies instead of crashing. The parser, writer, store and commands are covered by unit tests.

## How it works

```
client -> TCP server (port 6380) -> RespParser -> CommandHandler -> Store
                                                       |
client <------------- RespWriter <---------------------+
```

- **RespParser** reads a command from the network and turns it into a list.
- **CommandHandler** decides what to do for each command. It receives its `Store` through the constructor (constructor injection).
- **Store** keeps the data in memory in a `ConcurrentHashMap`. Counters use an atomic update, so two clients can never lose a count.
- **RespWriter** builds replies in the RESP format (text, errors, numbers, lists).

## Run it

Requirements: Java 21 and the included Maven wrapper.

1. Run the tests: `.\mvnw.cmd test`
2. Start the server: run the `StashlyMain` class in IntelliJ. It listens on port 6380.
3. Try it from PowerShell:

```
. .\scripts\stashly-tools.ps1
Stash SET counter 10
Stash INCR counter
Stash GET counter
Stash KEYS *
```

## Roadmap

- [x] TCP server and RESP parser
- [x] Replies, `PING`, `ECHO`
- [x] `SET`, `GET`, `DEL`, `EXISTS`
- [x] `INCR`, `DECR`, `KEYS`, `DBSIZE`, `FLUSHALL`
- [x] Many clients at once
- [ ] Faster server with a Java NIO event loop
- [ ] Key expiry (`EXPIRE`, `TTL`)
- [ ] Lists and hashes
- [ ] Saving data to disk (append-only file)
- [ ] Pub/Sub
- [ ] Test with the real `redis-cli` and benchmark against real Redis
- [ ] Web dashboard (Spring Boot)

## Known limits (for now)

- Data is lost when the server stops (persistence is planned).
- Values are text only.

## Notes

This is a learning project and is not affiliated with Redis.