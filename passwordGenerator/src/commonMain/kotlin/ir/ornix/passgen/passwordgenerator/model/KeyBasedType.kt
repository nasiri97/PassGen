package ir.ornix.passgen.passwordgenerator.model

/**
 * A type with a unique string [key] identifier.
 *
 * Subclasses must provide:
 *  - [key]: a unique identifier for this type.
 *  - [createInstance]: a method to create an instance of type [T].
 */
abstract class KeyBasedType<T> {

    abstract val key: String

    abstract fun createInstance(): T

    override fun equals(other: Any?): Boolean {
        return if (this === other) true
        else if (other !is KeyBasedType<*>) false
        else key == other.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}