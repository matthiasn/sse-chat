# sse-chat client written in Scala.js

This is an initial attempt at rewriting the sse-chat client with
**[Scala.js](https://github.com/lampepfl/scala-js)**.

## Get started

There are two important sbt tasks you need in the development process:

1) `sbt packageJS` will package your application including the entire Scala
standard library translated into JavaScript. This creates the file
`target/scala-2.10/example.js` and its siblings `example-extdeps.js` and
`example-intdeps.js`This task is fast as it only needs
to compile the project source code and nothing else. However this really only works
during development on your local machine because the browser will need to download
almost 20 MB for this.

2) `sbt optimizeJS` run the Google Closure Compiler over your application, resulting
in a file that is compact enough for production usage. For example this application
is 300 KB after this step and compresses down to about 60 KB. This step is slow, so you
probably don't want to run this every single time something changes.

After running each of these steps, you will need to run `copy.sh` to make the resulting
file available to the Play project using this client.

During development, it is useful to use `~packageJS` in sbt, so that each
time you save a source file, a compilation of the project is triggered.
You will need to run `copy.sh`again after each recompilation (or you manage
to have sbt handle the copying, in which case I would like to ask you share
by submitting a pull request). You can also use `~optimizeJS` to have the task listen
for changes. However, note that this will get your fans spinning and drain your battery
fast if you make plenty of changes.

The non-optimized version will be available under this address:
**[localhost:9000/react-scalajs](http://localhost:9000/react-scalajs)**
when running play locally by executing `play run` in the parent directory.

Here you can find the optimized version:
**[localhost:9000/react-scalajs-opt](http://localhost:9000/react-scalajs-opt)**
