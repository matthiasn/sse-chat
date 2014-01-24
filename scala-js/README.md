# sse-chat client written in Scala.js

This is an initial attempt at rewriting the sse-chat client with
**[Scala.js](https://github.com/lampepfl/scala-js)**.

## Get started

To get started, open `sbt` in this example project, and issue the task
`packageJS`. This creates the file `target/scala-2.10/example.js` and its
siblings `example-extdeps.js` and `example-intdeps.js`.

After making changes, you will need to run `copy.sh` to make the resulting
file available to the Play project using this client.

During development, it is useful to use `~packageJS` in sbt, so that each
time you save a source file, a compilation of the project is triggered.
You will need to run `copy.sh`again after each recompilation (or you manage
to have sbt handle the copying, in which case I would like to ask you share
by submitting a pull request).

The non-optimized version will be available under this address:
**[localhost:9000/react-scalajs](http://localhost:9000/react-scalajs)**
when running play locally by executing `play run` in the parent directory.

## The optimized version

Instead of running `packageJS`, you can also run `optimizeJS` to have the
**[Google Closure Compiler](https://developers.google.com/closure/compiler/**
generate a much more compact version of the JavaScript code. You can also use
`~optimizeJS` to have the task listen for changes. However, note that this will
get your laptop warm and eat your battery fast if you make plenty of changes.

Here you can find the opmtized version:
**[localhost:9000/react-scalajs-opt](http://localhost:9000/react-scalajs-opt)**
