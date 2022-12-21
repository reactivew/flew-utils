package com.utils.flewutils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import com.utils.flewutils.Const.adgroup_key
import com.utils.flewutils.Const.adset_id_key
import com.utils.flewutils.Const.adset_key
import com.utils.flewutils.Const.af_id_key
import com.utils.flewutils.Const.af_siteid_key
import com.utils.flewutils.Const.app_campaign_key
import com.utils.flewutils.Const.campaign_id_key
import com.utils.flewutils.Const.deeplink_key
import com.utils.flewutils.Const.dev_tmz_key
import com.utils.flewutils.Const.gadid_key
import com.utils.flewutils.Const.orig_cost_key
import com.utils.flewutils.Const.secure_get_parametr
import com.utils.flewutils.Const.secure_key
import com.utils.flewutils.Const.source_key
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal suspend fun appsData(context: Context): MutableMap<String, Any>? = suspendCoroutine {
    AppsFlyerLib.getInstance().init(Const.AFID, object : AppsFlyerConversionListener {
        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
            it.resume(p0)
        }

        override fun onConversionDataFail(p0: String?) {
            it.resume(null)
        }

        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        }

        override fun onAttributionFailure(p0: String?) {
        }

    }, context)
    AppsFlyerLib.getInstance().start(context)
}

internal suspend fun testAppsData(context: Context): MutableMap<String, Any>? = suspendCoroutine {
    AppsFlyerLib.getInstance().init(Const.AFID, object : AppsFlyerConversionListener {
        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
            val test: MutableMap<String, Any> = mutableMapOf()
            test["af_status"] = "Non-organic"
            test["media_source"] = "testSource"
            test["campaign"] = "test1_test2_test3_test4_test5"
            test["adset"] = "testAdset"
            test["adset_id"] = "testAdsetId"
            test["campaign_id"] = "testCampaignId"
            test["orig_cost"] = "1.22"
            test["af_site_id"] = "testSiteID"
            test["adgroup"] = "testAdgroup"
            it.resume(test)
        }

        override fun onConversionDataFail(p0: String?) {
            it.resume(null)
        }

        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        }

        override fun onAttributionFailure(p0: String?) {
        }

    }, context)
    AppsFlyerLib.getInstance().start(context)
}

internal suspend fun faceData(context: Context): String = suspendCoroutine {
    AppLinkData.fetchDeferredAppLinkData(context) { appLinkData ->
        it.resume(appLinkData?.targetUri.toString())
    }
}

internal suspend fun testFaceData(context: Context): String = suspendCoroutine {
    AppLinkData.fetchDeferredAppLinkData(context) { _ ->
        it.resume("myapp://test1/test2/test3/test4/test5")
    }
}

fun rooted(context: Context): Boolean =
    Settings.Global.getString(context.contentResolver, Settings.Global.ADB_ENABLED) == "1"

internal suspend fun id(context: Context) = withContext(Dispatchers.Default) {
    AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString()
}

internal fun uid(context: Context): String =
    AppsFlyerLib.getInstance().getAppsFlyerUID(context).toString()

internal fun zone(): String = TimeZone.getDefault().id

internal fun appsSource(data: MutableMap<String, Any>?): String =
    data?.get("media_source").toString()

internal fun appsAdSetId(data: MutableMap<String, Any>?): String = data?.get("adset_id").toString()
internal fun appsCampaignId(data: MutableMap<String, Any>?): String =
    data?.get("campaign_id").toString()

internal fun appsAppCampaign(data: MutableMap<String, Any>?): String =
    data?.get("campaign").toString()

internal fun appsAdSet(data: MutableMap<String, Any>?): String = data?.get("adset").toString()
internal fun appsAdGroup(data: MutableMap<String, Any>?): String = data?.get("adgroup").toString()
internal fun appsOrigCost(data: MutableMap<String, Any>?): String =
    data?.get("orig_cost").toString()

internal fun appsAfSiteId(data: MutableMap<String, Any>?): String =
    data?.get("af_siteid").toString()

