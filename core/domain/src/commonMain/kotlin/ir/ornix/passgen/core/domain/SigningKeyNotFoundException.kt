package ir.ornix.passgen.core.domain

class SigningKeyNotFoundException(keyId: String) :
    NoSuchElementException("No signing key found for keyId=\"$keyId\"")