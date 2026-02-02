package com.fabiobassi.famigliab.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.fabiobassi.famigliab.data.FrequencyType
import com.fabiobassi.famigliab.data.MedicationSchedule
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MedicationNotificationManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNotifications(schedules: List<MedicationSchedule>) {
        schedules.filter { !it.isArchived }.forEach { schedule ->
            scheduleNotification(schedule)
        }
    }

    fun scheduleNotification(schedule: MedicationSchedule) {
        val nextTriggerTime = calculateNextTriggerTime(schedule) ?: return

        val intent = Intent(context, MedicationNotificationReceiver::class.java).apply {
            putExtra("medication_name", schedule.name)
            putExtra("dosage", schedule.dosage)
            putExtra("person", schedule.person.name)
            putExtra("schedule_id", schedule.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTriggerTime,
            pendingIntent
        )
    }

    private fun calculateNextTriggerTime(schedule: MedicationSchedule): Long? {
        val calendar = Calendar.getInstance()
        val timeParts = schedule.hour.split(":")
        if (timeParts.size != 2) return null

        val targetHour = timeParts[0].toInt()
        val targetMinute = timeParts[1].toInt()

        calendar.set(Calendar.HOUR_OF_DAY, targetHour)
        calendar.set(Calendar.MINUTE, targetMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val now = System.currentTimeMillis()

        // Iterate through days starting from today until we find a match
        for (i in 0..365) { // Check up to a year ahead
            if (calendar.timeInMillis > now && isScheduleActiveOnDate(schedule, calendar.time)) {
                return calendar.timeInMillis
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return null
    }

    private fun isScheduleActiveOnDate(schedule: MedicationSchedule, date: Date): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance().apply { time = date }
        
        return when (schedule.frequencyType) {
            FrequencyType.WEEKLY -> {
                val dayOfWeek = getDayOfWeek(calendar)
                schedule.daysOfWeek?.contains(dayOfWeek) == true
            }
            FrequencyType.INTERVAL -> {
                if (schedule.startDate != null && schedule.intervalDays != null) {
                    try {
                        val startDate = sdf.parse(schedule.startDate)
                        if (startDate != null && !date.before(startDate)) {
                            val diffInMillis = date.time - startDate.time
                            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
                            diffInDays % schedule.intervalDays == 0
                        } else {
                            false
                        }
                    } catch (e: Exception) {
                        false
                    }
                } else {
                    false
                }
            }
        }
    }

    private fun getDayOfWeek(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }
    }

    fun cancelNotification(schedule: MedicationSchedule) {
        val intent = Intent(context, MedicationNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
