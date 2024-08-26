package com.example.storyapp.view.login


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model.UserPreference
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.view.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        playAnimation()

        loginViewModel.getLoginSession().observe(this) { isLogin ->
            if (isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        loginViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }


        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    loginViewModel.login(email, password)


                }
            }
        }

        loginViewModel.getLoginSession().observe(this) { isLogin ->
            if (isLogin) {
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
            binding.messageTextView,
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


