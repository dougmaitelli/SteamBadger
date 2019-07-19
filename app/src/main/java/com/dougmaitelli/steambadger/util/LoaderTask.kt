package com.dougmaitelli.steambadger.util

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask

abstract class LoaderTask<C : Context> constructor(context: C, dialog: Boolean = true) : AsyncTask<C, Any, Any>() {

    val context: C
    private var dialog: ProgressDialog? = null

    init {
        this.context = context

        if (dialog) {
            this.dialog = ProgressDialog(context)
            this.dialog!!.setMessage("Loading...")
            this.dialog!!.setCancelable(false)
            this.dialog!!.setOnCancelListener {
                onCancelTriggered()
            }
            this.dialog!!.show()
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

    fun setCancellable(cancellable: Boolean) {
        if (this.dialog != null) {
            this.dialog!!.setCancelable(cancellable)
        }
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