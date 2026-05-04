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

## UI implementation details & rules

- The app has its own UI Kit. Every screen should use the kit as much as possible. The kit is stored at ui/kit.
- Every single icon, color or text style must be from the UI kit. There are no unknown styles or colors (other than Color.Transparent).
- Colors are stored in `LocalTMColors.current` (ui/kit/style/Color.kt)
- Text styles are stored in `LocalTMTypography.current` (ui/kit/style/Type.kt)
- Icons are stored in ui/kit/icons, and all need `TMIcons` to be accessed
- Shapes are in `TMShapes` (ui/kit/style/Shape.kt)
- All UI Kit components are in ui/kit/components
- All strings are stored in values/strings.xml. No UI strings in code is allowed (unless they're not supposed to be translated ever).
- Before implementing any custom component, always make sure this component is not present in UI Kit already

- Every screen in the app must support edge-to-edge
- Every screen you implement should be covered with `@PreviewWrapper(TMPreviewWrapperProvider::class)` and `@Preview` annotations
- Every UI state must be restored after activity recreation. Do no use `remember` unless it's a state that is to be reset anyway.
- Every state change (other than text field's input) must be animated. If it's not clear which animation(s) to use, ask (all animations at once)

## Working rules

- Keep changes scoped to the user request; do not refactor unrelated areas.
- Match existing Kotlin and Gradle style in the touched files.
- Prefer small, targeted changes over broad rewrites.
- When modifying dependencies or build logic, update the relevant Kotlin DSL files instead of adding parallel config. Also, ask user for permission to make these modifications (via polls).
- Do not remove or overwrite user changes you did not make.
- Use consistent code style. Two generated files should not be different in code style (line break after annotation, trailing comma, etc.)

## Validation

- Prefer targeted validation first.
- For compile checks, use `./gradlew :app:compileDebugJavaWithJavac` when Java/Kotlin source changes justify it.
- If broader verification is needed, use the smallest relevant Gradle task before running full builds.

## Notes for agents

- Follow repository-local instructions over generic defaults when they conflict.
- If during the work you find something that would be good to have in this file - suggest to add it, but don't do it yourself
