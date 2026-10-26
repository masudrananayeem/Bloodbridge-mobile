package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bloodbridge.app.ui.AppViewModel

private data class TabItem(val label: String, val icon: String)

private val tabs = listOf(
    TabItem(t("Find"), "🔍"),
    TabItem(t("Requests"), "🩸"),
    TabItem(t("Donor"), "❤"),
    TabItem(t("Alerts"), "🔔"),
    TabItem(t("Profile"), "👤"),
)

@Composable
fun MainShell(vm: AppViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.info) {
        vm.info?.let {
            snackbar.showSnackbar(it)
            vm.clearInfo()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, item ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { vm.clearError(); tab = i },
                        icon = { Text(item.icon) },
                        label = { Text(t(item.label)) },
                    )
                }
            }
        },
    ) { padding ->
        androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
            when (tab) {
                0 -> FindTab(vm)
                1 -> RequestsTab(vm)
                2 -> DonorTab(vm)
                3 -> AlertsTab(vm)
                else -> ProfileTab(vm)
            }
        }
    }
}
