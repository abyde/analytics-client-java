Analytics python client (Beta)
==============================

Create tables and cubes, insert data and query aggregates with Python!


Requirements
------------

ant


Building
--------

```
$ ant
```

The build output is in the out/ directory. It contains the Acunu Analytics Java
API and jbird-perf, a tool to quickly insert and query random random while
choosing the "randomness" of each inserted event.

Jbird-perf uses the Acunu Analytics Java API and is a good start if you want to
see how one can code your own tools with it.


Random event insertion
----------------------

To run the performance tool example, first import the tables in summer on your node:
```
yournode$ summer < perf-examples.aql
```
or 
```
yournode$ summer # And copy/paste the content of perf-examples.aql
```


Then insert some "random" events:

```
$ out/acunu-java-client/perf/perf-example HOST [PORT]
```

