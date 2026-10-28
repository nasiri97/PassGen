## Preserve annotations and line numbers for debugging
#-keepattributes *Annotation*,Signature,SourceFile,LineNumberTable

# WorkManager: preserve the generated Room database implementation constructor
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}