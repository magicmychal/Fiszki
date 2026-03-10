# ORMLite — models use reflection via @DatabaseField annotations
-keepclassmembers class click.quickclicker.fiszki.model.** {
    <fields>;
    <init>();
}

# ORMLite — DBHelper is instantiated via reflection by OpenHelperManager.getHelper()
-keep class click.quickclicker.fiszki.database.ORM.DBHelper {
    public <init>(android.content.Context);
}

# Serializable classes passed between activities via Intent extras
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# MaterialDrawer (mikepenz) — uses reflection for drawer items
-dontwarn com.mikepenz.**
-keep class com.mikepenz.** { *; }

# Material Dialogs (afollestad) — legacy library, keep to be safe
-dontwarn com.afollestad.**
-keep class com.afollestad.** { *; }

# Sentry — ships its own rules but suppress warnings for native integration
-dontwarn io.sentry.**
