package ir.ornix.passgen.core.data

import android.app.Activity
import java.lang.ref.WeakReference

object AppContextProvider {
    private var currentActivityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }

    fun getActivity(): Activity? {
        return currentActivityRef?.get()
    }
}
