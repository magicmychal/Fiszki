package click.quickclicker.fiszki.activity

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import click.quickclicker.fiszki.AlarmReceiver
import click.quickclicker.fiszki.FiszkiApplication
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.exam.ExamActivity
import click.quickclicker.fiszki.activity.learning.LearningActivity
import click.quickclicker.fiszki.activity.myWords.category.CategoryActivity
import click.quickclicker.fiszki.dialogs.ReminderScheduleDialogFragment
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class SettingsActivity : AppCompatActivity() {

    private lateinit var mNightModeController: NightModeController
    private lateinit var prefs: LocalSharedPreferences
    private lateinit var notificationSwitch: MaterialSwitch
    private lateinit var scheduleValue: TextView

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
        enableEdgeToEdge()
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.activity_settings)

        prefs = LocalSharedPreferences(this)
        handleWindowInsets()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        buildToolbar()
        buildNotificationSection()
        buildNightModeSwitch()
        buildLanguageRow()
        buildAlgorithmSwitch()
        buildDiagnosticSwitch()
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
        updateScheduleSubtitle()
    }

    private fun handleWindowInsets() {
        val scrollView = findViewById<ScrollView>(R.id.settings_scroll_view)
        val originalBottom = scrollView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(bottom = originalBottom + bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.settings_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // --- Notification Section ---

    private fun buildNotificationSection() {
        notificationSwitch = findViewById(R.id.settings_notification_switch)
        scheduleValue = findViewById(R.id.settings_notification_schedule_value)

        notificationSwitch.isChecked = prefs.notificationEnabled
        updateScheduleSubtitle()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tryEnableNotifications()
            } else {
                prefs.notificationEnabled = false
                AlarmReceiver.cancel(this)
            }
        }

        findViewById<View>(R.id.settings_notification_schedule_row).setOnClickListener {
            showScheduleDialog()
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

    private fun showScheduleDialog() {
        val dialog = ReminderScheduleDialogFragment.newInstance(
            prefs.notificationHour,
            prefs.notificationMinute,
            prefs.notificationDays
        )
        dialog.onScheduleConfirmed = { hour, minute, days ->
            prefs.notificationHour = hour
            prefs.notificationMinute = minute
            prefs.notificationDays = days
            updateScheduleSubtitle()
            if (prefs.notificationEnabled) {
                AlarmReceiver.cancel(this)
                if (days.isNotEmpty()) {
                    AlarmReceiver.scheduleNext(this)
                }
            }
        }
        dialog.show(supportFragmentManager, "schedule_dialog")
    }

    private fun updateScheduleSubtitle() {
        val time = String.format("%02d:%02d", prefs.notificationHour, prefs.notificationMinute)
        val days = prefs.notificationDays
        val daysPart = if (days.size == 7) {
            getString(R.string.settings_notification_days_everyday)
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
            selectedNames.joinToString(", ")
        }
        scheduleValue.text = "$time \u2022 $daysPart"
    }

    // --- General Section ---

    private fun buildNightModeSwitch() {
        val nightModeSwitch = findViewById<MaterialSwitch>(R.id.settings_night_mode_switch)
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

    private fun buildAlgorithmSwitch() {
        val algorithmSwitch = findViewById<MaterialSwitch>(R.id.settings_algorithm_switch)
        val algorithmSummary = findViewById<TextView>(R.id.settings_algorithm_summary)

        algorithmSwitch.isChecked = prefs.useFsrsAlgorithm
        updateAlgorithmSummary(algorithmSummary, prefs.useFsrsAlgorithm)

        algorithmSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.useFsrsAlgorithm = isChecked
            updateAlgorithmSummary(algorithmSummary, isChecked)
        }
    }

    private fun updateAlgorithmSummary(tv: TextView, enabled: Boolean) {
        tv.text = if (enabled) {
            getString(R.string.settings_algorithm_summary_on)
        } else {
            getString(R.string.settings_algorithm_summary_off)
        }
    }

    private fun buildDiagnosticSwitch() {
        val diagnosticSwitch = findViewById<MaterialSwitch>(R.id.settings_diagnostic_switch)
        val testCrashRow = findViewById<View>(R.id.settings_test_crash)

        diagnosticSwitch.isChecked = prefs.diagnosticDataEnabled
        testCrashRow.visibility = if (prefs.diagnosticDataEnabled) View.VISIBLE else View.GONE

        diagnosticSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.diagnosticDataEnabled = isChecked
            testCrashRow.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                (application as FiszkiApplication).initSentry()
            }
        }

        testCrashRow.setOnClickListener {
            throw RuntimeException("Fiszki test crash — diagnostic reporting verification")
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
