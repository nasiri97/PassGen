package ir.ornix.passgen.core.domain.platformauth

enum class PlatformAuthenticationFailureReason {
    NotAvailable,           // no hardware, or the platform doesn't support it
    HardwareUnavailable,    // sensor exists but is temporarily unusable
    NotEnrolled,            // no biometric or screen lock set up
    TemporarilyLocked,      // too many attempts, try again shortly
    PermanentlyLocked,      // locked until the user unlocks with the screen lock
    SecurityUpdateRequired,
    Unknown
}