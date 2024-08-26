package com.example.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()

        registerViewModel.isRegisterSuccess.observe(this) { isRegisterSuccess ->
            if (isRegisterSuccess) {
                Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        registerViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    registerViewModel.register(name, email, password)

                }
            }
        }



        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        // Animasi imageView dengan penggunaan translation, scale, dan alpha
        val imageViewAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
                    duration = 3000
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                },
                ObjectAnimator.ofFloat(binding.imageView, View.SCALE_X, 1f, 1.2f).apply {
                    duration = 1500
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                },
                ObjectAnimator.ofFloat(binding.imageView, View.SCALE_Y, 1f, 1.2f).apply {
                    duration = 1500
                    repeatCount = ObjectAnimator.INFINITE
                    repeatMode = ObjectAnimator.REVERSE
                },
                ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 0f, 1f).apply {
                    duration = 3000
                }
            )
        }

        // Menyusun elemen yang akan di animasi
        val animatedItems = listOf(
            binding.titleTextView,
            binding.emailTextView,
            binding.emailEditTextLayout,
            binding.passwordTextView,
            binding.passwordEditTextLayout,
            binding.loginButton
        )

        // Membuat animasi set untuk masing-masing elemen
        val elementsAnimatorSet = AnimatorSet().apply {
            playTogether(animatedItems.map { view ->
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
                        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 50f, 0f)
                    )
                }
            })
            startDelay = 500
            duration = 500
        }

        // Membuat animator set besar yang berisi semua animasi
        val allAnimations = AnimatorSet().apply {
            playTogether(
                imageViewAnimator,
                elementsAnimatorSet
            )
        }

        // Memulai semua animasi
        allAnimations.start()
    }


}