suspend fun data(context: Context): String = withContext(Dispatchers.IO) {
    OneSignal.initWithContext(context)
    OneSignal.setAppId("539bbf64-e8f1-4d61-8af3-0047f36c59a9")
    OneSignal.setExternalUserId(id(context))
    var collected = ""
    val d = faceData(context)
    OneSignal.sendTag("key2", d.replace("myapp://", "").substringBefore("/"))
    if (d != "null") {
        collected = "https://firegoblin.website/fa.php".toUri().buildUpon().apply {
            appendQueryParameter(secure_get_parametr, secure_key)
            appendQueryParameter(dev_tmz_key, zone())
            appendQueryParameter(gadid_key, id(context))
            appendQueryParameter(deeplink_key, d)
            appendQueryParameter(source_key, "deeplink")
            appendQueryParameter(af_id_key, "null")
            appendQueryParameter(adset_id_key, "null")
            appendQueryParameter(campaign_id_key, "null")
            appendQueryParameter(app_campaign_key, "null")
            appendQueryParameter(adset_key, "null")
            appendQueryParameter(adgroup_key, "null")
            appendQueryParameter(orig_cost_key, "null")
            appendQueryParameter(af_siteid_key, "null")
        }.build().toString()
    } else {
        val a = appsData(context)
        val c = appsAppCampaign(a)
        if (c == "null") OneSignal.sendTag("key2", "organic") else OneSignal.sendTag(
            "key2",
            c.substringBefore("_")
        )
        collected = "https://firegoblin.website/fa.php".toUri().buildUpon().apply {
            appendQueryParameter(secure_get_parametr, secure_key)
            appendQueryParameter(dev_tmz_key, zone())
            appendQueryParameter(gadid_key, id(context))
            appendQueryParameter(deeplink_key, "null")
            appendQueryParameter(source_key, appsSource(a))
            appendQueryParameter(af_id_key, uid(context))
            appendQueryParameter(adset_id_key, appsAdSetId(a))
            appendQueryParameter(campaign_id_key, appsCampaignId(a))
            appendQueryParameter(app_campaign_key, appsAppCampaign(a))
            appendQueryParameter(adset_key, appsAdSet(a))
            appendQueryParameter(adgroup_key, appsAdGroup(a))
            appendQueryParameter(orig_cost_key, appsOrigCost(a))
            appendQueryParameter(af_siteid_key, appsAfSiteId(a))
        }.build().toString()
    }
    collected
}

suspend fun testDataF(context: Context): String = withContext(Dispatchers.IO) {
    val d = testFaceData(context)
    "https://firegoblin.website/fa.php".toUri().buildUpon().apply {
        appendQueryParameter(secure_get_parametr, secure_key)
        appendQueryParameter(dev_tmz_key, zone())
        appendQueryParameter(gadid_key, id(context))
        appendQueryParameter(deeplink_key, d)
        appendQueryParameter(source_key, "deeplink")
        appendQueryParameter(af_id_key, "null")
        appendQueryParameter(adset_id_key, "null")
        appendQueryParameter(campaign_id_key, "null")
        appendQueryParameter(app_campaign_key, "null")
        appendQueryParameter(adset_key, "null")
        appendQueryParameter(adgroup_key, "null")
        appendQueryParameter(orig_cost_key, "null")
        appendQueryParameter(af_siteid_key, "null")
    }.build().toString()
}

suspend fun testDataA(context: Context): String = withContext(Dispatchers.IO) {
    val a = testAppsData(context)
    "https://firegoblin.website/fa.php".toUri().buildUpon().apply {
        appendQueryParameter(secure_get_parametr, secure_key)
        appendQueryParameter(dev_tmz_key, zone())
        appendQueryParameter(gadid_key, id(context))
        appendQueryParameter(deeplink_key, "null")
        appendQueryParameter(source_key, appsSource(a))
        appendQueryParameter(af_id_key, uid(context))
        appendQueryParameter(adset_id_key, appsAdSetId(a))
        appendQueryParameter(campaign_id_key, appsCampaignId(a))
        appendQueryParameter(app_campaign_key, appsAppCampaign(a))
        appendQueryParameter(adset_key, appsAdSet(a))
        appendQueryParameter(adgroup_key, appsAdGroup(a))
        appendQueryParameter(orig_cost_key, appsOrigCost(a))
        appendQueryParameter(af_siteid_key, appsAfSiteId(a))
    }.build().toString()
}

fun client(start: (u: String) -> Unit, finish: (u: String) -> Unit): WebViewClient =
    object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            start(url)
        }

        override fun onPageFinished(view: WebView?, url: String) {
            super.onPageFinished(view, url)
            finish(url)
        }
    }

fun chrome(call: (ValueCallback<Array<Uri>>) -> Unit): WebChromeClient =
    object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams?
        ): Boolean {

            return true
        }
    }