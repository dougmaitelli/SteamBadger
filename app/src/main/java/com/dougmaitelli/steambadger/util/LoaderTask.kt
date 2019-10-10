package com.dougmaitelli.steambadger.util

import android.app.Activity
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.ProgressBar

abstract class LoaderTask<C : Activity> constructor(context: C, showProgress: Boolean = true) : AsyncTask<C, Any, Any>() {

    protected val context: C
    private var progressBar: ProgressBar? = null

    init {
        this.context = context

        if (showProgress) {
            val layout: ViewGroup = context.findViewById(android.R.id.content)

            progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
            val params = RelativeLayout.LayoutParams(100, 100)
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            layout.addView(progressBar, params)
            progressBar!!.visibility = View.VISIBLE
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
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
    }

    open fun onComplete() {}

    open fun onCancelTriggered() {}
}