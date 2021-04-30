package com.miguelzaragozaserrano.dam.v2.presentation.utils

import java.time.LocalDateTime

object Utils {

    fun isNextDay(currentDay: LocalDateTime, lastDay: LocalDateTime?): Boolean? =
        lastDay?.plusDays(1)?.isBefore(
            currentDay
        )

}