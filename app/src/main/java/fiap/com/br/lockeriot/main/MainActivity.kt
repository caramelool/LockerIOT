package fiap.com.br.lockeriot.main

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import dagger.android.AndroidInjection
import fiap.com.br.lockeriot.R
import fiap.com.br.lockeriot.extension.observe
import fiap.com.br.lockeriot.extension.playAnimation
import fiap.com.br.lockeriot.fingerprint.FingerprintStatus
import fiap.com.br.lockeriot.main.LottieColorManager.LottieColor
import fiap.com.br.lockeriot.repository.LockerStatus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private val fingerLottieManager by lazy { LottieColorManager(fingerLottie) }
    private val lockLottieManager by lazy { LottieColorManager(lockLottie) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        with(viewModel) {
            lifecycle.addObserver(this)
            observe(fingerprintLiveData, ::onFingerprintChange)
            observe(lockerStatusLiveData, ::onLockerChange)
        }
    }

    private fun onFingerprintChange(status: FingerprintStatus?) {
        when (status) {
            is FingerprintStatus.Success -> {
                fingerLottie.visibility = View.VISIBLE
                fingerLottieManager.setColor(LottieColor.GREEN)
            }
            is FingerprintStatus.Failure,
            is FingerprintStatus.Error -> {
                fingerLottie.visibility = View.VISIBLE
                fingerLottieManager.setColor(LottieColor.RED)
            }
            is FingerprintStatus.Idle -> {
                fingerLottieManager.playAnimation( 1f, 0f)
            }
        }
    }

    private fun onLockerChange(status: LockerStatus?) {
        when (status) {
            is LockerStatus.Open -> {
                lockLottieManager.playAnimation(0f, 0.5f)
            }
            is LockerStatus.Close -> {
                lockLottieManager.playAnimation(0.5f, 1f)
            }
        }
    }
}

class LottieColorManager(
       private val lottieView: LottieAnimationView
) : LottieValueCallback<ColorFilter>(LottieColor.GRAY.value) {

    init {
        lottieView.addValueCallback(
                KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                this)
    }

    enum class LottieColor(val value: PorterDuffColorFilter) {
        NONE(PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP)),
        GRAY(PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP)),
        RED(PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)),
        GREEN(PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP))
    }

    fun setColor(color: LottieColor, playAnimation: Boolean = true) {
        setValue(color.value)
        lottieView.playAnimation()
    }

    fun playAnimation(start: Float, end: Float) {
        lottieView.playAnimation(start, end)
    }
}
