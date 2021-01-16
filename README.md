# Assignments for Coursera's "Functional Programming in Scala"

## Table of Contents
* [Prerequistes](#prereqs)
* [Recursion (recfun)](recfun/README.md)
* [Functional Sets (funsets)](funsets/README.md)
* [Object-Oriented Sets (objsets)](objsets/README.md)

<a name="prereqs"></a>
## Prerequisites
Ensure that:
* JDK 11+ and SBT (Scala Build Tool) are installed on your system.
    * Run `java -version` to figure out if a JDK has been installed on your system, and if so, what version it is.
    * Run `sbt about` to verify that SBT is installed on your system.
* Within each assignment (each of which is a discrete directory with its own src/ and project/ folders):
    * **project/build.properties**'s `sbt.version` key has the correct SBT version (as gleaned from `sbt about` on your system). When downloaded from Coursera, this was initially 1.2.8, but my system's SBT version is 1.4.6.
    * To execute on VSCode with the Scala Metals plugin, disable the `io.get-coursier` plugin in **project/plugins.sbt**.
    * In **build.sbt**, change the Scala version to match the version of scala on your system (as determined via `sbt scalaVersion`) OR to whatever is in the warning message is thrown up by VSCode. I changed it from 2.13 to 2.13.4.
