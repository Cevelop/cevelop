################
Cevelop Plug-ins
################

This repository contains most plug-ins that make up Cevelop. This document
describes the structure of the projects, as well as the workflow used. Please
adhere to the guidelines described in this document to ensure a smooth
development process. Note that this document does not describe code style or
formatting. Please refer to the `SLOTH IDE Configuration <https://gitlab.dev.ifs.hsr.ch/SLOTH/configuration>`_
for Eclipse configuration presets including save-actions and code-formatter
settings used in this repository.

Additionally, please take your time to update this and other documentation files
if things change. There are few things worse than an out-of-date documentation.

.. contents:: Table of Contents
   :depth: 2

Structure
#########

The project layout follows the following conventions:

* Top-level helpers, configurations, and this file are located at the repository
  root
* Further documentation is located in the folder ``docs`` in the repository root
* All plug-in code resides in the folder ``CevelopProject`` in the repository
  root

Helpers and Configuration
=========================

The helpers and configurations that reside at the root of the repository are
described in this section.

Maven Build Helper
------------------

The project includes a simple helper script, to make it easy to run *Maven*
builds of specific bundles/tests of the project. The script is designed to run on
Linux systems, since it is used in the continuous integration environment. To
get started, simply call ``./tools/build`` in the repository root directory. This will
perform a complete build of all bundles in the project, as well as execute all
tests. For more information, consult the output of ``./build -h``.

Some settings of the build helper can be configured using the file
``build.ini``, while others are passed directly on the command line.

Gitlab CI Configuration
-----------------------

The Plug-ins repository uses Gitlab CI for continuous integration builds and
deployment. The configuration file is called ``.gitlab-ci.yml`` and lives in the
repository root. Since the project employs a highly parallel pipeline for
testing, you should not change the configuration manually, but rather
use the `Maven Build Helper`_ to generate the configuration **NOTE: NOT YET
IMPLEMENTED!**. This ensures that all test are executed during the
execution of the CI pipeline.

Workflow
########

All development in this repository follows a **git-flow**-*eseque* workflow.
This section describes the general worklow that all developers should adhere
to. In addition, `Student Project Workflow`_ describes the specifics/details
that are of importance to students working on Cevelop as a term-project or
bachelors thesis.

Branches and Commits
====================

In general, the repository has two protected branches, ``master`` and
``develop``. The purpose of both of these branches is described in `Master`_ and
`Develop`_ respectively. Additionally, there might be ``feature/``,
``archive/``, and ``release/`` branches. Their purpose is described in their
respective sections later on. Here are some general guidelines that you should
adhere to:

Regardless of the branch you are committing to, keep your commits small.
  Besides being "the right thing to do", keeping your commits as small as
  possible helps when trying to find hidden issues later on. If you ever
  bisected a repository containing large (200+ lines changed) commits, you know
  the pain.

Only include related changes within one commit.
  If you happen to change a lot of unrelated things at once, ``git commit -p``
  is your friend. Isolating changes makes it easier to track down problems and
  to review a change.

Write descriptive commit messages.
  Outline a broad description of your change in the first line of your commit,
  and keep it short (<=50 characters). If you can't do that, your commit is
  most likely too big. If your commit does need further explanations, leave an
  empty line after the commit summary and then explain in complete sentences
  what needs to be explained. Wrap these explanations at 72 characters for easy
  viewing.

Use present-tense in your commit messages.
  Commits are "things" that do stuff. For example, when adding a dialog window,
  the commit does not "added dialog window XYZ" (the bad grammar, intented it
  is!), but rather it does "add dialog window XYZ".

Rebase instead of merge
  If you are "out of sync" on your current branch, e.g. you have commits that
  you have not yet published but the remote branch has moved on already, please
  consider rebasing your changes onto the remote `HEAD` of your branch. Merge
  commits are ugly and should only ever be used in certain situations, like
  merging a ``feature/`` back into ``develop``.

(optional) Prefix your commit messages
  Sometimes, it is nice to just have to process a very small number of charaters
  in order to get an idea on what a commit is about. Prefixing your commit with
  a "tag" that describes the subsystem/plug-in/configuration you touched is
  a nice way to give an idea about the nature of a commit. For example, if you
  changed the CI configuration, prefix your commit with 'ci:', so everybody
  knows you worked on something related to CI. You don't have to follow that
  rule, but it would be a nice touch :)

A Note on ``push -f``
---------------------

