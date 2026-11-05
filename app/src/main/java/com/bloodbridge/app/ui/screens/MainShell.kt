package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    "orgs" to TabItem("orgs", "Organizations", "＋"),
    "alerts" to TabItem("alerts", "Alerts", "♧"),
    "profile" to TabItem("profile", "Profile", "●"),
)

@Composable
fun MainShell(vm: AppViewModel) {
    val mode = vm.user?.activeMode ?: "donor"
    val tabIds = if (mode == "donor") listOf("requests", "donor", "orgs", "alerts", "profile")
        else listOf("find", "requests", "alerts", "profile")
    var selected by rememberSaveable { mutableStateOf("requests") }
    val current = if (selected in tabIds) selected else tabIds.first()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(vm.info) {
        vm.info?.let { snackbar.showSnackbar(it); vm.clearInfo() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
            ) {
                tabIds.forEach { id ->
                    val item = allTabs.getValue(id)
                    NavigationBarItem(
                        selected = current == id,
                        onClick = { vm.clearError(); selected = id },
                        icon = {
                            Box(
                                modifier = Modifier.size(30.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (current == id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(item.icon, color = if (current == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        },
                        label = { Text(t(item.label), style = MaterialTheme.typography.labelSmall) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            AppTopHeader(vm = vm, mode = mode)
            if (!vm.emailVerified) VerifyEmailBanner(vm)
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
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
private fun AppTopHeader(vm: AppViewModel, mode: String) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) { Text("♥", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineSmall) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("BloodBridge", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(t("Together, we save lives"), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center) {
                Text(vm.user?.fullName?.firstOrNull()?.uppercase() ?: "B", fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("donor" to "Blood Donor", "seeker" to "Blood Seeker").forEach { (value, label) ->
                val selected = mode == value
                Surface(
                    onClick = { if (!selected) vm.setMode(value) },
                    enabled = !vm.busy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = if (selected) 2.dp else 0.dp,
                ) {
                    Text(
                        (if (value == "donor") "♥  " else "⌕  ") + t(label),
                        modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp),
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
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
