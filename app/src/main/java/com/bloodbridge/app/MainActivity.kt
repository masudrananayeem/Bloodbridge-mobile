package com.bloodbridge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.BloodBridgeTheme
import com.bloodbridge.app.ui.screens.MainShell
import com.bloodbridge.app.ui.screens.LoginScreen
import com.bloodbridge.app.ui.screens.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.bloodbridge.app.i18n.Lang.init(this)
        com.bloodbridge.app.ui.ThemePref.init(this)
        setContent {
            BloodBridgeTheme {
                Surface(modifier = Modifier.fillMaxSize()) { Root() }
            }
        }
    }
}

@Composable
private fun Root(vm: AppViewModel = viewModel()) {
    var showRegister by rememberSaveable { mutableStateOf(false) }
    when {
        vm.booting -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        vm.user != null && vm.user?.profileComplete == false -> com.bloodbridge.app.ui.screens.CompleteProfileScreen(vm)
        vm.user != null -> MainShell(vm)
        showRegister -> RegisterScreen(vm, onGoLogin = { showRegister = false })
        else -> LoginScreen(vm, onGoRegister = { showRegister = true })
    }
}
