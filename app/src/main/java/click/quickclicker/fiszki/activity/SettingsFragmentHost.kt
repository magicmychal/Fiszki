package click.quickclicker.fiszki.activity

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView

@Composable
fun SettingsFragmentHost(
    fragmentActivity: FragmentActivity,
    modifier: Modifier = Modifier
) {
    val containerId = remember { View.generateViewId() }

    AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            val fm = fragmentActivity.supportFragmentManager
            if (fm.findFragmentById(containerId) == null) {
                fm.beginTransaction()
                    .replace(containerId, SettingsFragment())
                    .commitNowAllowingStateLoss()
            }
        }
    )

    DisposableEffect(containerId) {
        onDispose {
            val fm = fragmentActivity.supportFragmentManager
            val fragment = fm.findFragmentById(containerId)
            if (fragment != null) {
                fm.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss()
            }
        }
    }
}
