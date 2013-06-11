sse-chat-example
================

Today I somehow felt the sudden urge to write a chat example app after not being allowed into a session at Scala Days that I wanted to attend (the room was more than full, so that was understandable). Sitting around bored wasn't really an option, so I set myself the goal of making this application so concise that no more than 10 lines of code in Scala would be needed for this canonical example.

It actually worked. For reasons of legibility I have stretched out those lines a little, but one could bring this down to five lines of relevant application code (with one line being 122 characters long).

The application uses Server Sent Events for delivering messages to the client and REST calls for sending messages to the server. The central information hub is a Concurrent.broadcast and dispatching to the correct chat room is done with a filtering Enumeratee.