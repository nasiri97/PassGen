# Architecture Refactoring and Bug Fixes Plan

The goal is to refactor the project to follow a Feature-Sliced Clean Architecture pattern, separating UI and business logic (ViewModels) into distinct files and packages. Additionally, several build and logic bugs will be addressed.

## User Review Required

> [!IMPORTANT]
> - **Package Reorganization**: I will move Screen files to `ui` sub-packages and ViewModels to `presentation` sub-packages within each feature's `impl` module.
> - **Dependency Update**: I will update the `compileSdk` to 37 to resolve AAR metadata conflicts with Compose dependencies.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Hashem/source/KMP/PassGen/gradle/libs.versions.toml)
- Update `android-compileSdk` and `android-targetSdk` to `37`.
- Update `multiplatform-settings` dependency to use the standard module.

### Core Data Fixes

#### [MODIFY] [SettingsMasterKeyRepository.kt](file:///C:/Users/Hashem/source/KMP/PassGen/core/data/src/commonMain/kotlin/ir/ornix/passgen/core/data/SettingsMasterKeyRepository.kt)
- Fix unresolved reference for `Settings` (if updating dependency doesn't fix it automatically).

---

### Feature Setup Refactoring

#### [NEW] [SetupViewModel.kt](file:///C:/Users/Hashem/source/KMP/PassGen/feature/setup/impl/src/commonMain/kotlin/ir/ornix/passgen/feature/setup/impl/presentation/SetupViewModel.kt)
- Move `SetupViewModel` logic from `SetupScreen.kt`.

#### [MODIFY] [SetupScreen.kt](file:///C:/Users/Hashem/source/KMP/PassGen/feature/setup/impl/src/commonMain/kotlin/ir/ornix/passgen/feature/setup/impl/ui/SetupScreen.kt)
- Move to `ui` package.
- Remove `SetupViewModel` definition.
- Keep `SetupScreen` and `SetupContent`.

---

### Feature Home Refactoring

#### [NEW] [HomeViewModel.kt](file:///C:/Users/Hashem/source/KMP/PassGen/feature/home/impl/src/commonMain/kotlin/ir/ornix/passgen/feature/home/impl/presentation/HomeViewModel.kt)
- [NEW] Create a standard ViewModel for the Home feature.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Hashem/source/KMP/PassGen/feature/home/impl/src/commonMain/kotlin/ir/ornix/passgen/feature/home/impl/ui/HomeScreen.kt)
- Move to `ui` package and wire up the new ViewModel.

---

### Other Features (About, Settings, Saved Passwords)

#### [MODIFY] All Screen files
- Move to `ui` package.
- Create corresponding ViewModels in `presentation` package for each feature.

---

### Application Wiring

#### [MODIFY] [App.kt](file:///C:/Users/Hashem/source/KMP/PassGen/composeApp/src/commonMain/kotlin/ir/ornix/passgen/composeapp/App.kt)
- Fix the bug where `SetupScreen` was called without a `repository`.
- Update imports for the new Screen locations.

## Verification Plan

### Automated Tests
- Run `gradlew build` to ensure all modules compile correctly with `android-37` and the new architecture.

### Manual Verification
- Deploy to an Android emulator to verify the navigation and Master Key setup flow works correctly with the separated ViewModel.
