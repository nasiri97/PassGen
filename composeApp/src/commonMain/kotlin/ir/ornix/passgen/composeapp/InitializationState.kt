package ir.ornix.passgen.composeapp

sealed interface InitializationState {
    data object Initializing : InitializationState
    data object Completed : InitializationState
    data class Failed(val reason: FailedInitializationReason) : InitializationState
}