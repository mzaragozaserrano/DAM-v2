package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.view.Menu
import com.miguelzaragozaserrano.dam.v2.R
import java.time.LocalDateTime

object Utils {

    fun isNextDay(currentDay: LocalDateTime, lastDay: LocalDateTime?): Boolean? =
        lastDay?.plusDays(1)?.isBefore(
            currentDay
        )

    fun setItemsVisibility(menu: Menu, visible: Boolean) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item.itemId != R.id.search_icon) item.isVisible = visible
        }
    }

}