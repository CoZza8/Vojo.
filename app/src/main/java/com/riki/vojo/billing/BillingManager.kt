package com.riki.vojo.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Google Play Billing Manager for Vojo PRO.
 *
 * Product ID: "vojo_pro" — One-time purchase (€4.99)
 *
 * Setup required in Google Play Console:
 * 1. Create an in-app product with ID "vojo_pro"
 * 2. Set price to €4.99
 * 3. Set type to "One-time" (managed product)
 *
 * Cost to you: $0. Google takes 15% commission on first $1M revenue.
 */
object BillingManager {
    private const val TAG = "VojoBilling"
    private const val PRODUCT_ID = "vojo_pro"

    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Initialize billing client. Call from MainActivity.onCreate().
     */
    fun initialize(context: Context) {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(context, purchase)
                    }
                }
            }
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing setup successful")
                    queryProduct()
                    checkExistingPurchases(context)
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    private fun queryProduct() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetails = productDetailsList.firstOrNull()
                Log.d(TAG, "Product found: ${productDetails?.name}")
            }
        }
    }

    /**
     * Launch the purchase flow. Call when user taps "Buy PRO".
     */
    fun launchPurchase(activity: Activity) {
        val details = productDetails
        if (details == null) {
            Toast.makeText(
                activity,
                "⏳ Purchase loading... Please try again in a moment.",
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "Product details not available. Configure 'vojo_pro' in Google Play Console.")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    private fun handlePurchase(context: Context, purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged")
                    }
                }
            }
            unlockPro(context)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(context, "🏆 Welcome to Vojo PRO! All features unlocked!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun unlockPro(context: Context) {
        val prefs = context.getSharedPreferences("vojo_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("is_pro_user", true).apply()
        scope.launch {
            com.riki.vojo.data.UserPreferencesRepository(context).setProUser(true)
        }
    }

    private fun checkExistingPurchases(context: Context) {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchasesList) {
                    if (purchase.products.contains(PRODUCT_ID) &&
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        unlockPro(context)
                        Log.d(TAG, "PRO purchase restored")
                    }
                }
            }
        }
    }
}
