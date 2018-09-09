package fiap.com.br.lockeriot.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.playAnimation(
        start: Float,
        end: Float,
        duration: Long = 1000,
        onComplete: () -> Unit = {}) {
    val animator = ValueAnimator.ofFloat(start, end)
            .setDuration(duration)
    animator.addUpdateListener { animation -> this.progress = animation.animatedValue as Float }
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            onComplete()
        }
    })
    animator.start()
}