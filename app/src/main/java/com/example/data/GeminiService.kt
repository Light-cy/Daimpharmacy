package com.example.data

import android.graphics.Bitmap
import android.util.Base64
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<ContentPart>
)

@JsonClass(generateAdapter = true)
data class ContentPart(
    val parts: List<PartData>
)

@JsonClass(generateAdapter = true)
data class PartData(
    val text: String? = null,
    val inlineData: InlineDataPart? = null
)

@JsonClass(generateAdapter = true)
data class InlineDataPart(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<CandidatePart>?
)

@JsonClass(generateAdapter = true)
data class CandidatePart(
    val content: ContentPart?
)

@JsonClass(generateAdapter = true)
data class ScannedMedicineResult(
    val name: String,
    val formula: String,
    val category: String?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApi::class.java)
    }
}

object GeminiService {
    suspend fun analyzeMedicineImage(bitmap: Bitmap): ScannedMedicineResult? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ScannedMedicineResult(
                name = "Demo Panadol 100mg",
                formula = "Paracetamol",
                category = "tablets"
            )
        }

        // 1. Downscale the image to speed up upload and model response time
        val optimizedBitmap = bitmap.resizeToMaxDimension(800)
        val base64Image = optimizedBitmap.toBase64()

        val prompt = """
            You are an expert pharmacist and data extraction assistant.
            Analyze this image of a medicine packaging.
            Extract the medicine's "Name" and "Formula".
            
            Rules:
            1. The medicine's name MUST include its strength/mg/ml if visible on the packaging (e.g. "Panadol 100mg" or "Amoxil 250mg"). Always mention the mg/ml with the name itself.
            2. The formula should be the scientific chemical formula/generic name of the medicine (e.g. "Paracetamol" or "Ibuprofen").
            3. Detect the category if possible. The available categories are: "tablets", "capsules", "syrups", "injections". If category is not clear or not detected, set it to null.
            
            You MUST respond strictly in the following JSON format:
            {
              "name": "extracted name with strength",
              "formula": "extracted scientific/generic formula",
              "category": "tablets" | "capsules" | "syrups" | "injections" | null
            }
            
            Do not include any markdown formatting (like ```json), explanations, or trailing characters. Return only the raw JSON.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                ContentPart(
                    parts = listOf(
                        PartData(text = prompt),
                        PartData(inlineData = InlineDataPart(mimeType = "image/jpeg", data = base64Image))
                    )
                )
            )
        )

        // 2. Retry loop with Exponential Backoff for handling network timeouts and "High Demand" 429/503 issues
        var attempt = 0
        val maxAttempts = 3
        var lastException: Exception? = null

        while (attempt < maxAttempts) {
            try {
                val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
                val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!jsonText.isNullOrBlank()) {
                    val cleanJson = jsonText.trim()
                        .removePrefix("```json")
                        .removePrefix("```")
                        .removeSuffix("```")
                        .trim()
                    
                    val jsonObject = JSONObject(cleanJson)
                    val extractedName = jsonObject.optString("name", "").trim()
                    val extractedFormula = jsonObject.optString("formula", "").trim()
                    val extractedCategory = if (jsonObject.isNull("category")) null else jsonObject.optString("category", null)?.trim()

                    return@withContext ScannedMedicineResult(
                        name = extractedName,
                        formula = extractedFormula,
                        category = extractedCategory
                    )
                } else {
                    throw Exception("Received an empty response from Gemini API.")
                }
            } catch (e: retrofit2.HttpException) {
                lastException = e
                val code = e.code()
                // Retry on rate limit (429), service unavailable/high demand (503), gateway timeout (504), or server errors (5xx)
                if (code == 429 || code == 503 || code == 504 || code >= 500) {
                    attempt++
                    if (attempt < maxAttempts) {
                        val backoffTimeMs = 1500L * (1 shl attempt) // 3000ms, 6000ms
                        kotlinx.coroutines.delay(backoffTimeMs)
                        continue
                    }
                }
                break // Unrecoverable or exceeded maximum retries
            } catch (e: java.io.IOException) {
                // Retry on network timeouts or connectivity interruptions
                lastException = e
                attempt++
                if (attempt < maxAttempts) {
                    val backoffTimeMs = 1500L * (1 shl attempt) // 3000ms, 6000ms
                    kotlinx.coroutines.delay(backoffTimeMs)
                    continue
                }
                break
            } catch (e: Exception) {
                lastException = e
                break // Non-transient exceptions (like JSON parsing errors) shouldn't be retried
            }
        }

        // If we reach here, it failed. Handle the final exception throw.
        val finalException = lastException ?: Exception("Unknown error occurred during analysis.")
        if (finalException is retrofit2.HttpException) {
            val errorBody = finalException.response()?.errorBody()?.string()
            val parsedError = try {
                val errorObj = JSONObject(errorBody ?: "")
                val errorMsg = errorObj.optJSONObject("error")?.optString("message")
                errorMsg ?: errorBody
            } catch (jsonEx: Exception) {
                errorBody
            }
            throw Exception("API Error (HTTP ${finalException.code()}): ${parsedError ?: finalException.message()}")
        } else {
            throw Exception("Failed to analyze image: ${finalException.message ?: finalException.toString()}")
        }
    }

    private fun Bitmap.resizeToMaxDimension(maxDim: Int): Bitmap {
        val width = this.width
        val height = this.height
        if (width <= maxDim && height <= maxDim) return this

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (ratio > 1) {
            newWidth = maxDim
            newHeight = (maxDim / ratio).toInt()
        } else {
            newHeight = maxDim
            newWidth = (maxDim * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
