package com.example.cocktailapp.api

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleAuthClient(private val context: Context){
    private val credentialManager = CredentialManager.create(context)

    fun singIn(
        coroutineScope: CoroutineScope,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        val webClient = "186773952106-29q7bvb0fp3b7gdoimfoaru2r8r2reqq.apps.googleusercontent.com"

        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = webClient
        )
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ){
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    onSuccess(googleIdTokenCredential.id)
                } else {
                    onError("Unknown authorization type")
                }
            } catch (e: GetCredentialException){
                Log.e("GoogleAuth", "Error: ${e.message}")
                onError(e.message ?: "Login error")
            } catch (e: Exception){
                Log.e("GoogleAuth", "Error: ${e.message}")
                onError(e.message ?: "Unknow error")
            }
        }
    }
}