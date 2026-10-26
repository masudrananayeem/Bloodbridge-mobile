package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.bloodbridge.app.i18n.t
import com.bloodbridge.app.ui.AppViewModel

private data class TabItem(val id: String, val label: String, val icon: String)

private val allTabs = mapOf(
    "find" to TabItem("find", "Find", "🔍"),
    "requests" to TabItem("requests", "Requests", "🩸"),
    "donor" to TabItem("donor", "Donor", "❤"),
    "orgs" to TabItem("orgs", "Organizations", "🏥"),
    "alerts" to TabItem("alerts", "Alerts", "🔔"),
    "profile" to TabItem("profile", "Profile", "👤"),
)

@Composable
fun MainShell(vm: AppViewModel) {
    val mode = vm.user?.activeMode ?: "donor"
    // Same idea as the web app: donor mode and seeker mode show different tabs.
    val tabIds = if (mode == "donor") listOf("requests", "donor", "orgs", "alerts", "profile") else listOf("find", "requests", "orgs", "alerts", "profile")
    var selected by rememberSaveable { mutableStateOf("requests") }
    val current = if (selected in tabIds) selected else tabIds.first()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.user?.id) { if (vm.user != null) vm.loadNotifications() }

    LaunchedEffect(vm.info) {
        vm.info?.let {
            snackbar.showSnackbar(it)
            vm.clearInfo()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("BloodBridge", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        Text(t("Connecting blood donors with people in need"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { vm.clearError(); selected = "alerts"; vm.loadNotifications() }) {
                        Text("🔔" + if (vm.unreadCount > 0) " ${vm.unreadCount}" else "")
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                tabIds.forEach { id ->
                    val item = allTabs.getValue(id)
                    NavigationBarItem(
                        selected = current == id,
                        onClick = { vm.clearError(); selected = id },
                        icon = { Text(item.icon) },
                        label = { Text(t(item.label)) },
                    )
                }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            ModeSwitch(mode = mode, busy = vm.busy, onChange = { vm.setMode(it) })
            if (!vm.emailVerified) VerifyEmailBanner(vm)
            Box(Modifier.weight(1f)) {
                when (current) {
                    "find" -> FindTab(vm)
                    "requests" -> RequestsTab(vm)
                    "donor" -> DonorTab(vm)
                    "orgs" -> OrganizationsTab(vm)
                    "alerts" -> AlertsTab(vm, onOpenRequests = { selected = "requests" })
                    else -> ProfileTab(vm)
                }
            }
        }
    }
}

@Composable
private fun ModeSwitch(mode: String, busy: Boolean, onChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf("donor" to "Blood Donor", "seeker" to "Blood Seeker").forEach { (value, text) ->
            val label = if (value == "donor") "❤ " + t("Blood Donor") else "🔍 " + t("Blood Seeker")
            if (mode == value) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large, modifier = Modifier.weight(1f)) {
                    Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
                }
            } else {
                OutlinedButton(onClick = { onChange(value) }, enabled = !busy, modifier = Modifier.weight(1f)) { Text(label) }
            }
        }
    }
}

@Composable
private fun VerifyEmailBanner(vm: AppViewModel) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(t("Please verify your email address. We sent you a verification link."), style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { vm.resendVerification() }, enabled = !vm.busy) { Text(t("Resend email")) }
                TextButton(onClick = { vm.checkEmailVerified() }, enabled = !vm.busy) { Text(t("I've verified")) }
            }
        }
    }
}
