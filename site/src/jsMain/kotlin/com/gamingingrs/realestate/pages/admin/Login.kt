package com.gamingingrs.realestate.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.gamingingrs.realestate.components.Message
import com.gamingingrs.realestate.components.composables.CustomButton
import com.gamingingrs.realestate.components.composables.OutlinedInput
import com.gamingingrs.realestate.models.User
import com.gamingingrs.realestate.models.enums.Errors
import com.gamingingrs.realestate.models.enums.Language
import com.gamingingrs.realestate.models.enums.Progress
import com.gamingingrs.realestate.models.enums.Progress.ACTIVE
import com.gamingingrs.realestate.models.enums.Progress.ERROR
import com.gamingingrs.realestate.models.enums.Progress.NOT_ACTIVE
import com.gamingingrs.realestate.utils.Fonts.FONT_ROBOTO
import com.gamingingrs.realestate.utils.Id.PASSWORD_INPUT
import com.gamingingrs.realestate.utils.Id.USERNAME_INPUT
import com.gamingingrs.realestate.utils.Image.HIDDEN_IMG
import com.gamingingrs.realestate.utils.Image.PASSWORD_IMG
import com.gamingingrs.realestate.utils.Image.USERNAME_IMG
import com.gamingingrs.realestate.utils.Image.VISIBLE_IMG
import com.gamingingrs.realestate.utils.Routes.HOME_ROUTE
import com.gamingingrs.realestate.utils.getOrDefault
import com.gamingingrs.realestate.utils.interpolateColor
import com.gamingingrs.realestate.utils.loadStrings
import com.gamingingrs.realestate.utils.rememberLoggedIn
import com.gamingingrs.realestate.utils.setDelay
import com.gamingingrs.realestate.utils.userAuthenticated
import com.gamingingrs.realestate.utils.userExist
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.document
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLInputElement

@Page
@Composable
fun LoginScreen() {

    val context = rememberPageContext()
    var isUserAuthenticated by remember { mutableStateOf<Boolean?>(null) }

    val language = produceState<Map<String, String>>(
        initialValue = emptyMap(),
        key1 = Unit
    ) {
        value = loadStrings(Language.ENGLISH)
    }

    userAuthenticated(
        isUserAuthenticated = {
            isUserAuthenticated = it
        }
    )

    when (isUserAuthenticated) {
        true -> {
            context.router.navigateTo(HOME_ROUTE)
        }

        false -> {
            LoginLayout(
                language = language.value
            )
        }

        else -> Unit
    }
}

@Composable
private fun LoginLayout(
    language: Map<String, String>
) {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    val message by remember { mutableStateOf(Message()) }
    var progress by remember { mutableStateOf(NOT_ACTIVE) }
    var passwordVisible by remember { mutableStateOf(false) }
    var borderColor by remember { mutableStateOf<CSSColorValue>(Colors.DimGray) }

    LaunchedEffect(progress) {
        launch {
            val delayTime = ANIMATION_DURATION / ANIMATION_STEPS

            suspend fun animateColor(fromColor: Color, toColor: Color) {
                for (i in 0..ANIMATION_STEPS) {
                    val fraction = i / ANIMATION_STEPS.toFloat()
                    borderColor = interpolateColor(fromColor, toColor, fraction)
                    delay(delayTime)
                }
            }

            while (progress == ACTIVE) {
                animateColor(Colors.DimGray, Colors.LightGoldenRodYellow)
                animateColor(Colors.LightGoldenRodYellow, Colors.DimGray)
            }
        }
    }

    Box(
        modifier = Modifier
            .backgroundColor(Colors.AntiqueWhite)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .padding(leftRight = 50.px, top = 20.px, bottom = 24.px)
                .border(
                    width = 1.px,
                    style = LineStyle.Dashed,
                    color = if (progress == ACTIVE) borderColor else Colors.DimGray
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            H1(
                attrs = Modifier
                    .fontFamily(FONT_ROBOTO)
                    .fontWeight(FontWeight.ExtraBold)
                    .margin(topBottom = 50.px)
                    .toAttrs()
            ) {
                Text(language.getOrDefault("app_name"))
            }

            H4(
                attrs = Modifier
                    .fontFamily(FONT_ROBOTO)
                    .fontWeight(FontWeight.ExtraLight)
                    .margin(bottom = 40.px)
                    .toAttrs()
            ) {
                Text("YOUR WAY TO NEW HOME")
            }

            OutlinedInput(
                type = InputType.Text,
                placeholder = "Username",
                id = USERNAME_INPUT,
                leadingIconPath = USERNAME_IMG,
                isDisabled = progress == ACTIVE
            )

            OutlinedInput(
                type = if (passwordVisible) InputType.Text else InputType.Password,
                placeholder = "Password",
                id = PASSWORD_INPUT,
                leadingIconPath = PASSWORD_IMG,
                trailingIconPath = if (passwordVisible) {
                    VISIBLE_IMG
                } else {
                    HIDDEN_IMG
                },
                isDisabled = progress == ACTIVE,
                onTrailingIconClicked = {
                    passwordVisible = !passwordVisible
                }
            )

            if (progress != ACTIVE) {
                CustomButton(
                    text = "Sign in",
                    onClick = {
                        scope.launch {
                            processUser(
                                onProgress = {
                                    progress = it
                                },
                                onProgressError = {
                                    this.launch {
                                        progress = ERROR
                                        message.set("${it.code()}: ${it.message()}")
                                        setDelay {
                                            message.reset()
                                            progress = NOT_ACTIVE
                                        }
                                    }
                                },
                                onMessage = {
                                    message.set(text = it)
                                },
                                navigateHome = {
                                    context.router.navigateTo(HOME_ROUTE)
                                }
                            )
                        }
                    }
                )
            }

            if (progress == ERROR || progress == ACTIVE) {
                SpanText(
                    modifier = Modifier
                        .margin(top = 24.px)
                        .width(350.px)
                        .fontFamily(FONT_ROBOTO)
                        .color(
                            when (progress) {
                                ERROR -> Colors.DarkRed
                                else -> Colors.Black
                            }
                        )
                        .textAlign(TextAlign.Center),
                    text = message.message
                )
            }
        }
    }
}

private suspend fun processUser(
    onProgress: (Progress) -> Unit,
    onMessage: (String) -> Unit,
    onProgressError: (Errors) -> Unit,
    navigateHome: () -> Unit
) {
    val username =
        (document.getElementById(USERNAME_INPUT) as? HTMLInputElement)?.value.orEmpty()
    val password =
        (document.getElementById(PASSWORD_INPUT) as? HTMLInputElement)?.value.orEmpty()

    if (username.isNotEmpty() && password.isNotEmpty()) {
        onProgress(ACTIVE)
        onMessage("PLEASE HOLD ON WHILE I CHECK IF THE USER IS IN THE SYSTEM")
        userExist(
            User(
                username = username,
                password = password
            )
        )?.let { user ->
            onMessage("WELCOME BACK")
            rememberLoggedIn(remember = true, user = user)
            navigateHome()
        } ?: run {
            onProgressError(Errors.USER_DOESNT_EXIST)
        }
    } else {
        onProgressError(Errors.INPUT_EMPTY)
    }
}

private const val ANIMATION_DURATION = 2000L
private const val ANIMATION_STEPS = 100