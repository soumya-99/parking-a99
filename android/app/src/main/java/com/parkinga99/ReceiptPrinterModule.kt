package com.parkinga99

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.BaseActivityEventListener
import com.eze.api.EzeAPI
import org.json.JSONObject
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.app.Activity
import android.content.Intent
import java.io.File

class ReceiptPrinterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    // Existing request codes
    private val REQUEST_CODE_INITIALIZE = 10001
    private val REQUEST_CODE_PRINT_BITMAP = 10029

    // New request codes from EzeConstants
    private val REQUEST_CODE_PAY = 1019
    private val REQUEST_CODE_PREPARE_DEVICE = 1002

    private var callback: Callback? = null

    init {
        reactContext.addActivityEventListener(object : BaseActivityEventListener() {
            override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
                when (requestCode) {
                    REQUEST_CODE_INITIALIZE -> handleInitializeResult(resultCode, data)
                    REQUEST_CODE_PRINT_BITMAP -> handlePrintResult(resultCode, data)
                    REQUEST_CODE_PAY -> handlePayResult(resultCode, data)
                    REQUEST_CODE_PREPARE_DEVICE -> handlePrepareDeviceResult(resultCode, data)
                }
            }
        })
    }

    override fun getName(): String {
        return "ReceiptPrinter"
    }

    @ReactMethod
    fun initializeEzeAPI(callback: Callback) {
        val activity = currentActivity
        if (activity != null) {
            val jsonRequest = JSONObject().apply {
                put("demoAppKey", "a40c761a-b664-4bc6-ab5a-bf073aa797d5")
                put("prodAppKey", "a40c761a-b664-4bc6-ab5a-bf073aa797d5")
                put("merchantName", "SYNERGIC_SOFTEK_SOLUTIONS")
                put("userName", "9903044748")
                put("currencyCode", "INR")
                put("appMode", "DEMO")
                put("captureSignature", false)
                put("prepareDevice", false)
            }
            this.callback = callback
            EzeAPI.initialize(activity, REQUEST_CODE_INITIALIZE, jsonRequest)
        } else {
            callback.invoke("Activity is null")
        }
    }

    private fun handleInitializeResult(resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra("response")) {
            try {
                val response = JSONObject(data.getStringExtra("response")!!)
                if (resultCode == Activity.RESULT_OK && response.has("result")) {
                    callback?.invoke(response.toString())
                } else if (resultCode == Activity.RESULT_CANCELED && response.has("error")) {
                    val error = response.getJSONObject("error")
                    val errorCode = error.getString("code")
                    val errorMessage = error.getString("message")
                    callback?.invoke("Initialization failed: $errorCode - $errorMessage")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke("Exception: ${e.message}")
            }
        }
    }

    @ReactMethod
    fun printCustomReceipt(base64String: String, callback: Callback) {
        val activity = currentActivity
        if (activity != null) {
            val bitmap = decodeBase64ToBitmap(base64String)
            val jsonRequest = JSONObject()
            val jsonImageObj = JSONObject()

            val encodedImageData = getEncoded64ImageStringFromBitmap(bitmap)
            jsonImageObj.put("imageData", encodedImageData)
            jsonImageObj.put("imageType", "JPEG")
            jsonImageObj.put("height", "")  // optional
            jsonImageObj.put("weight", "")  // optional

            jsonRequest.put("image", jsonImageObj)
            this.callback = callback
            EzeAPI.printBitmap(activity, REQUEST_CODE_PRINT_BITMAP, jsonRequest)
        } else {
            callback.invoke("Activity is null")
        }
    }

    @ReactMethod
    fun printLargeReceipt(filePath: String, callback: Callback) {
        val activity = currentActivity
        if (activity != null) {
            val file = File(filePath)
            if (!file.exists()) {
                callback.invoke("File not found at $filePath")
                return
            }
            val jsonRequest = JSONObject()
            val jsonImageObj = JSONObject()
            jsonImageObj.put("imageData", filePath)
            jsonImageObj.put("imageType", "file")
            jsonImageObj.put("height", "")  // optional
            jsonImageObj.put("weight", "")  // optional

            jsonRequest.put("image", jsonImageObj)
            this.callback = callback
            EzeAPI.printBitmap(activity, REQUEST_CODE_PRINT_BITMAP, jsonRequest)
        } else {
            callback.invoke("Activity is null")
        }
    }

    // --- New Methods Inserted ---

    @ReactMethod
    fun pay(jsonString: String, callback: Callback) {
        val activity = currentActivity
        if (activity != null) {
            try {
                val jsonRequest = JSONObject(jsonString)
                this.callback = callback
                EzeAPI.pay(activity, REQUEST_CODE_PAY, jsonRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.invoke("Exception: ${e.message}")
            }
        } else {
            callback.invoke("Activity is null")
        }
    }

    @ReactMethod
    fun prepareDevice(callback: Callback) {
        val activity = currentActivity
        if (activity != null) {
            this.callback = callback
            EzeAPI.prepareDevice(activity, REQUEST_CODE_PREPARE_DEVICE)
        } else {
            callback.invoke("Activity is null")
        }
    }

    // --- Handlers for New Methods ---
    private fun handlePayResult(resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra("response")) {
            try {
                // Return the full response as a JSON string
                val response = JSONObject(data.getStringExtra("response")!!)
                callback?.invoke(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke("Exception: ${e.message}")
            }
        }
    }

    private fun handlePrepareDeviceResult(resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra("response")) {
            try {
                val response = JSONObject(data.getStringExtra("response")!!)
                callback?.invoke(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke("Exception: ${e.message}")
            }
        }
    }

    private fun handlePrintResult(resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra("response")) {
            try {
                val response = JSONObject(data.getStringExtra("response")!!)
                callback?.invoke(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                callback?.invoke("Exception: ${e.message}")
            }
        }
    }

    private fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
