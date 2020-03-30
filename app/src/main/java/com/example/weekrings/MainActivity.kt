package com.example.weekrings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

class MainActivity : AppCompatActivity() {
    data class Ring(var week: View)

    var rings = mutableListOf<Ring>()
    val APP_PREFERENCES = "WeekRings"
    val FINISHED_WEEKS_PREFERENCE = "finishedWeeks"
    val NEXT_DATE_PREFERENCE = "nextDate"
    lateinit var nextDate: LocalDate
    var currentWeek = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weekRingsPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        currentWeek = weekRingsPref.getInt(FINISHED_WEEKS_PREFERENCE, 0)
        val nextDateString = weekRingsPref.getString(NEXT_DATE_PREFERENCE, "2020-03-18")
        nextDate = LocalDate.parse(nextDateString)

        fillRingList()
        FillingFinishedOnStart()
    }

    override fun onPause() {
        super.onPause()

        val weekRingsPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = weekRingsPref.edit()
        editor.putInt(FINISHED_WEEKS_PREFERENCE, currentWeek)
        editor.putString(NEXT_DATE_PREFERENCE, nextDate.toString())
        editor.apply()
    }

    fun fillRingList() {
    val ringView = findViewById<TableLayout>(R.id.ringsTable)
        for (i in 0..4) {
            val curRowView = ringView.getChildAt(i) as TableRow
            for (k in 0.. 4) {
                val curRingView = curRowView.getChildAt(k)
                val curRing = Ring(curRingView)
                rings.add(curRing)
            }
        }
    }

    fun FillingFinishedOnStart() {
        for (i in 0 until currentWeek) {
            rings[i].week.setBackgroundResource(R.drawable.ring_background)
        }
    }

    fun fillWeek(view: View) {
        if (currentWeek < rings.size) {
            if (checkDate()) {
                rings[currentWeek].week.setBackgroundResource(R.drawable.ring_background)
                currentWeek++
            } else {
                val curDate = Date()
                val curLocalDate = curDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val period = Period.between(curLocalDate, nextDate).days
                val remainingToast = Toast.makeText(this, getString(R.string.remaining_days_toast, period), Toast.LENGTH_SHORT)
                remainingToast.setGravity(Gravity.BOTTOM, 0, 310)
                remainingToast.show()
            }
        } else {
            currentWeek = 0
        }
    }

    fun checkDate() : Boolean
    {
        val curDate = Date()
        val curLocalDate = curDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        if (curLocalDate.isAfter(nextDate)) {
            nextDate = nextDate.plusDays(7)
            return true
        } else {
            println(nextDate.toString())
            return false
        }
    }
}
