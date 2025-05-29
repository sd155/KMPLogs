# Project rules and policies

## Git policy

- Default and release branch is **main**.
- RC (release candidate) branche is **dev**.
- Use [Conventional Commits](https://www.conventionalcommits.org) specification for writing commit messages in a structured and consistent way.

## Project scheme
```text
/
├── kmplogs-api/                 # API module
│   ├── src/                     # Source code
│   └── build.gradle.kts         # Module build configuration
│
├── kmplogs-core/                # Core module
│   ├── src/                     # Source code
│   └── build.gradle.kts         # Module build configuration
│
├── gradle/                      # Gradle wrapper
│   └── wrapper/
│   │   └── gradle-wrapper.properties # Gradle wrapper properties
│   └── libs.versions.toml        # KMP project dependencies and properties
│
├── build.gradle.kts              # KMP project build configuration
├── gradle.properties             # KMP project gradle properties
├── gradlew                       # KMP project gradle wrapper script (for *nix)
├── gradlew.bat                   # KMP project gradle wrapper script (for Windows)
└── settings.gradle.kts           # KMP project settings
```
