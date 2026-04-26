# AGENTS.md

This repository contains the Android application for Tundra Market.

## App idea overview

The app idea is this:
- There are nomads in Russian Yamal Tundras, who eventually need supplies (food for instance)
- There are trading stations (factories) who sell those supplies
- There are only 26 trading stations across whole area, and those trading stations can be hard to reach

From one side, the app gives an opportunity for nomads to make an order which will be delivered via a drone by the trading station handling the order.
From the other side, the app lets trading stations see & handle orders which nomads created.

This is only an MVP, so there's no payment mechanism yet.

Also, since nomads are in Tundra most of the time, it's considered they have very weak Internet connection, if at all. 
Which means, nomad's role in the app must be fully offline-first (letting do everything with no Internet connection, and then syncing).  

## Project structure

- `app/` is the main Android application module.
- `buildSrc/` holds shared Gradle build logic and project constants.
- The project uses Gradle Kotlin DSL (`*.gradle.kts`).

## Tech stack

- Android native application
- Kotlin
- Jetpack Compose (Decompose)
- Ktor (REST + Protobuf)
- Room
- Koin

## Architecture

- MVVM
- Clean Architecture 
- SOLID

## Working rules

- Keep changes scoped to the user request; do not refactor unrelated areas.
- Match existing Kotlin and Gradle style in the touched files.
- Prefer small, targeted changes over broad rewrites.
- When modifying dependencies or build logic, update the relevant Kotlin DSL files instead of adding parallel config. Also, ask user for permission to make these modifications.
- Do not remove or overwrite user changes you did not make.
- Use consistent code style. Two generated files should not be different in code style (line break after annotation, trailing comma, etc.)

## Validation

- Prefer targeted validation first.
- For compile checks, use `./gradlew :app:compileDebugJavaWithJavac` when Java/Kotlin source changes justify it.
- If broader verification is needed, use the smallest relevant Gradle task before running full builds.

## Notes for agents

- Follow repository-local instructions over generic defaults when they conflict.
