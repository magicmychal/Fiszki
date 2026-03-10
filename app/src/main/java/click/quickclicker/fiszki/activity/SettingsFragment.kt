package click.quickclicker.fiszki.activity

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import click.quickclicker.fiszki.AlarmReceiver
import click.quickclicker.fiszki.FiszkiApplication
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.dialogs.csv.CsvImportBottomSheet
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class SettingsFragment : Fragment() {

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
                Toast.makeText(requireContext(), R.string.settings_permission_notifications, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNightModeController = NightModeController(requireActivity())
        prefs = LocalSharedPreferences(requireContext())

        buildToolbar(view)
        buildNotificationSection(view)
        buildNightModeSwitch(view)
        buildColorPalette(view)
        buildLanguageRow(view)
        buildImportCsv(view)
        buildAlgorithmSwitch(view)
        buildDiagnosticSwitch(view)
        buildClearData(view)
        buildSendFeedback(view)
        buildVersion(view)
    }

    override fun onResume() {
        super.onResume()
        val ctx = context ?: return
        if (prefs.notificationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                prefs.notificationEnabled = false
                AlarmReceiver.cancel(ctx)
                notificationSwitch.isChecked = false
            }
        }
        updateNotificationSubtitles()
    }

    private fun buildToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.settings_toolbar_title)
    }

    // --- Notification Section ---

    private fun buildNotificationSection(view: View) {
        notificationSwitch = view.findViewById(R.id.settings_notification_switch)
        timeValue = view.findViewById(R.id.settings_notification_time_value)
        daysValue = view.findViewById(R.id.settings_notification_days_value)

        notificationSwitch.isChecked = prefs.notificationEnabled
        updateNotificationSubtitles()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tryEnableNotifications()
            } else {
                prefs.notificationEnabled = false
                AlarmReceiver.cancel(requireContext())
            }
        }

        view.findViewById<View>(R.id.settings_notification_time_row).setOnClickListener {
            showTimePicker()
        }

        view.findViewById<View>(R.id.settings_notification_days_row).setOnClickListener {
            showDayPicker()
        }
    }

    private fun tryEnableNotifications() {
        val ctx = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            notificationSwitch.isChecked = false
            Toast.makeText(ctx, R.string.settings_permission_exact_alarm, Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${ctx.packageName}")
            })
            return
        }

        enableNotifications()
    }

    private fun enableNotifications() {
        val ctx = requireContext()
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            notificationSwitch.isChecked = false
            Toast.makeText(ctx, R.string.settings_permission_exact_alarm, Toast.LENGTH_SHORT).show()
            return
        }

        prefs.notificationEnabled = true
        AlarmReceiver.scheduleNext(ctx)
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
                AlarmReceiver.cancel(requireContext())
                AlarmReceiver.scheduleNext(requireContext())
            }
        }

        picker.show(childFragmentManager, "time_picker")
    }

    private fun showDayPicker() {
        val ctx = requireContext()
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

        com.google.android.material.dialog.MaterialAlertDialogBuilder(ctx)
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
                    AlarmReceiver.cancel(ctx)
                    if (selected.isNotEmpty()) {
                        AlarmReceiver.scheduleNext(ctx)
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateNotificationSubtitles() {
        timeValue.text = String.format("%02d:%02d", prefs.notificationHour, prefs.notificationMinute)

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

    private fun buildNightModeSwitch(view: View) {
        val nightModeSwitch = view.findViewById<SwitchCompat>(R.id.settings_night_mode_switch)
        nightModeSwitch.isChecked = mNightModeController.getStatus() != 0
        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), R.string.drawer_nightmode_toast_on, Toast.LENGTH_SHORT).show()
                mNightModeController.on()
            } else {
                Toast.makeText(requireContext(), R.string.drawer_nightmode_toast_off, Toast.LENGTH_SHORT).show()
                mNightModeController.off()
            }
            ChangeActivityManager(requireActivity()).resetMain()
        }
    }

    private fun buildColorPalette(view: View) {
        val paletteValue = view.findViewById<TextView>(R.id.settings_color_palette_value)
        updatePaletteLabel(paletteValue)

        view.findViewById<View>(R.id.settings_color_palette_row).setOnClickListener {
            val options = arrayOf(
                getString(R.string.settings_color_palette_summary_purple),
                getString(R.string.settings_color_palette_summary_yellow)
            )
            val current = prefs.colorPalette
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.settings_color_palette)
                .setSingleChoiceItems(options, current) { dialog, which ->
                    if (which != current) {
                        prefs.colorPalette = which
                        updatePaletteLabel(paletteValue)
                        dialog.dismiss()
                        ChangeActivityManager(requireActivity()).resetMain()
                    } else {
                        dialog.dismiss()
                    }
                }
                .show()
        }
    }

    private fun updatePaletteLabel(tv: TextView) {
        tv.text = if (prefs.colorPalette == LocalSharedPreferences.PALETTE_YELLOW) {
            getString(R.string.settings_color_palette_summary_yellow)
        } else {
            getString(R.string.settings_color_palette_summary_purple)
        }
    }

    private fun buildLanguageRow(view: View) {
        view.findViewById<View>(R.id.settings_language_row).setOnClickListener {
            val ctx = requireContext()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                    data = Uri.parse("package:${ctx.packageName}")
                }
                startActivity(intent)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${ctx.packageName}")
                }
                startActivity(intent)
            }
        }
    }

    private fun buildImportCsv(view: View) {
        view.findViewById<View>(R.id.settings_import_csv).setOnClickListener {
            CsvImportBottomSheet().show(childFragmentManager, "csv_import")
        }
    }

    private fun buildAlgorithmSwitch(view: View) {
        val algorithmSwitch = view.findViewById<MaterialSwitch>(R.id.settings_algorithm_switch)
        val algorithmSummary = view.findViewById<TextView>(R.id.settings_algorithm_summary)

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

    private fun buildDiagnosticSwitch(view: View) {
        val diagnosticSwitch = view.findViewById<MaterialSwitch>(R.id.settings_diagnostic_switch)
        val testCrashRow = view.findViewById<View>(R.id.settings_test_crash)

        diagnosticSwitch.isChecked = prefs.diagnosticDataEnabled
        testCrashRow.visibility = if (prefs.diagnosticDataEnabled) View.VISIBLE else View.GONE

        diagnosticSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.diagnosticDataEnabled = isChecked
            testCrashRow.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (isChecked) {
                (requireActivity().application as FiszkiApplication).initSentry()
            }
        }

        testCrashRow.setOnClickListener {
            throw RuntimeException("Fiszki test crash — diagnostic reporting verification")
        }
    }

    // --- Other Settings ---

    private fun buildClearData(view: View) {
        view.findViewById<View>(R.id.settings_clear_data).setOnClickListener {
            val ctx = requireContext()
            AlertDialog.Builder(ctx)
                .setMessage(R.string.alert_clear_database_settings)
                .setPositiveButton(R.string.button_action_yes) { _, _ ->
                    deleteDbRows()
                    CategoryRepository(ctx).addSystemCategory()
                    Toast.makeText(ctx, R.string.drawer_clear_name, Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.button_action_no) { _, _ -> }
                .show()
        }
    }

    private fun deleteDbRows() {
        val ctx = requireContext()
        val flashcardRepository = FlashcardRepository(ctx)
        val categoryRepository = CategoryRepository(ctx)

        flashcardRepository.deleteFlashcards(flashcardRepository.getAllFlashcards())
        categoryRepository.deleteCategories(categoryRepository.getAllCategory())
        AlarmReceiver.cancel(ctx)
        prefs.notificationEnabled = false
        notificationSwitch.isChecked = false
    }

    private fun buildSendFeedback(view: View) {
        view.findViewById<View>(R.id.settings_send_feedback).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:fiszki@quickclicker.click")
            }
            startActivity(emailIntent)
        }
    }

    private fun buildVersion(view: View) {
        val versionText = view.findViewById<TextView>(R.id.settings_version)
        try {
            val ctx = requireContext()
            val info = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            versionText.text = "${getString(R.string.drawer_version_ver)}${info.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            versionText.visibility = View.GONE
        }
    }
}
