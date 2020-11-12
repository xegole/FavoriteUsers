package com.bigthinkapps.sw.test.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import kotlin.math.truncate

data class ScreenSize(val height: Int, val width: Int)
data class ItemsOnScreen(val itemsOnHeight: Int, val itemsOnWidth: Int, val totalItemsOnScreen: Int)
data class Metrics(val heightDpSize: Int?, val widthDpSize: Int?)

fun Activity?.calculateScreenSizeAndItemsOnIt(
    itemSizeDpHeight: Int,
    itemSizeDpWidth: Int,
    overrideMetrics: Metrics? = null
): Pair<ScreenSize, ItemsOnScreen> {
    val metrics = DisplayMetrics()
    this?.windowManager?.defaultDisplay?.getMetrics(metrics)
    val itemSizePxHeight = truncate(itemSizeDpHeight * metrics.density)
    val itemSizePxWidth = truncate(itemSizeDpWidth * metrics.density)

    val heightPixels: Int =
        if (overrideMetrics?.heightDpSize != null) truncate(overrideMetrics.heightDpSize * metrics.density).toInt() else metrics.heightPixels
    val widthPixels =
        if (overrideMetrics?.widthDpSize != null) truncate(overrideMetrics.widthDpSize * metrics.density).toInt() else metrics.widthPixels

    val appBarHeight = this.getAppBarHeight()
    val statusBarHeight = this.getStatusBarHeight()
    val height =
        truncate(((heightPixels - itemSizePxHeight - appBarHeight - statusBarHeight) / 2)).toInt()
    val width = truncate(((widthPixels - itemSizePxWidth) / 2)).toInt()

    val itemsH =
        truncate((metrics.heightPixels - appBarHeight - statusBarHeight) / itemSizePxHeight).toInt()
    val itemsW = truncate(widthPixels / itemSizePxWidth).toInt()
    val totalItems = (itemsH * itemsW)

    val screenSize = ScreenSize(height, width)
    val itemsOnScreen = ItemsOnScreen(itemsH, itemsW, totalItems)

    return Pair(screenSize, itemsOnScreen)
}

fun Activity?.getAppBarHeight(): Int {
    val styledAttributes = this?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val appBarHeight = styledAttributes?.getDimension(0, 0f)?.toInt()
    styledAttributes?.recycle()

    return appBarHeight ?: 0
}

fun Activity?.getStatusBarHeight(): Int {
    val rectangle = Rect()
    val window = this?.window
    window?.decorView?.getWindowVisibleDisplayFrame(rectangle)
    val statusBarHeight = rectangle.top
    val contentViewTop = window?.findViewById<View>(Window.ID_ANDROID_CONTENT)?.top ?: 0
    val height = contentViewTop - statusBarHeight
    return if (height < 0) 0 else height
}

fun Activity?.addContact(fullName: String, phone: String, email: String? = null) {
    if (this == null) return
    val intent = Intent(ContactsContract.Intents.Insert.ACTION)
    intent.type = ContactsContract.RawContacts.CONTENT_TYPE
    // required
    intent.putExtra(ContactsContract.Intents.Insert.NAME, fullName)
    intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
    // optionals
    if (email != null) intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email)
    this.startActivity(intent)
}