package com.dougmaitelli.steambadger.util

import android.app.Activity
import android.os.AsyncTask
import androidx.appcompat.app.AlertDialog
import com.dougmaitelli.steambadger.R

abstract class LoaderTask<C : Activity> constructor(context: C, showProgress: Boolean = true) : AsyncTask<C, Any, Any>() {

    protected val context: C
    private var dialog: AlertDialog? = null

    init {
        this.context = context

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