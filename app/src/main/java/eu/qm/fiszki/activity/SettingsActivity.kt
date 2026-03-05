package eu.qm.fiszki.activity

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import eu.qm.fiszki.AlarmReceiver
import eu.qm.fiszki.LocalSharedPreferences
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.exam.ExamActivity
import eu.qm.fiszki.activity.learning.LearningActivity
import eu.qm.fiszki.activity.myWords.category.CategoryActivity
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class SettingsActivity : AppCompatActivity() {

    private lateinit var mNightModeController: NightModeController
    private lateinit var prefs: LocalSharedPreferences
    private lateinit var notificationSwitch: MaterialSwitch
    private lateinit var timeValue: TextView
    private lateinit var daysValue: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                enableNotifications()
            } else {
                notificationSwitch.isChecked = false
                Toast.makeText(this, R.string.settings_permission_notifications, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNightModeController = NightModeController(this)
        mNightModeController.useTheme()
        setContentView(R.layout.activity_settings)

        prefs = LocalSharedPreferences(this)

        buildToolbar()
        buildNotificationSection()
        buildNightModeSwitch()
        buildLanguageRow()
        buildClearData()
        buildSendFeedback()
        buildVersion()
        buildBottomNav()
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission state in case user revoked externally
        if (prefs.notificationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                prefs.notificationEnabled = false
                AlarmReceiver.cancel(this)
                notificationSwitch.isChecked = false
            }
        }
        updateNotificationSubtitles()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.settings_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    // --- Notification Section ---

    private fun buildNotificationSection() {
        notificationSwitch = findViewById(R.id.settings_notification_switch)
        timeValue = findViewById(R.id.settings_notification_time_value)
        daysValue = findViewById(R.id.settings_notification_days_value)

        notificationSwitch.isChecked = prefs.notificationEnabled
        updateNotificationSubtitles()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tryEnableNotifications()
            } else {
                prefs.notificationEnabled = false
                AlarmReceiver.cancel(this)
            }
        }

        findViewById<View>(R.id.settings_notification_time_row).setOnClickListener {
            showTimePicker()
        }

        findViewById<View>(R.id.settings_notification_days_row).setOnClickListener {
            showDayPicker()
        }
    }

    private fun tryEnableNotifications() {
        // Check POST_NOTIFICATIONS permission (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        // Check exact alarm permission
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            notificationSwitch.isChecked = false
            Toast.makeText(this, R.string.settings_permission_exact_alarm, Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            })
            return
        }

        enableNotifications()
    }

    private fun enableNotifications() {
        // Re-check exact alarm after permission grant
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            notificationSwitch.isChecked = false
            Toast.makeText(this, R.string.settings_permission_exact_alarm, Toast.LENGTH_SHORT).show()
            return
        }

        prefs.notificationEnabled = true
        AlarmReceiver.scheduleNext(this)
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(prefs.notificationHour)
            .setMinute(prefs.notificationMinute)
            .setTitleText(R.string.settings_notification_time)
            .build()

        picker.addOnPositiveButtonClickListener {
            prefs.notificationHour = picker.hour
            prefs.notificationMinute = picker.minute
            updateNotificationSubtitles()
            if (prefs.notificationEnabled) {
                AlarmReceiver.cancel(this)
                AlarmReceiver.scheduleNext(this)
            }
        }

        picker.show(supportFragmentManager, "time_picker")
    }

    private fun showDayPicker() {
        val dayNames = arrayOf(
            getString(R.string.day_monday),
            getString(R.string.day_tuesday),
            getString(R.string.day_wednesday),
            getString(R.string.day_thursday),
            getString(R.string.day_friday),
            getString(R.string.day_saturday),
            getString(R.string.day_sunday)
        )
        val currentDays = prefs.notificationDays
        val checked = BooleanArray(7) { i -> currentDays.contains((i + 1).toString()) }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(R.string.settings_notification_days)
            .setMultiChoiceItems(dayNames, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val selected = mutableSetOf<String>()
                for (i in checked.indices) {
                    if (checked[i]) selected.add((i + 1).toString())
                }
                prefs.notificationDays = selected
                updateNotificationSubtitles()
                if (prefs.notificationEnabled) {
                    AlarmReceiver.cancel(this)
                    if (selected.isNotEmpty()) {
                        AlarmReceiver.scheduleNext(this)
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateNotificationSubtitles() {
        // Time
        timeValue.text = String.format("%02d:%02d", prefs.notificationHour, prefs.notificationMinute)

        // Days
        val days = prefs.notificationDays
        if (days.size == 7) {
            daysValue.text = getString(R.string.settings_notification_days_everyday)
        } else {
            val dayNames = arrayOf(
                getString(R.string.day_monday),
                getString(R.string.day_tuesday),
                getString(R.string.day_wednesday),
                getString(R.string.day_thursday),
                getString(R.string.day_friday),
                getString(R.string.day_saturday),
                getString(R.string.day_sunday)
            )
            val selectedNames = (1..7)
                .filter { days.contains(it.toString()) }
                .map { dayNames[it - 1] }
            daysValue.text = selectedNames.joinToString(", ")
        }
    }

    // --- General Section ---

    private fun buildNightModeSwitch() {
        val nightModeSwitch = findViewById<SwitchCompat>(R.id.settings_night_mode_switch)
        nightModeSwitch.isChecked = mNightModeController.getStatus() != 0
        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, R.string.drawer_nightmode_toast_on, Toast.LENGTH_SHORT).show()
                mNightModeController.on()
            } else {
                Toast.makeText(this, R.string.drawer_nightmode_toast_off, Toast.LENGTH_SHORT).show()
                mNightModeController.off()
            }
            ChangeActivityManager(this).resetMain()
        }
    }

    private fun buildLanguageRow() {
        findViewById<View>(R.id.settings_language_row).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } else {
                // API 31-32: open general app settings as fallback
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    // --- Other Settings ---

    private fun buildClearData() {
        findViewById<View>(R.id.settings_clear_data).setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.alert_clear_database_settings)
                .setPositiveButton(R.string.button_action_yes) { _, _ ->
                    deleteDbRows()
                    CategoryRepository(this).addSystemCategory()
                    Toast.makeText(this, R.string.drawer_clear_name, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.button_action_no) { _, _ -> }
                .show()
        }
    }

    private fun deleteDbRows() {
        val flashcardRepository = FlashcardRepository(this)
        val categoryRepository = CategoryRepository(this)

        flashcardRepository.deleteFlashcards(flashcardRepository.getAllFlashcards())
        categoryRepository.deleteCategories(categoryRepository.getAllCategory())
        AlarmReceiver.cancel(this)
        prefs.notificationEnabled = false
        notificationSwitch.isChecked = false
    }

    private fun buildSendFeedback() {
        findViewById<View>(R.id.settings_send_feedback).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:fiszki@quickclicker.click")
            }
            startActivity(emailIntent)
        }
    }

    private fun buildBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_settings

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> true
                R.id.nav_flashcards -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_learning -> {
                    startActivity(Intent(this, LearningActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_exam -> {
                    startActivity(Intent(this, ExamActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun buildVersion() {
        val versionText = findViewById<TextView>(R.id.settings_version)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            versionText.text = "${getString(R.string.drawer_version_ver)}${info.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            versionText.visibility = View.GONE
        }
    }
}
