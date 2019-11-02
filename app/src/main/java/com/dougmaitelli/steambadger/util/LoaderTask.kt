package com.dougmaitelli.steambadger.util

import android.app.Activity
import android.os.AsyncTask
import androidx.appcompat.app.AlertDialog
import com.dougmaitelli.steambadger.R

abstract class LoaderTask<C : Activity> constructor(protected val context: C, showProgress: Boolean = true) : AsyncTask<C, Any, Any>() {

    private var dialog: AlertDialog? = null

    init {
        if (showProgress) {
            val builder = AlertDialog.Builder(context)
            builder.setView(R.layout.progress)
            dialog = builder.create()
            dialog!!.show()
        }

        execute(this.context)
    }

    override fun onProgressUpdate(vararg values: Any) {
        onUpdate(*values)
    }

    open fun onUpdate(vararg values: Any) {}

    fun doUpdate(vararg values: Any) {
        publishProgress(values)
    }

    override fun doInBackground(vararg params: C): C? {
        process()
        return null
    }

    abstract fun process()

    override fun onPostExecute(result: Any?) {
        onComplete()
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    open fun onComplete() {}

    open fun onCancelTriggered() {}
}