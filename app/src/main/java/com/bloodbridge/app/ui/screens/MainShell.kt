package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.i18n.t
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.Brand

private data class TabItem(val id: String, val label: String, val icon: String)
private val allTabs = mapOf(
    "find" to TabItem("find", "Find", "⌕"),
    "requests" to TabItem("requests", "Requests", "▤"),
    "donor" to TabItem("donor", "Donor", "♥"),
    "orgs" to TabItem("orgs", "Organizations", "♧"),
    "alerts" to TabItem("alerts", "Alerts", "♧"),
    "profile" to TabItem("profile", "Profile", "●"),
)

@Composable
fun MainShell(vm: AppViewModel) {
    val mode = vm.user?.activeMode ?: "donor"
    val tabIds = if (mode == "donor") listOf("requests", "donor", "orgs", "alerts", "profile") else listOf("find", "requests", "alerts", "profile")
    var selected by rememberSaveable { mutableStateOf("donor") }
    val current = if (selected in tabIds) selected else tabIds.first()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(vm.info) { vm.info?.let { snackbar.showSnackbar(it); vm.clearInfo() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                tabIds.forEach { id ->
                    val item = allTabs.getValue(id)
                    NavigationBarItem(
                        selected = current == id,
                        onClick = { vm.clearError(); selected = id },
                        icon = { Text(item.icon, fontWeight = FontWeight.Bold) },
                        label = { Text(t(item.label)) },
                    )
                }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            AppHeader(vm = vm, mode = mode, onChange = { vm.setMode(it) })
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
private fun AppHeader(vm: AppViewModel, mode: String, onChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Brand, Color(0xFF8F1018)))), contentAlignment = Alignment.Center) {
                Text("♥", color = Color.White, fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
            }
            Column(Modifier.weight(1f).padding(start = 11.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("BloodBridge", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Together, we save lives", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                    Text(vm.user?.fullName?.firstOrNull()?.uppercase() ?: "U", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            listOf("donor" to "♥  Blood Donor", "seeker" to "⌕  Blood Seeker").forEach { (value, label) ->
                if (mode == value) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Text(label, fontWeight = FontWeight.Bold) }
                } else {
                    TextButton(onClick = { onChange(value) }, enabled = !vm.busy, modifier = Modifier.weight(1f)) { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold) }
                }
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
