package com.bloodbridge.app.ui

import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    placeholder: String? = null,
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
        visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (password) {
            { Text(if (visible) t("Hide") else t("Show"), color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickableNoRipple { visible = !visible }) }
        } else null,
        modifier = modifier.fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    anyLabel: String? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = if (selected.isBlank()) anyLabel.orEmpty() else lbl(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (anyLabel != null) {
                DropdownMenuItem(text = { Text(anyLabel) }, onClick = { onSelect(""); expanded = false })
            }
            options.forEach { option ->
                DropdownMenuItem(text = { Text(lbl(option)) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
fun PrimaryButton(text: String, busy: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, enabled = !busy, modifier = modifier.fillMaxWidth().height(50.dp)) {
        if (busy) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(text)
        }
    }
}

@Composable
fun ErrorBanner(message: String?) {
    if (message != null) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.clickable(onClick = onClick)

@Composable
fun LanguageToggle(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.material3.TextButton(
        onClick = { com.bloodbridge.app.i18n.Lang.set(context, if (com.bloodbridge.app.i18n.Lang.current == "bn") "en" else "bn") },
        modifier = modifier,
    ) {
        Text(if (com.bloodbridge.app.i18n.Lang.current == "bn") "English" else "বাংলা")
    }
}

@Composable
fun ThemeToggle(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.material3.TextButton(onClick = { ThemePref.cycle(context) }, modifier = modifier) {
        Text(
            when (ThemePref.mode) {
                "light" -> com.bloodbridge.app.i18n.t("Light")
                "dark" -> com.bloodbridge.app.i18n.t("Dark")
                else -> com.bloodbridge.app.i18n.t("System")
            }
        )
    }
}

@Composable
fun LegalLinks(modifier: Modifier = Modifier) {
    var open by remember { mutableStateOf<String?>(null) }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        androidx.compose.material3.TextButton(onClick = { open = "privacy" }) {
            Text(com.bloodbridge.app.i18n.t("Privacy Policy"), style = MaterialTheme.typography.bodySmall)
        }
        androidx.compose.material3.TextButton(onClick = { open = "terms" }) {
            Text(com.bloodbridge.app.i18n.t("Terms of Service"), style = MaterialTheme.typography.bodySmall)
        }
    }
    open?.let { LegalDialog(it) { open = null } }
}
