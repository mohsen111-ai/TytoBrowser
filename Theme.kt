package com.tyto.browser.ui.theme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
private val Scheme=darkColorScheme(primary=TytoCrimson,onPrimary=TytoWhite,background=TytoBlack,onBackground=TytoWhite,surface=TytoGunmetal,onSurface=TytoWhite,surfaceVariant=TytoGunmetal,onSurfaceVariant=TytoSilver,outline=TytoCrimsonBorder)
@Composable fun TytoTheme(content:@Composable()->Unit){MaterialTheme(colorScheme=Scheme,shapes=MaterialTheme.shapes.copy(small=RoundedCornerShape(10.dp),medium=RoundedCornerShape(16.dp),large=RoundedCornerShape(20.dp)),content=content)}
