package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import com.bloodbridge.app.ui.LanguageToggle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.Brand
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.PrimaryButton

@Composable
fun LoginScreen(vm: AppViewModel, onGoRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showReset by rememberSaveable { mutableStateOf(false) }
    var resetEmail by rememberSaveable { mutableStateOf("") }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false; vm.clearError() },
            title = { Text(t("Reset password")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(t("We'll email you a link to set a new password."))
                    AppTextField(resetEmail, { resetEmail = it }, t("Email"), keyboardType = KeyboardType.Email)
                    ErrorBanner(vm.error)
                }
            },
            confirmButton = {
                TextButton(enabled = !vm.busy, onClick = { vm.resetPassword(resetEmail) { showReset = false } }) {
                    Text(if (vm.busy) t("Sending…") else t("Send link"))
                }
            },
            dismissButton = { TextButton(onClick = { showReset = false; vm.clearError() }) { Text(t("Cancel")) } },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            com.bloodbridge.app.ui.ThemeToggle()
            LanguageToggle()
        }
        Spacer(Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Brand), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFE11D24), Color(0xFF74131A)))).padding(20.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                androidx.compose.foundation.layout.Box(Modifier.size(62.dp).background(Color.White.copy(alpha = .16f), RoundedCornerShape(18.dp)), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("♥", color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                }
                Column(Modifier.padding(start = 14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("BloodBridge", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text(t("Together, we save lives."), color = Color.White.copy(alpha = .9f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(t("Welcome back"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text(t("Log in to continue saving lives."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        AppTextField(email, { email = it }, t("Email"), keyboardType = KeyboardType.Email)
        AppTextField(password, { password = it }, t("Password"), password = true)
        if (!showReset) ErrorBanner(vm.error)
        vm.info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        TextButton(onClick = { vm.clearError(); resetEmail = email; showReset = true }) { Text(t("Forgot password?")) }
        PrimaryButton(t("Log in"), vm.busy, { vm.login(email, password) })

        Text(t("or"), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
        val context = LocalContext.current
        OutlinedButton(onClick = { vm.googleLogin(context) }, enabled = !vm.busy, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("G  " + t("Log in with Google"))
        }

        TextButton(onClick = { vm.clearError(); onGoRegister() }, modifier = Modifier.fillMaxWidth()) {
            Text(t("Don't have an account? Register"))
        }
        com.bloodbridge.app.ui.LegalLinks()
    }
}

@Composable
fun RegisterScreen(vm: AppViewModel, onGoLogin: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var upazila by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(t("Create your account"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(t("Join the network of donors and seekers."), color = MaterialTheme.colorScheme.onSurfaceVariant)

        AppTextField(fullName, { fullName = it }, t("Full name"))
        AppTextField(email, { email = it }, t("Email"), keyboardType = KeyboardType.Email)
        AppTextField(phone, { phone = it }, t("Phone (01XXXXXXXXX)"), keyboardType = KeyboardType.Phone)
        DropdownField(t("Blood group"), bloodGroup, Constants.bloodGroups, { bloodGroup = it })
        DropdownField(t("Gender"), gender, Constants.genders, { gender = it })
        AppTextField(dob, { dob = it }, t("Date of birth"), placeholder = "YYYY-MM-DD", keyboardType = KeyboardType.Number)
        DropdownField(t("District"), district, Constants.districts, { district = it })
        AppTextField(upazila, { upazila = it }, t("Upazila"))
        AppTextField(address, { address = it }, t("Present address"))
        AppTextField(password, { password = it }, t("Password"), password = true)
        AppTextField(confirm, { confirm = it }, t("Confirm password"), password = true)

        ErrorBanner(vm.error)
        PrimaryButton(t("Register"), vm.busy, {
            vm.register(fullName, email, password, confirm, phone, bloodGroup, gender, dob, district, upazila, address)
        })

        TextButton(onClick = { vm.clearError(); onGoLogin() }, modifier = Modifier.fillMaxWidth()) {
            Text(t("Already have an account? Log in"))
        }
        Spacer(Modifier.height(24.dp))
    }
}
