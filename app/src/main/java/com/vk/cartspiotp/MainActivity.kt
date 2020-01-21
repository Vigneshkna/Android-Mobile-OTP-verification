package com.vk.cartspiotp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var btnGenerateOTP: Button
    var btnSignIn: Button? = null
    var etPhoneNumber: EditText? = null
    var etOTP: EditText? = null
    var phoneNumber: String? = null
    var otp: String? = null
    var auth: FirebaseAuth? = null
    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        StartFirebaseLogin()
        btnGenerateOTP!!.setOnClickListener {
                phoneNumber = etPhoneNumber!!.text.toString()
            sendVerificationCode(phoneNumber!!);

        }
        btnSignIn!!.setOnClickListener{
                otp = etOTP!!.text.toString()
                val credential =
                    PhoneAuthProvider.getCredential(verificationCode!!, otp!!)
                SigninWithPhone(credential)
            }

    }
    private fun sendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallback
        ) // OnVerificationStateChangedCallbacks
    }
    private fun SigninWithPhone(credential: PhoneAuthCredential) {
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@MainActivity, SignedIn::class.java))
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Incorrect OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    private fun findViews() {
        btnGenerateOTP = findViewById(R.id.btn_generate_otp)
        btnSignIn = findViewById(R.id.btn_sign_in)
        etPhoneNumber = findViewById(R.id.et_phone_number)
        etOTP = findViewById(R.id.et_otp)
    }

    private fun StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance()
        mCallback = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Toast.makeText(this@MainActivity, "verification completed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, "verification fialed", Toast.LENGTH_SHORT).show()
            }
            override fun onCodeSent(
                s: String,
                forceResendingToken: ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationCode = s
                Toast.makeText(this@MainActivity, "Code sent", Toast.LENGTH_SHORT).show()
            }
        }
    }
}