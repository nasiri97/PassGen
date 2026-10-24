package ir.ornix.passgen.core.common

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

object AppContextProvider {
    private var currentActivityRef: WeakReference<Activity>? = null
    var applicationContext: Context? = null

    fun setActivity(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    fun getActivity(): Activity? {
        return currentActivityRef?.get()
    }
}