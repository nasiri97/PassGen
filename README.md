# 🔐 PassGen — Stateless Deterministic Password Generator

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.12.2-blue.svg?style=flat&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)
[![Platform Support](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web%20(Wasm%2FJS)-green.svg?style=flat)](#-building--running-the-applications)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

**PassGen** is a modern, security-first, fully decentralized **Deterministic Password & Token Generator** built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**.

Unlike traditional password managers that store encrypted password vaults in cloud servers or local files, **PassGen is completely stateless**. It generates strong, cryptographically secure passwords on demand using a Master Key and a contextual input (such as an account name or service URL). Given the same inputs, PassGen always produces the exact same password on any device—without ever saving passwords to disk or syncing them across networks.

---

## 💡 What PassGen Is For & The Problem It Solves

### The Problem with Traditional Password Managers
* **Vault Exposure & Data Leaks:** Storing passwords in encrypted databases (cloud or local) creates a single point of failure. If a cloud service is breached or your local database file is stolen, all your credentials are at risk.
* **Sync & Lock-in Issues:** Accessing your passwords requires sync infrastructure, cloud accounts, master password databases, or platform-specific vault applications.
* **Backup & Loss Risks:** Losing your encrypted vault backup or getting locked out of a cloud account means losing access to all your credentials permanently.

### How PassGen Solves This
PassGen adopts a **Zero-Knowledge, Stateless Architecture**:
1. **Zero Database / Zero Storage:** PassGen never saves your passwords anywhere. There is no password vault to breach, backup, or synchronize.
2. **Deterministic On-Demand Generation:** Enter your Master Key + account name (e.g., `github.com`), and PassGen dynamically derives your password using hardware-backed cryptographic key derivation functions (KDF).
3. **Cross-Device Consistency:** Re-create your exact password anywhere—on Android, iOS, Windows, macOS, Linux, or directly inside a web browser—using identical master inputs.
4. **Decentralized Security:** You only need to remember your single Master Key. Your credentials exist purely as mathematical derivations, impossible to steal from storage because they are never stored.

---

## 📖 How to Use PassGen

### 1. Create a Password Generator Configuration
Choose and remember a single secret **Master Key**. It is the foundation for generating all your passwords.

On the **home page**, create a **Password Generator Configuration** using your **Master Key**. You can also customize settings such as the configuration name, password generation strategy, password length, and other options.

Your **configuration** is stored locally on your device. Your **Master Key** is also protected on your device and is never uploaded to or stored on any external server.

<br/>

<div align="center">
  <table>
    <tr>
      <td align="center" width="25%">
        <img src="assets/screenshots/Create_Config_1.jpg" alt="Creating configuration (step 1)" width="100%" />
        <br/><sub><b>Creating configuration (step 1)</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Create_Config_2.jpg" alt="Creating configuration (step 2)" width="100%" />
        <br/><sub><b>Creating configuration (step 2)</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Create_Config_3.jpg" alt="Configuration created" width="100%" />
        <br/><sub><b>Configuration created</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Create_Config_4.jpg" alt="Password generated for empty Context Input" width="100%" />
        <br/><sub><b>Password generated for empty Context Input</b></sub>
      </td>
    </tr>
  </table>
</div>

<br/>

### 2. Enter Context Input
Once your **configuration** is ready, you can use it to generate all your passwords. Simply enter a specific **Context Input**, such as a service name, website, application name, username, or any other text, and **PassGen** will generate the corresponding password.

For example, to generate a password for your Google account, you can use any of the following **Context Inputs**:\
• `google`\
• `google 123`\
• `google.com user@gmail.com`

<br/>

<div align="center">
  <table>
    <tr>
      <td align="center" width="33%">
        <img src="assets/screenshots/Context_Input_1.jpg" alt="Context Input: Service Name" width="100%" />
      </td>
      <td align="center" width="33%">
        <img src="assets/screenshots/Context_Input_2.jpg" alt="Context Input: Service + Variation" width="100%" />
      </td>
      <td align="center" width="33%">
        <img src="assets/screenshots/Context_Input_3.jpg" alt="Context Input: Domain + Username" width="100%" />
      </td>
    </tr>
  </table>
</div>

<br/>

### 3. Same Password, Everywhere
That's it! Using the same **configuration** and **Context Input** will always produce the same password, even across different devices (Android, iOS, Desktop, Web)

<br/>

<div align="center">
  <img src="assets/screenshots/Same_Password_Everywhere.png" alt="Same Password Everywhere Across Devices" width="85%" />
  <br/><sub><b>Cross-Device Consistency:</b> Deriving identical passwords across Android, iOS, Desktop, and Web</sub>
</div>

<br/>

### 4. How Autofill Works
Saved **credentials** are used to automatically fill usernames and passwords in other apps and websites.

To match credentials accurately, set the target **Application Package Name** (e.g., com.example.app), **Web Domain** (e.g., example.com), or **Keywords** when creating or editing a credential.

Make sure the **Autofill Service** is enabled in your system settings and set to **PassGen** for autofill to function.

<br/>

<div align="center">
  <table>
    <tr>
      <td align="center" width="25%">
        <img src="assets/screenshots/Credential_1.jpg" alt="Creating new credential" width="100%" />
        <br/><sub><b>Creating new credential</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Credential_2.jpg" alt="Setting web domain" width="100%" />
        <br/><sub><b>Setting web domain</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Credential_3.jpg" alt="Credential created" width="100%" />
        <br/><sub><b>Credential created</b></sub>
      </td>
      <td align="center" width="25%">
        <img src="assets/screenshots/Credential_4.jpg" alt="Enable Autofill" width="100%" />
        <br/><sub><b>Enable Autofill</b></sub>
      </td>
    </tr>
  </table>
</div>

---

## 🛠 How PassGen Works (Cryptographic Pipeline)

PassGen offers two distinct modes of password generation: **Deterministic KDF Generation** and **Secure Random Generation**.

### 1. Deterministic KDF Pipeline (Key Derivation Function)

When generating a deterministic password, PassGen routes your Master Key and contextual input through a secure, multi-stage cryptographic pipeline:

```mermaid
flowchart TD
%% ─────────────────────────────────────────────
%% Key Derivation / Authentication
%% ─────────────────────────────────────────────

    MK(["🔑 Master Key (<b>MK</b>)"]) -->|SHA-512| MKD(["Master Key Digest</br>(<b>MKD</b>: 64 Bytes)"])

    CIN(["Context Input</br>(e.g. 'user@gmail.com')"]) --> NORMALIZE["• Trim Leading/Trailing Spaces<br/>• Collapse Multiple Spaces<br/>• Convert to Lowercase"]
    NORMALIZE --> NCIN(["Normalized Context Input"])

    subgraph HMAC_STAGE["HMAC"]
        MKD --> HMAC["HMAC-SHA-512"]
        NCIN --> HMAC
        HMAC -->|64 bytes| HASH(["<b>Password Seed</b></br>(64 Bytes)"])
    end


%% ─────────────────────────────────────────────
%% Password Hashing / KDF
%% ─────────────────────────────────────────────

    HASH --> ALG{"Hashing"}

    ALG --> SHA256["SHA-256"]
    ALG --> SHA512["SHA-512"]
    ALG --> BCRYPT["BCrypt"]
    ALG --> ARGON["Argon2id"]

    SHA256 -->|32 bytes| RAW(["<b>Raw Entropy Bytes</b>"])
    SHA512 -->|64 bytes| RAW
    BCRYPT -->|23 bytes| RAW
    ARGON -->|64 bytes| RAW

%% ─────────────────────────────────────────────
%% Encoding
%% ─────────────────────────────────────────────

    RAW --> ENC{"Encoding"}

    subgraph InvisibleGroup
        ENC --> HEX["Base16 (Hex)"]
        ENC --> BASE64["Base64 (Standard)"]
        ENC --> Z85["Base85 (Z85)"]
        ENC -->|The first N required bytes| BIP39["BIP39 Seed Phrases</br>(12-24 Words)"]
    end

    HEX --> STR(["<b>Full Encoded Token String</b>"])
    BASE64 --> STR
    Z85 --> STR

%% ─────────────────────────────────────────────
%% Final Output
%% ─────────────────────────────────────────────

    STR -->|Truncate to N Chars| FINAL(["🔓 <b>Password</b>"])
    BIP39 --> |Full Mnemonic String| FINAL

%% ─────────────────────────────────────────────
%% Styling
%% ─────────────────────────────────────────────

    classDef input fill:#e8f4ff,stroke:#1976d2,stroke-width:2px,color:#0d47a1
    classDef crypto fill:#fff3e0,stroke:#ef6c00,stroke-width:2px,color:#5d2f00
    classDef algorithm fill:#f3e5f5,stroke:#8e24aa,stroke-width:2px,color:#4a148c
    classDef encoding fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px,color:#1b5e20
    classDef output fill:#fce4ec,stroke:#c2185b,stroke-width:2px,color:#880e4f

    class MK,MKD,NCIN,NORMALIZE,CIN input
    class HMAC,HASH crypto
    class ALG,SHA256,SHA512,BCRYPT,ARGON,RAW algorithm
    class ENC,HEX,BASE64,Z85,STR,BIP39 encoding
    class FINAL output

    style InvisibleGroup fill:transparent,stroke:transparent,color:transparent
```

#### Detailed Step Breakdown:
1. **Master Key Digest (MKD) Derivation:** The raw Master Key (MK) is converted into a 64-byte digest: `MKD = SHA-512(MK)`. The MKD is registered securely using platform-native secure key storage (Android Keystore with StrongBox/TEE, iOS Keychain, or secure JVM/Web memory). The raw Master Key is wiped immediately from mutable memory.
2. **Context Input Preprocessing:** Context strings (e.g., service URL or account name) pass through configurable preprocessing rules:
   * **Trim Whitespace:** Removes leading and trailing whitespace characters.
   * **Collapse Spaces:** Reduces multiple consecutive spaces into a single space.
   * **Convert Lowercase:** Converts character strings to lowercase for input consistency.
3. **Password Seed Generation:** The preprocessed context and registered MKD pass into HMAC-SHA512 to produce a 64-byte authenticated **Password Seed**:
   $$\text{Password Seed} = \text{HMAC-SHA512}(\text{Key} = \text{MKD}, \text{Message} = \text{UTF-8}(\text{Normalized Input}))$$
4. **Key Derivation & Hashing:** The Password Seed is hashed using the selected algorithm to yield raw entropy bytes:
   * **SHA-256:** Produces 32 bytes of deterministic entropy.
   * **SHA-512:** Produces 64 bytes of deterministic entropy.
   * **BCrypt:** High memory/CPU complexity (Cost 12) using a salt derived from `SHA-256(Input)`.
   * **Argon2id:** Industry gold-standard memory-hard KDF (4 iterations, 128 MB RAM, 1 parallelism thread, 64-byte output).
5. **Encoding:** The derived entropy bytes are formatted using the requested output encoder:
   * **String Encoders:** Hex (Base16), Base64, Z85 (Base85).
   * **Seed Encoders:** BIP39 Mnemonic Seed Phrases (12, 15, 18, 21, or 24 words).
6. **Truncation & Output:** String-encoded tokens are truncated to the configured `passwordLength`. Seed phrase encoders return the full mnemonic word sequence.
7. **Immediate Memory Zeroing:** All intermediate mutable byte arrays (`passwordSeed`, raw master key buffers) are cleared (`fill(0)`) immediately after execution.

---

### 2. Cryptographically Secure Random Generation Pipeline

For cases where deterministic generation is not required (e.g., creating temporary Wi-Fi passwords, one-off tokens, or fresh crypto wallet seeds):
* **Entropy Source:** Uses platform-native CSPRNG (`java.security.SecureRandom` on Android/JVM, `SecRandomCopyBytes` on iOS, `crypto.getRandomValues` on Web).
* **Configurable Output:** Supports all string and seed encoders (Hex, Base64, Z85, BIP39) with custom password length controls and live entropy/strength calculations.

---

## 🧰 Cryptographic Options Reference

### Hashing & KDF Algorithms

| Algorithm | Key / Identifiers | Parameters / Behavior | Output Entropy |
|:---|:---|:---|:---|
| **SHA-256** | `INPUT_HASHER_SHA256` | Direct SHA-256 digest of input seed | 32 Bytes (256 bits) |
| **SHA-512** | `INPUT_HASHER_SHA512` | Direct SHA-512 digest of input seed | 64 Bytes (512 bits) |
| **BCrypt** | `INPUT_HASHER_BCRYPT` | Cost factor: `12`<br/>Salt: First 16 bytes of `SHA-256(Input)` | 23 Bytes |
| **Argon2id** | `INPUT_HASHER_ARGON2ID` | Iterations: `4`<br/>Memory: `128 MB`<br/>Parallelism: `1`<br/>Salt: First 16 bytes of `SHA-256(Input)` | 64 Bytes (512 bits) |

### Encoders & Output Formats

| Encoder Class | Format | Description | Length / Word Count |
|:---|:---|:---|:---|
| **HexPassEncoder** | Base16 | Standard hexadecimal string (`0-9`, `a-f`) | Truncated to `passwordLength` |
| **Base64PassEncoder** | Standard Base64 | Alphanumeric + standard Base64 characters (`A-Z`, `a-z`, `0-9`, `+`, `/`) | Truncated to `passwordLength` |
| **Z85PassEncoder** | Base85 (Z85) | High-density ASCII text format for binary data | Truncated to `passwordLength` |
| **Bip39L12PassEncoder** | BIP39 Seed | 12 Mnemonic Words | 128-bit entropy |
| **Bip39L15PassEncoder** | BIP39 Seed | 15 Mnemonic Words | 160-bit entropy |
| **Bip39L18PassEncoder** | BIP39 Seed | 18 Mnemonic Words | 192-bit entropy |
| **Bip39L21PassEncoder** | BIP39 Seed | 21 Mnemonic Words | 224-bit entropy |
| **Bip39L24PassEncoder** | BIP39 Seed | 24 Mnemonic Words | 256-bit entropy |

---

## ✨ Features & User Experience

* **🏠 Home Screen (Deterministic Generator):**
  * Live Master Key input field with secure visibility toggles.
  * Fast context/service selection.
  * Real-time password strength meter (entropy bit calculation & strength indicators).
  * Quick copy-to-clipboard with auto-clear safety indicators.
* **🎲 Random Password & Seed Generator:**
  * One-tap generation of randomized passwords and cryptographic seed phrases.
  * Customizable encoder rules and character length sliders.
* **⚙️ PassGen Configurations & Presets:**
  * Create, edit, and save custom generation profiles.
  * Choose preferred hashing algorithm (Argon2id, BCrypt, SHA-512, SHA-256), encoder type, length, and text pre-processing rules.
* **📂 Saved Accounts / Services:**
  * Save non-sensitive account metadata (e.g., Service Name: `GitHub`, Account: `user@example.com`, Profile: `High-Security Preset`).
  * Quickly regenerate passwords for specific accounts without manually entering options every time.
* **🔒 Local Security & Biometric Lock:**
  * Protect app entry with a personal PIN / Passcode or hardware Biometrics (Fingerprint, Touch ID, Face ID).
  * Auto-lock gate on app cold start and timeout.
* **🌐 Multilingual & Adaptive UI:**
  * Built-in multi-language support: **English** and **Persian (فارسی)**.
  * Full **RTL (Right-to-Left)** and **LTR** layout direction switching.
  * Dark Mode, Light Mode, and System Theme auto-adaptation.

---

## 🏗 Modular Architecture Breakdown

PassGen follows **Clean Architecture** principles and a strict **Feature-Based Multi-Module** structure:

```
PassGen
 ├── androidApp/            # Android target launcher & platform setup
 ├── iosApp/                # iOS Xcode project hosting Compose Multiplatform
 ├── desktopApp/            # Desktop (JVM) target launcher & window configuration
 ├── webApp/                # Web targets (Kotlin/Wasm & Kotlin/JS)
 ├── composeApp/            # Aggregated shared UI, DI, and App navigation
 ├── core/
 │    ├── common/           # Cryptographic codecs (BIP39, Z85, Hex, Base64), Hasher implementations, PassGen core
 │    ├── data/             # Repositories (Settings, Credentials, Configurations, Platform HMAC Signers)
 │    ├── database/         # Local persistence layer & SQLDelight database schemas
 │    ├── domain/           # Use cases (GenerateKDFPassUseCase, LocalAuth, AppConfig)
 │    ├── model/            # Business models (PassGenConfig, Password, Credential, PreprocessConfig)
 │    ├── designsystem/     # Design system tokens, colors, typography, and themes
 │    ├── ui/               # Reusable Jetpack Compose components & resource bindings
 │    └── logging/          # Multiplatform logging engine
 └── feature/               # Isolated feature modules (api + impl separation)
      ├── home/             # Deterministic password generation UI & ViewModel
      ├── random/           # Random password generator UI & ViewModel
      ├── config/           # Preset configuration management UI
      ├── credentials/      # Saved credentials & Autofill management UI (package names, web domains, keywords)
      ├── localauth/        # Local security PIN / Password setup
      ├── unlock/           # App lock screen & Biometric verification
      ├── settings/         # App preferences (Theme, Language, Security)
      └── about/            # App metadata & version info
```

---

## 💻 Tech Stack & Open-Source Libraries

* **Language:** [Kotlin](https://kotlinlang.org/) (Multiplatform 2.4.0)
* **UI Framework:** [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) (Material 3)
* **Dependency Injection:** [Koin](https://insert-koin.io/) & `koin-compose`
* **Navigation:** [Jetpack Navigation 3](https://developer.android.com/guide/navigation) (`androidx.navigation3`)
* **Asynchrony & State:** Kotlin Coroutines & `StateFlow`
* **Local Storage:** `multiplatform-settings` (Key-Value persistence for preferences & account metadata)
* **Serialization:** `kotlinx.serialization`
* **Cryptography:** Platform-native crypto primitives (Android Keystore, StrongBox/TEE, iOS Keychain, CommonCrypto, WebCrypto API)

---

## 🚀 Building & Running the Applications

### Prerequisites
* **Android Studio:** Jellyfish (2024.1.1) or newer with Kotlin Multiplatform plugin.
* **JDK:** Java 17 or higher.
* **Xcode:** Version 15+ (required only for building and running on iOS).

### Run Commands

#### Android
```bash
# Build & Install Debug APK
./gradlew :androidApp:installDebug
```

#### Desktop (JVM)
```bash
# Run Desktop Application
./gradlew :desktopApp:run

# Run with Hot Reload Dev Mode
./gradlew :desktopApp:hotRun --auto
```

#### Web (Kotlin/Wasm & JS)
```bash
# Run WebAssembly (Wasm) target (Modern Browsers)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Run JavaScript target (Legacy Browsers)
./gradlew :webApp:jsBrowserDevelopmentRun
```

#### iOS
Open `iosApp/iosApp.xcworkspace` in **Xcode**, select a simulator or connected physical device, and press `Cmd + R` to run.

---

## 🧪 Running Tests

PassGen maintains test suites to ensure cryptographic consistency across platforms:

```bash
# Run unit tests across common and platform modules
./gradlew test

# Run Android instrumental UI & integration tests
./gradlew connectedAndroidTest
```

---

## 🛡️ Security Principles & Compliance

1. **Zero Password Storage:** PassGen never stores passwords. Passwords are derived on demand in memory and disappear when discarded.
2. **Memory Protection:** Sensitive byte arrays are zeroed out (`ByteArray.fill(0)`) immediately after generation to minimize exposure window in heap memory.
3. **Hardware Key Isolation:** Master Key Digest HMAC operations leverage hardware security modules (TEE / StrongBox) where available on supported mobile hardware.
4. **Stateless Portability:** You are never locked into a single app or server. As long as you know your Master Key and configuration settings, you can derive your exact passwords using any standard implementation of PassGen.

---

## 📄 License

This project is licensed under the **Apache 2.0 License** — see the repository for details.
