package com.sneyder.biznearby.data.preferences

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.core.content.edit
import com.google.gson.GsonBuilder
import com.sneyder.biznearby.data.model.user.UserProfile

abstract class PreferencesHelper(val sharedPreferences: SharedPreferences) {

    fun getCurrentUserProfile(): UserProfile? {
        val json: String = get(AppPreferencesHelper.USER) ?: return null
        return try {
            GsonBuilder().serializeNulls().create()
                .fromJson(json, UserProfile::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    @Suppress("UNCHECKED_CAST")
    open operator fun set(key: String, value: Any?) {
        sharedPreferences.apply {
            when (value) {
                is String -> edit { putString(key, value) }
                is Int -> edit { putInt(key, value) }
                is Boolean -> edit { putBoolean(key, value) }
                is Float -> edit { putFloat(key, value) }
                is Long -> edit { putLong(key, value) }
                is Set<*> -> edit { putStringSet(key, value as? Set<String>) }
                else -> throw UnsupportedOperationException("Not yet implemented")
            }
        }
    }

    private val map by lazy { HashMap<String, (Any) -> Unit>() }
    private var changeListenerRegistered = false

    fun registerOnChangeListenerFor(keyToTrack: String, action: (Any) -> Unit) {
        map[keyToTrack] = action
        if (!changeListenerRegistered) registerChangeListener()
    }

    private val changeListener by lazy {
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            map.filter { it.key == key }.forEach { item ->
                sharedPreferences.all[key]?.let { item.value.invoke(it) }
            }
        }
    }

    private fun registerChangeListener() {
        changeListenerRegistered = true
        sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener)
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    inline operator fun <reified T : Any> get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> sharedPreferences.getString(key, defaultValue as? String) as T?
            Int::class -> sharedPreferences.getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> sharedPreferences.getBoolean(
                key,
                defaultValue as? Boolean ?: false
            ) as T?
            Float::class -> sharedPreferences.getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> sharedPreferences.getLong(key, defaultValue as? Long ?: -1) as T?
            Set::class -> sharedPreferences.getStringSet(key, defaultValue as? Set<String> ?: emptySet()) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }


    fun clearPreferences() {
        sharedPreferences.edit { clear() }
    }

}