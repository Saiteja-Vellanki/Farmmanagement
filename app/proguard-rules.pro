# Keep Room entities' fields (reflection-free with KSP, but keep for safety with @Keep-less models)
-keep class com.farmmanagement.app.data.db.entity.** { *; }

# Strip verbose logging in release builds (see docs/SECURITY.md)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
