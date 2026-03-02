package com.riki.vojo.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Centralized ad manager for Vojo.
 * - Banner ads: shown on Map, Learning, and Passport screens for free users
 * - Interstitial ads: shown after every 2nd quest completion and when switching tabs
 * - PRO users: never see any ads
 */
object AdManager {
    private const val TAG = "VojoAds"

    // Test ad unit IDs (replace with real ones before production)
    private const val BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isInitialized = false
    private var actionCounter = 0 // Track actions to show interstitial periodically

    /** Initialize AdMob SDK - call once from Application or MainActivity */
    fun initialize(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) {
            isInitialized = true
            Log.d(TAG, "AdMob initialized")
        }
        loadInterstitial(context)
    }

    /** Create a banner AdView for embedding in Compose */
    fun createBannerAdView(context: Context): AdView {
        return AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BANNER_AD_UNIT
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            loadAd(AdRequest.Builder().build())
        }
    }

    /** Load an interstitial ad */
    private fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Interstitial loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    Log.e(TAG, "Interstitial failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Track an action and show interstitial every N actions.
     * Actions: completing a quest, switching tabs, opening a place detail.
     */
    fun trackAction(activity: Activity, isProUser: Boolean) {
        if (isProUser) return
        actionCounter++
        if (actionCounter >= 3) {
            showInterstitial(activity, isProUser)
            actionCounter = 0
        }
    }

    /** Show an interstitial ad if available */
    fun showInterstitial(activity: Activity, isProUser: Boolean) {
        if (isProUser) return
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    loadInterstitial(activity)
                }
            }
            ad.show(activity)
        } ?: loadInterstitial(activity)
    }
}
