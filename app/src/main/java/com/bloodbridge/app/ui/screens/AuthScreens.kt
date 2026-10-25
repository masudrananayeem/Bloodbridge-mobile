package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
            title = { Text("Reset password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("We'll email you a link to set a new password.")
                    AppTextField(resetEmail, { resetEmail = it }, "Email", keyboardType = KeyboardType.Email)
                    ErrorBanner(vm.error)
                }
            },
            confirmButton = {
                TextButton(enabled = !vm.busy, onClick = { vm.resetPassword(resetEmail) { showReset = false } }) {
                    Text(if (vm.busy) "Sending…" else "Send link")
                }
            },
            dismissButton = { TextButton(onClick = { showReset = false; vm.clearError() }) { Text("Cancel") } },
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
        Spacer(Modifier.height(48.dp))
        Text("BloodBridge", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Brand)
        Text("Welcome back", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text("Log in to continue saving lives.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        AppTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        AppTextField(password, { password = it }, "Password", password = true)
        if (!showReset) ErrorBanner(vm.error)
        vm.info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        TextButton(onClick = { vm.clearError(); resetEmail = email; showReset = true }) { Text("Forgot password?") }
        PrimaryButton("Log in", vm.busy, { vm.login(email, password) })

        TextButton(onClick = { vm.clearError(); onGoRegister() }, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? Register")
        }
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
        Text("Create your account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Join the network of donors and seekers.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        AppTextField(fullName, { fullName = it }, "Full name")
        AppTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        AppTextField(phone, { phone = it }, "Phone (01XXXXXXXXX)", keyboardType = KeyboardType.Phone)
        DropdownField("Blood group", bloodGroup, Constants.bloodGroups, { bloodGroup = it })
        DropdownField("Gender", gender, Constants.genders, { gender = it })
        AppTextField(dob, { dob = it }, "Date of birth", placeholder = "YYYY-MM-DD", keyboardType = KeyboardType.Number)
        DropdownField("District", district, Constants.districts, { district = it })
        AppTextField(upazila, { upazila = it }, "Upazila")
        AppTextField(address, { address = it }, "Present address")
        AppTextField(password, { password = it }, "Password", password = true)
        AppTextField(confirm, { confirm = it }, "Confirm password", password = true)

        ErrorBanner(vm.error)
        PrimaryButton("Register", vm.busy, {
            vm.register(fullName, email, password, confirm, phone, bloodGroup, gender, dob, district, upazila, address)
        })

        TextButton(onClick = { vm.clearError(); onGoLogin() }, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? Log in")
        }
        Spacer(Modifier.height(24.dp))
    }
}
