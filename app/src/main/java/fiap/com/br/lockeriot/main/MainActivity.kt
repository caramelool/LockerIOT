package fiap.com.br.lockeriot.main

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import dagger.android.AndroidInjection
import fiap.com.br.lockeriot.R
import fiap.com.br.lockeriot.extension.observe
import fiap.com.br.lockeriot.fingerprint.FingerprintStatus
import fiap.com.br.lockeriot.main.LottieColorManager.LottieColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private val lottieColorManager by lazy { LottieColorManager(fingerLottie) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        with(viewModel) {
            lifecycle.addObserver(this)
            observe(statusLiveData, ::onStatusChange)
        }
    }

    private fun onStatusChange(status: FingerprintStatus?) {
        when (status) {
            is FingerprintStatus.Failure,
            is FingerprintStatus.Error -> {
                lottieColorManager.setColor(LottieColor.RED)
            }
            is FingerprintStatus.Success -> {
                lottieColorManager.setColor(LottieColor.GREEN)
            }
            is FingerprintStatus.Idle -> {
                lottieColorManager.setColor(LottieColor.GRAY)
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
        GRAY(PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP)),
        RED(PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)),
        GREEN(PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP))
    }

    fun setColor(color: LottieColor) {
        setValue(color.value)
        lottieView.playAnimation()
    }
}
