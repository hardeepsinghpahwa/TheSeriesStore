package com.example.login.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup
import com.example.login.R
import com.irozon.sneaker.Sneaker
import dmax.dialog.SpotsDialog

class CommonUtils {

    companion object{

        fun showErrorMessage(activity: Activity, title:String, description:String){
            Sneaker.with(activity)
                    .setTitle(title, R.color.white)
                    .setMessage(description, R.color.white)
                    .setDuration(2000)
                    .setIcon(R.drawable.info, R.color.white)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setCornerRadius(10, 0)
                    .sneak(R.color.teal_200)

        }
        fun showErrorMessage(viewGroup: ViewGroup, title:String, description:String){
            Sneaker.with(viewGroup)
                    .setTitle(title, R.color.white)
                    .setMessage(description, R.color.white)
                    .setDuration(2000)
                    .setIcon(R.drawable.info, R.color.white)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setCornerRadius(10, 0)
                    .sneak(R.color.teal_200)

        }

        fun showSuccessMessage(viewGroup: ViewGroup, title:String, description:String){
            Sneaker.with(viewGroup)
                    .setTitle(title, R.color.white)
                    .setMessage(description, R.color.white)
                    .setDuration(2000)
                    .setIcon(R.drawable.info, R.color.white)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setCornerRadius(10, 0)
                    .sneak(R.color.green)

        }

        fun showSuccessMessage(activity: Activity, title:String, description:String){
            Sneaker.with(activity)
                    .setTitle(title, R.color.white)
                    .setMessage(description, R.color.white)
                    .setDuration(2000)
                    .setIcon(R.drawable.info, R.color.white)
                    .autoHide(true)
                    .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setCornerRadius(10, 0)
                    .sneak(R.color.green)

        }


        fun getProgressDialog(context: Context, title:String):AlertDialog{
            val alertDialog = SpotsDialog.Builder()
                    .setCancelable(false)
                    .setContext(context)
                    .setTheme(R.style.ProgressDialog)
                    .setMessage(title)
                    .build()

            return alertDialog

        }

    }
}