# fast-transfer
Client server file transfer program to utilize network efficiently by tranferring the file in parallel.

## Design
- Multithreaded server for quick connection handling.
- Uses default TCP protocol for reliable delivery.
- defines/implements custom application control protocol over TCP for client/server communication.
- Uses flexible 1024 bytes control structure (to support features in future).
- Transfers file in 5 (almost)equal parts in parallel from server to client; keeping CPU usage in mind.
- Tested with binary files to make sure integrity of transfered file.
- Uses memory mapped file for parallel read/write instead of concatenatination.

## Further improvements

- Implement dynamic file finding on server.
- Support/test multiple clients; maintain clients cache on server.
- Improve usability, have same or separate jar to start program in server/client mode with configurable params.
- Test with WAN kind of connections.
- Structure the code in OO paradigm.
- Improve logging with some logging utility.
- Make threads/parts configurable and part of control messages.
- Define control object and serialize instead of plain buffers.
- Improve the code; variable names, global constants like ports etc.

