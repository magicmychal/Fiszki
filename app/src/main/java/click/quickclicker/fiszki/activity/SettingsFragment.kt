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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import click.quickclicker.fiszki.AlarmReceiver
import click.quickclicker.fiszki.FiszkiApplication
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.dialogs.ReminderScheduleDialogFragment
import click.quickclicker.fiszki.dialogs.csv.CsvImportBottomSheet
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class SettingsFragment : Fragment() {

    private lateinit var mNightModeController: NightModeController
    private lateinit var prefs: LocalSharedPreferences

    // Mutable state triggers for recomposition
    private var notificationsEnabled = mutableStateOf(false)
    private var scheduleText = mutableStateOf("")
    private var nightModeEnabled = mutableStateOf(false)
    private var fsrsEnabled = mutableStateOf(false)
    private var debugAlgorithmEnabled = mutableStateOf(false)
    private var diagnosticEnabled = mutableStateOf(false)
    private var versionName = mutableStateOf("")

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                enableNotifications()
            } else {
                notificationsEnabled.value = false
                Toast.makeText(requireContext(), R.string.settings_permission_notifications, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FiszkiTheme {
                    SettingsScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNightModeController = NightModeController(requireActivity())
        prefs = LocalSharedPreferences(requireContext())

        notificationsEnabled.value = prefs.notificationEnabled
        nightModeEnabled.value = mNightModeController.getStatus() != 0
        fsrsEnabled.value = prefs.useFsrsAlgorithm
        debugAlgorithmEnabled.value = prefs.debugAlgorithmEnabled
        diagnosticEnabled.value = prefs.diagnosticDataEnabled
        updateScheduleSubtitle()
        updateVersionName()
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
                notificationsEnabled.value = false
            }
        }
        updateScheduleSubtitle()
    }

    @Composable
    private fun SettingsScreen() {
        val scrollState = rememberScrollState()
        val notifEnabled by remember { notificationsEnabled }
        val schedule by remember { scheduleText }
        val nightMode by remember { nightModeEnabled }
        val fsrs by remember { fsrsEnabled }
        val debugAlgorithm by remember { debugAlgorithmEnabled }
        val diagnostic by remember { diagnosticEnabled }
        val version by remember { versionName }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.settings_toolbar_title),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = RobotoSerifFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- ROUTINES Section ---
            SectionHeader(stringResource(R.string.settings_section_notifications).uppercase())
            Spacer(modifier = Modifier.height(12.dp))

            // Daily reminder card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_notification_enable),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_notification_enable_summary),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notifEnabled,
                        onCheckedChange = { checked ->
                            if (checked) tryEnableNotifications()
                            else {
                                prefs.notificationEnabled = false
                                AlarmReceiver.cancel(requireContext())
                                notificationsEnabled.value = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showScheduleDialog() }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.settings_notification_time).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = schedule.substringBefore(" "),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showScheduleDialog() }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.settings_notification_days).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = schedule.substringAfter("\u2022 ", schedule),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- APPEARANCE & SYSTEM Section ---
            SectionHeader(stringResource(R.string.settings_section_general).uppercase())
            Spacer(modifier = Modifier.height(12.dp))

            // Night mode
            SettingsRow(
                title = stringResource(R.string.drawer_nightmode),
                onClick = { /* toggle handled by switch */ },
                trailing = {
                    Switch(
                        checked = nightMode,
                        onCheckedChange = { checked ->
                            if (checked) {
                                mNightModeController.on()
                                Toast.makeText(requireContext(), R.string.drawer_nightmode_toast_on, Toast.LENGTH_SHORT).show()
                            } else {
                                mNightModeController.off()
                                Toast.makeText(requireContext(), R.string.drawer_nightmode_toast_off, Toast.LENGTH_SHORT).show()
                            }
                            nightModeEnabled.value = checked
                            ChangeActivityManager(requireActivity()).resetMain(R.id.nav_settings)
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Language
            SettingsRow(
                title = stringResource(R.string.settings_language),
                subtitle = stringResource(R.string.settings_language_summary),
                onClick = { openLanguageSettings() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Import from file
            SettingsRow(
                title = stringResource(R.string.settings_import_csv),
                subtitle = stringResource(R.string.settings_import_csv_summary),
                onClick = {
                    CsvImportBottomSheet().show(childFragmentManager, "csv_import")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Send feedback
            SettingsRow(
                title = stringResource(R.string.settings_contact_us),
                subtitle = stringResource(R.string.settings_feedback_email),
                onClick = { openFeedbackEmail() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // About the algorithm
            SettingsRow(
                title = stringResource(R.string.settings_algorithm_info_title),
                subtitle = stringResource(R.string.settings_algorithm_info_summary),
                onClick = { openAlgorithmInfo() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // About the app
            SettingsRow(
                title = stringResource(R.string.settings_about_title),
                subtitle = stringResource(R.string.settings_about_summary),
                onClick = { openAbout() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Diagnostic data
            SettingsRow(
                title = stringResource(R.string.settings_diagnostic_title),
                subtitle = stringResource(R.string.settings_diagnostic_summary),
                onClick = { /* toggle handled by switch */ },
                trailing = {
                    Switch(
                        checked = diagnostic,
                        onCheckedChange = { checked ->
                            prefs.diagnosticDataEnabled = checked
                            diagnosticEnabled.value = checked
                            if (checked) {
                                (requireActivity().application as FiszkiApplication).initSentry()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Debug algorithm
            SettingsRow(
                title = stringResource(R.string.settings_debug_algorithm_title),
                subtitle = stringResource(R.string.settings_debug_algorithm_summary),
                onClick = { /* toggle handled by switch */ },
                trailing = {
                    Switch(
                        checked = debugAlgorithm,
                        onCheckedChange = { checked ->
                            prefs.debugAlgorithmEnabled = checked
                            debugAlgorithmEnabled.value = checked
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- CLEAR DATA (Danger Zone) ---
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showClearDataDialog() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_title_data_base),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = stringResource(R.string.settings_summary_data_base),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Version
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = version,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    @Composable
    private fun SectionHeader(text: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }

    @Composable
    private fun SettingsRow(
        title: String,
        subtitle: String? = null,
        onClick: () -> Unit,
        trailing: @Composable (() -> Unit)? = null
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (trailing != null) {
                    trailing()
                }
            }
        }
    }

    // --- Logic methods (kept from original) ---

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
            notificationsEnabled.value = false
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
            notificationsEnabled.value = false
            Toast.makeText(ctx, R.string.settings_permission_exact_alarm, Toast.LENGTH_SHORT).show()
            return
        }

        prefs.notificationEnabled = true
        notificationsEnabled.value = true
        AlarmReceiver.scheduleNext(ctx)
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
                val ctx = requireContext()
                AlarmReceiver.cancel(ctx)
                if (days.isNotEmpty()) {
                    AlarmReceiver.scheduleNext(ctx)
                }
            }
        }
        dialog.show(childFragmentManager, "schedule_dialog")
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
        scheduleText.value = "$time \u2022 $daysPart"
    }

    private fun openLanguageSettings() {
        val ctx = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startActivity(Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                data = Uri.parse("package:${ctx.packageName}")
            })
        } else {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${ctx.packageName}")
            })
        }
    }

    private fun openFeedbackEmail() {
        startActivity(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:fiszki@quickclicker.click")
        })
    }

    private fun openAlgorithmInfo() {
        startActivity(Intent(requireContext(), AlgorithmInfoActivity::class.java))
    }

    private fun openAbout() {
        startActivity(Intent(requireContext(), AboutActivity::class.java))
    }

    private fun showClearDataDialog() {
        val ctx = requireContext()
        AlertDialog.Builder(ctx)
            .setMessage(R.string.alert_clear_database_settings)
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
                val flashcardRepository = FlashcardRepository(ctx)
                val categoryRepository = CategoryRepository(ctx)
                flashcardRepository.deleteFlashcards(flashcardRepository.getAllFlashcards())
                categoryRepository.deleteCategories(categoryRepository.getAllCategory())
                AlarmReceiver.cancel(ctx)
                prefs.notificationEnabled = false
                notificationsEnabled.value = false
                categoryRepository.addSystemCategory()
                Toast.makeText(ctx, R.string.drawer_clear_name, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.button_action_no) { _, _ -> }
            .show()
    }

    private fun updateVersionName() {
        try {
            val ctx = requireContext()
            val info = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            versionName.value = "VERSION V${info.versionName}".uppercase()
        } catch (_: PackageManager.NameNotFoundException) {
            versionName.value = ""
        }
    }
}
