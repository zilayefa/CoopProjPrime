@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.frontendmasters.filtered

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.frontendmasters.filtered.pages.OffersPage
import com.frontendmasters.filtered.ui.theme.FilteredTheme
import com.frontendmasters.filtered.ui.theme.Primary

@Preview
@Composable
fun App_Preview(){
    FilteredTheme() {
        App()
    }
}


@Composable
fun App() {

    val selectedRoute = remember {
        mutableStateOf(Routes.MenuPage.route)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppTitle() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary
                )
            )
        },

        content = { innerPadding ->
            when (selectedRoute.value) {
                Routes.MenuPage.route -> Text("Menu")
                Routes.OffersPage.route -> OffersPage(innerPadding)
                Routes.OrderPage.route -> Text("Orders")
                Routes.InfoPage.route -> Text("Info")
            }
            //OffersPage(innerPadding)
        },

        bottomBar = {
            NavBar(
                selectedRoute = selectedRoute.value,
                onChange = {
                selectedRoute.value = it
            })
        }
    )
}


@Composable
fun AppTitle(){
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Coffee Masters Logo"
        )
    }
}