We all know, that you can forcefully push a branch update in git. However,
**force push**-ing lands you and your fellow developers in a world of pain. If you
ever feel the need to **force push** to one of the protected branches, please
consider why. There are very few legitimate reasons to ever **force push** to a
branch. If you don't know these reasons, you probably don't want to perform a
**force push**. More than likely, you screwed up on your side, and consequently
you should fix the problem/conflict on your side! Also, if you can't remove the
branch protection yourself, you most certainly don't actually want to perform a
**force push** (NOTE: this does not mean that force pushing is legitimate just
because you can unprotect the branch!). If you still feel the need of **force
push**-ing to the repository, please consult with one of the project masters or
owners first. They will explain to you why you don't actually want to **force
push**.

Master
------

The ``master`` branch is where releases happen. There is no reason to commit to
the ``master`` branch outside of a scheduled release or a hotfix. When preparing
a release, first merge back the ``develop`` branch and then start with the
release specific tasks like bumping version numbers, or switching update sites.
All other development activity should happen on the ``develop`` branch or a
``feature/`` branch. Builds on the ``master`` branch are signed and released
into the current release's update-site.

Develop
-------

General development, e.g. regular bugfixes and small changes to plug-ins,
happens on the ``develop`` branch. Please don't perform large architectural
rework, e.g. a task that takes more than a day of work, on the ``develop``
branch, but instead use a ``feature/`` branch. Builds on the ``develop`` branch
are signed and released into the ``unstable`` update-site.

Feature Branches
----------------

Feature branches are prefixed by ``feature/``. If you work on larger changes,
as in they take more than a day to finish, consider using a feature branch. This
makes it easy to merge them back when you are finished, without having to rebase
a large number of commits when you are done. They are also a good way to get
your code reviewed if you are unsure about your changes. When merging a
``feature/`` branch containing 5+ commits back into the the ``develop`` branch,
consider creating a "non-fast-forward" merge, and provide an overview of what
you have been working on. Builds on any ``feature/`` branches are not signed and
also not deployed anywhere. If you want to share your changes in binary form,
consider packing an update site archive yourself.

Archive Branches
----------------

Archive branches, prefixed by ``archive/``, are a useful tool to archive work
that you have abandoned for whatever reason, but want to keep around for future
reference or other purposes. There is generally no reason for committing changes
to an ``archive`` branch, but you are free to do so anyway. Please choose a
descriptive name for any branches you want to archive, so it is easy to see
what changes or features are archived. Remember: branches in git are cheap, your
time is not. So prefer archiving your work instead of destroying it.

Student Project Workflow
########################

This section outlines some rules and tips regarding student projects (SA/BA/PA).
In addition to the workflow outlined above, you (as a student) will have to
adhere to the additional rules described here.

Getting Started
===============

Getting started with plug-in development for Cevelop can be scary at first.
DON'T PANIC! This section provides some tips to get you going as fast as
possible.

Rely On ILTIS
-------------

Eclipse itself, as well as Eclipse CDT can feel overwhelming at first. Besides
that, some interfaces and structures are cumbersome to use. To combat
reinventing the same abstractions time and time again, a huge amount of work has
been put into creating an abstraction layer called ILTIS that does most of the
heavy lifting for you. **Use it!**. ILTIS is included in the target platform
definition of Cevelop, so there is nothing to set up. You can find the
documentation **[INSERT DOCUMENTATION LINK HERE]**.

Use An up-to-date Eclipse PDE Installation
------------------------------------------

To build plug-ins for Cevlop, you will need either Eclipse PDE or Eclipse for
Committers. **Do not** try to retrofit an existing Eclipse installation, but
rather download the latest release of either Eclipse PDE or Eclipse for
Committers from the Eclipse Website.

Use A Clean Workspace
---------------------

**Do not** use your existing Eclipse Workspace. The dependency resolution
process employed during plugin development is somewhat intrusive, so we strongly
suggest you use a clean workspace for your project.

Branches
========

You will be working in this repository, not your own fork. Therefore it is
important that you follow the branch model described in this section, in order
to keep the history clean.

Main Feature Branch
-------------------

You will be working on your own ``feature`` branch, named after the title of
your project. For example, if the title of your project is *Evaluator -
Interactive evaluation of compile-time expressions*, your main development
branch will be called ``feature/evaluator``.

Additional Feature Branches
---------------------------

You are free, and encouraged, to create additional branches as needed. Prefix
these branches in a similar manner as your main development branch. For example,
if you create a new branch to work on your data model, call the branch
``feature/evaluator-data-model``. If you are finished with a sub-feature, merge
the branch back into your main development branch. If you do not feel the need
to keep short-lived feature branches around, you are free to delete them.

Merging Back To Develop
-----------------------

If you, or your advisor or supervisor, feel that your project is at a point that
warrants merging it back into the global development branch, open a merge
request in the Gitlab web user interface. In your merge request, explain what
you have already implemented and select your **technical advisor**, **not** your
supervisor, as the assignee for the merge request. *Tip:* Make sure that all of
your tests are green prior to requesting the merge, otherwise you request will
be rejected.
