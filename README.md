# Raider Robotix Scouting Models

This repo contains data classes of user collected data, which are into classes that then calculate aggregate statistics, draw conclusions, and highlight characterisitics of team play.

Currently, this repo is for FRC 2020, Infinite Recharge.

## Why kotlin?

North Brunswick Twp HS (where FRC team 25 is based), teaches AP Comp Sci A, and therefore most programming oriented students are familiar with Java. @hybras made the decision to port over sections of Team 25's codebase in order to reduce boilerplate. While nearly all of kotlin's features can be used in Java, they are only idiomatic in kotlin.

Don't take MY word for it, peruse the classes in this project.

## Jitpack Deployment

This project is deployed to Jitpack, which generates builds / maven artifacts on demand. This library is meant to be consumed in our scouting app (data collection) and client (data processing). For convenience's sake, data processing is also in this lib, so that the client only needs to perform GUI operations.
