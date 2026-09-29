package ir.ornix.passgen.core.model

enum class PasswordStrengthLevel(
    val label: String,
    val ratio: Float
) {
    Fragile(label = "Fragile", ratio = 0.1F),
    Weak(label = "Weak", ratio = 0.28F),
    Fair(label = "Fair", ratio = 0.46F),
    Good(label = "Good", ratio = 0.64F),
    Strong(label = "Strong", ratio = 0.82F),
    Robust(label = "Robust", ratio = 1F);

    companion object {
        /**
         * Maps entropy size to a password strength level:
         *
         * 0–7 bytes   → Fragile
         *
         * 8–11 bytes  → Weak
         *
         * 12–15 bytes → Fair
         *
         * 16–19 bytes → Good
         *
         * 20–23 bytes → Strong
         *
         * 24+ bytes   → Robust
         */
        fun entropyByteSizeToPasswordStrengthLevel(entropyByteSize: Int): PasswordStrengthLevel {
            require(entropyByteSize >= 0) {
                "entropyByteSize must not be negative."
            }

            return when (entropyByteSize * Byte.SIZE_BITS) {
                in 0..63 -> Fragile
                in 64..95 -> Weak
                in 96..127 -> Fair
                in 128..159 -> Good
                in 160..191 -> Strong
                else -> Robust
            }
        }
    }
}