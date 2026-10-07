package com.tyto.browser

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import com.tyto.browser.ui.theme.*

class MainActivity : ComponentActivity() {
 private lateinit var runtime: GeckoRuntime
 private lateinit var session: GeckoSession
 private var currentUrl by mutableStateOf("https://www.google.com")
 private var canBack by mutableStateOf(false)
 private var canForward by mutableStateOf(false)
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  runtime=GeckoRuntime.create(this); session=GeckoSession()
  session.navigationDelegate=object: GeckoSession.NavigationDelegate {
   override fun onLocationChange(s:GeckoSession,url:String?,perms:MutableList<GeckoSession.PermissionDelegate.ContentPermission>,hasUserGesture:Boolean){runOnUiThread{if(!url.isNullOrBlank())currentUrl=url}}
   override fun onCanGoBack(s:GeckoSession,v:Boolean){runOnUiThread{canBack=v}}
   override fun onCanGoForward(s:GeckoSession,v:Boolean){runOnUiThread{canForward=v}}
  }
  session.open(runtime); session.loadUri(currentUrl)
  setContent{TytoTheme{Browser(session,currentUrl,canBack,canForward,::navigate,{session.goBack()},{session.goForward()},{session.reload()},{session.loadUri("https://www.google.com")})}}
 }
 private fun navigate(input:String){val v=input.trim(); if(v.isEmpty())return; val u=when{URLUtil.isNetworkUrl(v)->v;v.startsWith("about:")->v;v.contains(" ")->"https://www.google.com/search?q=${v.replace(" ","+")}";v.contains(".")->"https://$v";else->"https://www.google.com/search?q=${v.replace(" ","+")}"};currentUrl=u;session.loadUri(u)}
 override fun onDestroy(){if(::session.isInitialized)session.close();super.onDestroy()}
}

@Composable private fun Browser(session:GeckoSession,url:String,back:Boolean,forward:Boolean,navigate:(String)->Unit,onBack:()->Unit,onForward:()->Unit,reload:()->Unit,home:()->Unit){
 var address by remember(url){mutableStateOf(url)}; val keyboard=LocalSoftwareKeyboardController.current
 Column(Modifier.fillMaxSize().background(TytoBlack).navigationBarsPadding().padding(horizontal=10.dp)){
  Spacer(Modifier.height(10.dp)); Card(Modifier.fillMaxWidth().border(1.dp,TytoCrimsonBorder,RoundedCornerShape(16.dp)),shape=RoundedCornerShape(16.dp),colors=CardDefaults.cardColors(TytoGunmetal)){Row(Modifier.fillMaxWidth().padding(6.dp),verticalAlignment=Alignment.CenterVertically){Text("TYTO",color=TytoCrimson,fontWeight=FontWeight.Black,fontSize=12.sp,letterSpacing=1.8.sp,modifier=Modifier.padding(horizontal=6.dp));TextField(value=address,onValueChange={address=it},modifier=Modifier.weight(1f),singleLine=true,textStyle=LocalTextStyle.current.copy(color=TytoWhite,fontSize=14.sp),placeholder={Text("Search or enter address",color=TytoSilver)},colors=TextFieldDefaults.colors(focusedContainerColor=Color.Transparent,unfocusedContainerColor=Color.Transparent,focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent,cursorColor=TytoCrimson,focusedTextColor=TytoWhite,unfocusedTextColor=TytoWhite),keyboardOptions=KeyboardOptions(imeAction=ImeAction.Go),keyboardActions=KeyboardActions(onGo={navigate(address);keyboard?.hide()}));Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(TytoCrimson).clickable{navigate(address);keyboard?.hide()},contentAlignment=Alignment.Center){Text("↵",color=TytoWhite,fontSize=20.sp,fontWeight=FontWeight.Bold)}}}
  Spacer(Modifier.height(8.dp)); Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(18.dp)).border(1.dp,TytoCrimsonBorder,RoundedCornerShape(18.dp)).background(Color.Black)){AndroidView(Modifier.fillMaxSize(),factory={c->GeckoView(c).apply{layoutParams=ViewGroup.LayoutParams(-1,-1);setSession(session)}})}
  Spacer(Modifier.height(8.dp)); Card(Modifier.fillMaxWidth().border(1.dp,TytoCrimsonBorder,RoundedCornerShape(18.dp)),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(TytoGunmetal)){Row(Modifier.fillMaxWidth().padding(vertical=7.dp),horizontalArrangement=Arrangement.SpaceEvenly){Nav("‹","BACK",back,onBack);Nav("›","FORWARD",forward,onForward);Nav("↻","RELOAD",true,reload);Nav("⌂","HOME",true,home);Nav("□","TABS",true,{})}};Spacer(Modifier.height(8.dp))
 }
}
@Composable private fun Nav(symbol:String,label:String,enabled:Boolean,onClick:()->Unit){Column(Modifier.clip(RoundedCornerShape(12.dp)).clickable(enabled,onClick).padding(horizontal=15.dp,vertical=3.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(symbol,color=if(enabled)TytoWhite else TytoSilver.copy(.35f),fontSize=22.sp);Text(label,color=if(enabled)TytoSilver else TytoSilver.copy(.3f),fontSize=7.sp,letterSpacing=.8.sp)}}
