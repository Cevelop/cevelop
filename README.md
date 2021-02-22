# Cevelop Plug-ins

This repository contains most plug-ins that make up Cevelop.
This document describes the structure of the projects, as well as the workflow used.
Please adhere to the guidelines described in this document to ensure a smooth development process.

Additionally, please take your time to update this and other documentation files if things change.
There are few things worse than an out-of-date documentation.

## Structure

The project layout follows the following conventions:

* Top-level helpers, configurations, and this file are located at the repository root
* All plug-in code resides in the folder *Plug-InsProject* in the repository root

### Helpers and Configuration

The helpers and configurations that reside at the root of the repository are described in this section.

#### Maven Build Helper

The project includes a simple helper script, to make it easy to run *Maven* builds of specific bundles/tests of the project.
The script is designed to run on Linux systems, since it is used in the continuous integration environment.
To get started, simply call `./releng-tools/build` in the repository root directory.
This will perform a complete build of all bundles in the project, as well as execute all tests.
For more information, consult the output of `./releng-tools/build -h`.

Some settings of the build helper can be configured using the file `build.ini`, while others are passed directly on the command line.