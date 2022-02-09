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

## Development Setup

The easiest way to get started with developing is to use Eclipse PDE/Eclipse for Committers.
We recommend that you install the development environment via the [Eclipse Installer](https://www.eclipse.org/downloads/packages/installer).
Below is a quick guide on how to set up a basic Eclipse installation:

1. Make sure you have a JDK 8 **and** JDK 11 set up on your machine.
1. Download and install/extract the [Eclipse Installer](https://www.eclipse.org/downloads/packages/installer).
1. Start the intaller.
1. Use the hamburger menu in the upper right corner to switch the installer to *Advanced Mode*
1. Select *Eclipse IDE for Eclipse Committers* from the list of available packages
1. **IMPORTANT** In the *Product Version* drop down menu, select *2020-06*
1. In the *Java 1.8+ VM* drop down menu, select your installation of JDK 11
1. Click *Next* twice
1. In the *Variable* screen, check the *Show all variables* box and configure the installation path to your liking.
1. Click *Next*
1. In the *Confirmation* click *Finish*

Depending on your internet connection speed, the installation will take some time.
During the installation process, you might be prompted as to if you would like to accept the installation of unsigned content.
This is expected and you will need to accept it in order for the installation to finish.
If you have left the *Launch automatically* box checked, your freshly installed Eclipse will start automatically when the installation finishes.
Otherwise you may start it manually afterwards.

When the Eclipse instance is lauchen, you will be asked to select a workspace.
We strongly recommend using a fresh, clean workspace to work on all things Cevelop.
Additionally, we recommend you switch to the *Plug-in Development* perspective and also enable the *Git* perspective.

The Cevelop source projects use a specific IDE configuration as well as a number of additional plug-ins.
We recommend you use the configurations found in the [development-support](https://github.com/Cevelop/development-support) repository.
Clone it somewhere and then perform the following steps to configure your IDE in accordance with the development process and guidelines of this and other source repositories of the Cevelop project:

1. From within the IDE, select the *Import...* entry from the *File* menu
1. In the newly opened window, select *Install Software Item from File* in the *Install* group and click *Next*
1. On the *Import Software Configuration* screen, select the path to the `cevelop-ide-plug-ins.p2` file within your clone of the [development-support](https://github.com/Cevelop/development-support) repository
1. **IMPORTANT** Uncheck the *Install latest version of selected software* box
1. **IMPORTANT** Check the *Contact all update sites during install to find required software* box
1. Click *Next* twice
1. On the *Review Licenses* screen, select *I accept the terms of the license agreements* and click finish

> removed kotlin and installed Kotlin plugin via market place, TPD gave error message about wrong dependency, used XText 2020...
> set up Kotlin compiler for JVM Target 11 and Language version 1.5, also JDK Home to the corresponding Zulu-11 JDK

During the installation process, you might be prompted as to if you would like to accept the installation of unsigned content.
This is expected and you will need to accept it in order for the installation to finish.
When the installation finishes, you will be prompted to restart the IDE.
Do so, in order for the newly installed plug-ins to be activated.

In order to import the code-style/formatting and debugging support configurations, follow these steps:

1. From within the IDE, select the *Import...* entry from the *File* menu
1. In the newly opened window, select *Preferences* in the *General* group and click *Next*
1. On the *Import Preferences* screen, select the path to the `cevelop-ide-settings.epf` file within your clone of the [development-support](https://github.com/Cevelop/development-support) repository
1. Make sure the *Import all* box is checked and click *Finish*

> changed references to JDT version to use most recent release 2021-12 versions
> Manually updated Maven to use homebrew mvn 3.8.4 instead of built-in

When the import finishes, you will be prompted to restart the IDE.
Do so, in order for the newly imported preferences to be activated.

Finally, make sure that the IDE knows about your JDK 8 installtion:

1. Open the IDE preferences
1. In The preferences screen, navigate to *Java -> Installed JREs*
1. Use the *Add...* button to add your JDK 8 installation
1. Navigate to *Java -> Installed JREs -> Execution Environments*
1. Select *JavaSE-1.8* from the list an check the JDK 8 box

> changed to JavaSE-11 (Zulu version on M1 Mac)
> adapted target to use most recent CDT release 10.5 - 202112
> change replace MANIFEST.MF Execution environment to JavaSE-11 (from JavaSE-1.8)
> manually click through project MANIFEST.MF and "update classpath" to get JAVASE-11 library in each plugin

You have now successfully prepared your IDE for development on the plug-in code and [ILTIS](https://github.com/Cevelop/iltis).

## Importing and Building the Plug-ins

Please be sure to finish the IDE setup, as described above, before starting work on the source code.
With the IDE setup in place, clone this repository to a location of your choosing.
You might do so either from within in the IDE or from the command line or any other tool you prefer.
Regardless of the cloning mechanism you chose, make that the clone is available within in the IDE.
If you have created the clone outside of the IDE, you may do so by switching to the *Git* perspective and clicking on *Add an existing local Git repository*.
To subsequent dialogs will guide you through the import process.

With the repository registered in IDE, right click on it and select *Import projects...*.
In the newly opened dialog, accept the default settings and click *Finish*.
Depending on the perfomance of your system, the import process might take some time, wait for it to finish.

After the import has finished, switch back to the *Plug-in Development* perspective.
You will notice that IDE has detected 25'000+ errors.
This is expected, so DON'T PANIC!
In the *Project Explorer* navigate the tree structure to either:

* Cevelop -> CevelopProject -> releng -> com.cevelop.target.master

or

* Cevelop -> CevelopProject -> releng -> com.cevelop.target.develop

depending on your currently checked-out branch.
Open the contained `.target` file, and in the newly opened editor click *Set as Active Target Platform*.
This will initiate the download and configuration of the dependencies required by the project.
The download/configuration process will take some time.
Afterwards, the IDE will automatically rebuild the project.

> NOTE: a warning/error, originating from the Kotlin plug-in, may pop up.
> It it safe to ignore this error.
> If it keeps reappearing, try to restart the IDE.

You should notice, that the previous errors are now fixed.

## Starting and Eclipse Instance with the Plug-ins

Having setup the build environment as described in the above two sections, you are now ready to launch the plug-ins in a new Eclipse instance.
Do do so, follow these steps:

1. From the *Run* menu, select *Run Configurations...*
1. In the newly opened dialog, expand the *Eclipse Application* node
1. From there, select either the *Cevelop Plug-Ins Full* or *Cevelop Plug-Ins Full + pASTa* launch configuration.
    * NOTE: the *+ pASTa* launch configuration will only work as expected if you are working with the `.develop` target platform configuration.
1. Click the *Run* button

You may be presented with a dialog warning about a missing constraint of the `org.eclipse.tools.templates.freemarker.java11` package.
This is expected and can be ignored using the *Continue* button.
A new Eclipse instance, containing all plug-ins, will be started.


## M1 Macos gotchas

* SWTBot requires permissions to access the screen. This can lead to hanging tests :-( iltis versionator test!
* Kotlin Plugin seems to work now and I have no idea why. May be because I selected the JavaSE-11 runtime? Or Jetbrains silently updated it in the Marketplace.
* need to make the internal maven available, otherwise some things do not work, tried with the brew installed maven and that was bad
