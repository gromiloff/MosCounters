package gromiloff.prefs

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*


class AppPref {
    private class ObserverValue<Type> internal constructor(value: Type?) {
        private val obs = Vector<Observer>()
        internal var customObserverName: Observable? = null

        var value: Type? = null
            private set
        var countListener: Observer? = null

        init {
            this.value = value
        }

        @Suppress("UNCHECKED_CAST")
        fun save(value: Any) {
            this.value = value as Type
            val arrLocal = this.obs.toTypedArray()

            for (i in arrLocal.indices.reversed())
                (arrLocal[i] as Observer).update(this.customObserverName, this.value)
        }

        fun observerCount() = this.obs.size
        fun addObserver(o: Observer?) {
            if (o == null)
                throw NullPointerException()
            if (!this.obs.contains(o)) {
                this.obs.addElement(o)
            }
            this.countListener?.update(this.customObserverName, obs.size)
        }

        fun deleteObserver(o: Observer?) {
            this.obs.removeElement(o)
            this.countListener?.update(this.customObserverName, obs.size)
        }
    }

    companion object {
        private var pref: SharedPreferences? = null
        private val cache = HashMap<PrefEnum<*>, ObserverValue<*>>()

        fun init(context: Context, name: String) {
            this.pref = context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        }

        fun reset(key: PrefEnum<Any>) {
            save<Any>(key)
            Log.d(key::class.java.simpleName, "reset $key")
        }

        fun <Type> load(key: PrefEnum<Any>, @Suppress("UNCHECKED_CAST") defValue: Type? = key.defaultValue as Type): Type? {
            var fromCache = false
            val cacheValue = this.cache[key]
            val def = if (defValue == null) null else "" + defValue
            val result = if (cacheValue == null) {
                val value = this.pref?.getString(key.name, def)

                if (value == null || value == def) defValue else when (key.defaultValue) {
                    is Boolean -> java.lang.Boolean.valueOf(value)
                    is Double -> java.lang.Double.valueOf(value)
                    is Int -> Integer.valueOf(value)
                    is Long -> java.lang.Long.valueOf(value)
                    is String -> value
                    is Parcelable -> throw NoSuchMethodException()
                    is Set<*>/*, is List<*>*/ -> {
                        var a: ObjectInputStream? = null
                        var b: ByteArrayInputStream? = null
                        var ret = key.defaultValue
                        try {
                            val split = value.substring(1, value.length - 1).split(", ")
                            val array = ByteArray(split.size)
                            for (i in split.indices) {
                                array[i] = java.lang.Byte.parseByte(split[i])
                            }
                            b = ByteArrayInputStream(array)
                            a = ObjectInputStream(b)
                            ret = a.readObject()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            a?.close()
                            b?.close()
                        }
                        ret
                    }
                    else -> key.defaultValue
                }
            } else {
                fromCache = true
                if (cacheValue.value == key.defaultValue) {
                    defValue
                } else {
                    cacheValue.value
                }
            }


            if (!fromCache) {
                // кешируем на будущий доступ
                this.cache[key] = ObserverValue(result)
            }

            Log.d(key.name, "get from " + (if (fromCache) " CACHE " else " PREFS ") + " [$result]")
            @Suppress("UNCHECKED_CAST")
            return result as? Type
        }

        fun <Type> save(key: PrefEnum<Any>, @Suppress("UNCHECKED_CAST") value: Type = key.defaultValue as Type) {
            val editor = this.pref!!.edit()
            val saveValue: String = when (key.defaultValue) {
                is Set<*>/*, is List<*>*/ -> {
                    var a: ObjectOutputStream? = null
                    val b = ByteArrayOutputStream()
                    var ret: String = "" + key.defaultValue
                    try {
                        a = ObjectOutputStream(b)
                        a.writeObject(value)
                        ret = Arrays.toString(b.toByteArray())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        a?.close()
                        b.close()
                    }
                    ret
                }
                is Parcelable -> throw NoSuchMethodException()
                else -> java.lang.String.valueOf(value)
            }
            editor.putString(key.name, saveValue)
            editor.apply()

            this.cache[key]?.save(value!!)
            Log.d(key.name, "save [$value]")
        }

        fun addListenerValue(key: PrefEnum<*>, listener: Observer, customObserverName: Observable? = null) {
            var o = this.cache[key]
            if (o == null) {
                o = ObserverValue(this.pref!!.getString(key.name, "" + key.defaultValue))
                this.cache[key] = o
            }
            o.customObserverName = customObserverName
            o.addObserver(listener)
        }

        fun removeListenerValue(key: PrefEnum<*>, listener: Observer) {
            this.cache[key]?.deleteObserver(listener)
        }

        fun getCurrentListenersCount(key: PrefEnum<*>) = this.cache[key]?.observerCount() ?: 0
        fun addListenerCountListeners(key: PrefEnum<*>, listener: Observer) {
            var o = this.cache[key]
            if (o == null) {
                o = ObserverValue(this.pref!!.getString(key.name, "" + key.defaultValue))
                this.cache[key] = o
            }
            o.countListener = listener
        }

        fun removeListenerCountListeners(key: PrefEnum<*>) {
            val o = this.cache[key]
            if (o != null) {
                o.countListener = null
            }
        }
    }
}
