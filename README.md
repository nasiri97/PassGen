# 🔐 PassGen — Kotlin Multiplatform Deterministic Password Generator

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.12.0-blue.svg?style=flat&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)
[![Platform Support](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web%20(Wasm)-green.svg?style=flat)](#running-the-apps)

**PassGen** is a modern, secure, and fully decentralized deterministic password generator built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It allows users to generate strong, cryptographically secure passwords deterministically across multiple platforms—Android, iOS, Desktop (JVM), and Web (Kotlin/Wasm & JS)—ensuring you can recreate your passwords anywhere without relying on cloud-synced credential vaults.

---

## 🚀 Key Features

*   **Deterministic Key Derivation (KDF):** Generate identical, ultra-secure passwords using a master key and specific input contexts (e.g., account name, service name).
*   **Fully Decentralized & Stateless:** No cloud synchronization or database requirements to retrieve passwords; your master key and generation configs are everything you need.
*   **Cross-Platform UI:** Beautiful, responsive UI built natively for Android, iOS, Desktop, and Web using **Compose Multiplatform** and Material 3 design systems.
*   **Industry-Standard Cryptography:** Support for advanced modern hashing algorithms (Argon2id, BCrypt, SHA-512, SHA-256) and flexible encoding encoders (Z85, Base64, Hex).
*   **Robust Modular Architecture:** Clean feature-based API/Implementation separation for high scalability, isolated testing, and codebase maintainability.
*   **Local Vaulting Optionality:** Safe local storage capability for account identifiers and metadata configuration presets with top-tier security access controls.

---

## 🛠 Cryptographic Password Generation Flow

The core generation mechanism utilizes a highly secure, multi-stage key derivation approach to map your master key and unique contextual input into a strong password string:


```mermaid
flowchart TD
%% ─────────────────────────────────────────────
%% Key Derivation / Authentication
%% ─────────────────────────────────────────────

    MK(["<b>MK</b> (Master Key)"]) -->|SHA-512| MKD(["<b>MKD</b> (Master Key Digest)<br/>(64 bytes)"])

    subgraph HMAC_STAGE["HMAC"]
        MKD --> HMAC["HMAC-SHA-512"]
        IN(["<b>Input</b>"]) --> HMAC

        HMAC -->|64 bytes| HASH(["<b>Password Seed</b>"])
    end


%% ─────────────────────────────────────────────
%% Password Hashing / KDF
%% ─────────────────────────────────────────────

    HASH --> ALG{"Hashing"}

    ALG --> SHA256["SHA-256"]
    ALG --> SHA512["SHA-512"]
    ALG --> BCRYPT["BCrypt"]
    ALG --> ARGON["Argon2id"]

    SHA256 -->|32 bytes| RAW(["<b>Raw Password</b>"])
    SHA512 -->|64 bytes| RAW
    BCRYPT -->|23 bytes| RAW
    ARGON -->|64 bytes| RAW

%% ─────────────────────────────────────────────
%% Encoding
%% ─────────────────────────────────────────────

    RAW --> ENC{"Encoding"}

    subgraph InvisibleGroup
        ENC --> HEX["Hex"]
        ENC --> BASE64["Base64"]
        ENC --> Z85["Z85"]
        ENC --> BIP39["BIP39"]
    end

    HEX --> STR(["<b>Full-Length Password</b><br/>(string)"])
    BASE64 --> STR
    Z85 --> STR

%% ─────────────────────────────────────────────
%% Final Output
%% ─────────────────────────────────────────────

    STR -->|The first n characters| OUT(["<b>Password</b>"])
    BIP39 --> OUT

%% ─────────────────────────────────────────────
%% Styling
%% ─────────────────────────────────────────────

    classDef input fill:#e8f4ff,stroke:#1976d2,stroke-width:2px,color:#0d47a1
    classDef crypto fill:#fff3e0,stroke:#ef6c00,stroke-width:2px,color:#5d2f00
    classDef algorithm fill:#f3e5f5,stroke:#8e24aa,stroke-width:2px,color:#4a148c
    classDef encoding fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#1b5e20
    classDef output fill:#fce4ec,stroke:#c2185b,stroke-width:2px,color:#880e4f

    class MK,MKD,IN input
    class HMAC,HASH crypto
    class ALG,SHA256,SHA512,BCRYPT,ARGON,RAW algorithm
    class ENC,HEX,BASE64,Z85,STR,BIP39 encoding
    class OUT output

    style InvisibleGroup fill:transparent,stroke:transparent,color:transparent
```

---

## 🏗 Modular Architecture Breakdown

PassGen is engineered with strict scalability rules following a feature-oriented and layered architecture pattern:

### Module Matrix & Responsibility

| Module / Component Layer | Description |
| :--- | :--- |
| **`:log-core`** | Central logging engine providing unified multi-platform logging hooks. |
| **`:codec`** | Lower-level binary encoding utilities implementing fast Hex, Base64, and Z85 encoders. |
| **`:hashing`** | Low-level cryptographic primitives isolating core implementations of SHA, BCrypt, and Argon2id. |
| **`:passwordGenerator`** | Pure Kotlin core domain handling deterministic generation, `KDFPassGen`, `TokenGen`, and secure random fallbacks. |
| **`:core:designsystem`** | Shared application theme, colors, fonts, shapes, and Atomic Design reusable design components. |
| **`:core:ui`** | Reusable high-level Compose components, structures, widgets, and animation canvases. |
| **`:core:domain`** | Framework-free layer containing rich use-cases (`GenerateKDFPassUseCase`, `SaveAccountUseCase`, etc.). |
| **`:core:data`** | Aggregates repositories and local state engines for configuration management and account profiles. |
| **`:core:model`** | Clean business and domain data model entities shared globally across features. |
| **`:feature:*:api`** | Contract interface definitions for features, isolating cross-feature dependencies. |
| **`:feature:*:impl`** | Concrete UI screens, ViewModels, business interaction workflows, and internal navigation rules. |
| **`:composeApp`** | Aggregated shared application Compose UI configuration layer. |
| **`:androidApp`** | Main entry-point launcher configuration and platform setup for Android. |
| **`:desktopApp`**| Main entry-point launcher setup and window target wrappers for Desktop (JVM). |
| **`:webApp`** | Compiled entry targets for both modern Wasm-JS browsers and heritage JS runtimes. |
| **`iosApp`** | Native Swift wrapper/Xcode project setting up layout entry frames hosting Compose Multiplatform. |

---

## 💻 Technical Stack & Custom Components

*   **Jetpack / Compose Multiplatform** (UI & layouts)
*   **Kotlin Coroutines & Flow** (Asynchronous stream computations & state propagation)
*   **Kotlinx Serialization** (Extensible binary and JSON serialization mechanics)
*   **KDF Core Engine:** Custom state-caching generation pipe leveraging specialized variable block bit expansion.

---

## 🚀 Building & Running the Applications

Ensure you have Android Studio Jellyfish (or newer), the Kotlin Multiplatform plugin, and Xcode installed (if compiling for iOS).

### Compilation Targets

Choose your target build platform via the IDE run widget configurations or use the native Gradle terminal tasks:

*   **Android Application:**
    ```bash
    ./gradlew :androidApp:assembleDebug
    ```
*   **Desktop Application (JVM):**
    *   *Hot Reload Dev Mode:* `./gradlew :desktopApp:hotRun --auto`
    *   *Standard Execution:* `./gradlew :desktopApp:run`
*   **Web Application:**
    *   *Wasm Target (Fast, modern browsers):* `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
    *   *JS Target (Legacy browser support):* `./gradlew :webApp:jsBrowserDevelopmentRun`
*   **iOS Application:**
    *   Open the `/iosApp` directory folder inside **Xcode** and click the `Run` button or trigger via standard Xcode simulator command chains.

---

## 🧪 Testing Execution

PassGen emphasizes extreme correctness with thorough test suites verifying cryptographic determinism and UI behavior:

*   **All Local Unit Tests:** `./gradlew test`
*   **Android Instrumental Tests:** `./gradlew connectedAndroidTest`
*   **Desktop Unit Tests:** `./gradlew :passwordGenerator:jvmTest` (or target specific modules)
*   **Web Testing:**
    *   *Wasm Runtime:* `./gradlew :passwordGenerator:wasmJsTest`
    *   *JS Runtime:* `./gradlew :passwordGenerator:jsTest`
*   **iOS Simulation Tests:** `./gradlew :passwordGenerator:iosSimulatorArm64Test`

---

## 🔒 Security Principles & Compliance

PassGen enforces a **Zero-Knowledge Architecture**:
1.  **Memory Zeroing:** Raw master keys are stored only as long as necessary within transient memory blocks and are cleared immediately after background compute operations.
2.  **Stateless Trust:** Because passwords are generated dynamically on demand, there is no master credential database that malicious actors could exploit or extract from storage.
3.  **Local Encryption Only:** Any accessory configuration metadata (such as custom account tags or lengths) saved on device remains isolated in secure local vaults under platform-backed sandboxing frameworks.

---

## 📄 License & Contributions

Contributions to PassGen are highly encouraged! Please open an issue or submit a comprehensive pull request for any performance tuning or extra algorithm extensions.

*Licensed under the MIT License — see the project repository details.*
