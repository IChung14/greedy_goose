package com.example.greedygoose.foreground.movementModule


import android.animation.*
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context.POWER_SERVICE
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnTouchListener
import com.example.greedygoose.foreground.ui.FloatingWindowModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleService
import com.example.greedygoose.data.Action
import com.example.greedygoose.data.Direction
import com.example.greedygoose.data.GooseState
import com.example.greedygoose.data.themeMap
import com.example.greedygoose.foreground.FloatingViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class DragMovementModule(
    private var context: Service?,
    private val viewModel: FloatingViewModel,
    private val windowModule: FloatingWindowModule
) : MovementModule(windowModule) {

    private var animator: ValueAnimator? = null
        set(value) {
            field?.cancel()
            field = value
        }

    override fun run() {}

    override fun startAction(round: Boolean, dir: Direction) {
        // set drag listener
        drag()

        viewModel.action.observe(context as LifecycleService){
            themeMap[viewModel.theme.value]?.get(viewModel.action.value)?.let { imgSrc ->
                windowModule.binding.gooseImg.setImageResource(imgSrc)
            }
        }

        // start random movements
        var job = MainScope().launch {
            val powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager
            delay(2000)
            while (true) {
//                if (!isAlive) {
//                    println("Job died %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
//                    break;
//                }
                if (powerManager.isInteractive) {
                    if (!isDragged) {
                        var chance = Random().nextInt(2)
                        if (viewModel.appMode.value == GooseState.PROD_GOOSE && chance == 0) {
                            flyingGoose(windowModule)
                        } else {
                            randomWalk(windowModule,
                                is_meme = false,
                                round = false,
                                dir = Direction.RIGHT
                            )
                        }
                    }
                }
                delay(7000)
            }
        }
//        job.cancel()
    }

    private fun flyingGoose(window: FloatingWindowModule?) {
        isDragged = true
        isFlying = true
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var x = min(Random().nextInt(1500) - 800, width / 2 - 270)
        x = max(x, -270)
        var y = Random().nextInt(1500) - 1000
        var pvhX = PropertyValuesHolder.ofInt("x", params.x, x)
        var pvhY = PropertyValuesHolder.ofInt("y", params.y, y)
        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        var direction = Direction.NONE
        val startx = params.x
        animator?.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
            windowManager.updateViewLayout(rootContainer, layoutParams)
            if (layoutParams.x > startx) {
                direction = Direction.RIGHT
                viewModel.action.value = Action.FLYING_RIGHT
            } else {
                direction = Direction.LEFT
                viewModel.action.value = Action.FLYING_LEFT
            }
        }
        animator?.doOnEnd {
                gooseSit(direction)
                isFlying = false
                isDragged = false
        }
        animator?.duration = Random().nextInt(2000).toLong() + 1000
        animator?.start()

    }

    fun walkOffScreen(dir: String) {
        isDragged = true
        isDraggable = false

        var pvhX = PropertyValuesHolder.ofInt("x", params.x, -1080)
        if (dir == "RIGHT") pvhX = PropertyValuesHolder.ofInt("x", params.x, 1080)
        var pvhY = PropertyValuesHolder.ofInt("y", params.y, params.y)
        params.flags = params.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params.x

        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = Direction.RIGHT

        animator?.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer.layoutParams as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
            windowManager.updateViewLayout(rootContainer, layoutParams)

            // For a smoother walking animation, only change the goose img every 5 animations
            updates += 1
            if (updates % 5 == 0) {
                if (layoutParams.x > startx) {
                    direction = Direction.RIGHT
                    gooseWalkImageSetter(isAngry = false, isRight = true)
                } else {
                    direction = Direction.LEFT
                    gooseWalkImageSetter(isAngry = false, isRight = false)
                }
            }
        }

        animator?.doOnEnd {
            gooseSit(direction)

            // Allow dragging again when the animation finishes
            isDraggable = true
            isDragged = false
        }

        animator?.duration = 2500
        animator?.start()
    }

    fun randomWalk(window: FloatingWindowModule?, is_meme: Boolean?, round: Boolean, dir: Direction) {
        // Do not allow dragging while the goose is moving
        isDraggable = false
        isDragged = true

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels
        var x = min(Random().nextInt(1500) - 800, width / 2 - 270)
        x = max(x, -270)
        var y = Random().nextInt(1500) - 1000
        if (is_meme == true && !round) {
            x = if (dir == Direction.RIGHT) -200 else 200
            y = params.y
        } else if (is_meme == true && round) {
            x = if (dir == Direction.RIGHT) -1000 else 1000
            y = params.y
        }
        var pvhX = PropertyValuesHolder.ofInt("x", params.x, x)
        var pvhY = PropertyValuesHolder.ofInt("y", params.y, y)
        animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        val startx = params.x
        // Do not allow dragging while the goose is moving
        var updates = 0
        var direction = Direction.NONE
        animator?.addUpdateListener { valueAnimator ->
            val layoutParams = rootContainer.getLayoutParams() as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
            windowManager.updateViewLayout(rootContainer, layoutParams)
            updates += 1
            if (updates % 5 == 0) {
                if ((layoutParams.x > startx) xor (is_meme == true)) {
                    direction = Direction.RIGHT
                    gooseWalkImageSetter(isAngry = false, isRight = true)
                } else {
                    direction = Direction.LEFT
                    gooseWalkImageSetter(isAngry = false, isRight = false)
                }
            }
        }

        animator?.doOnEnd {
            if (!round && is_meme == true) {
                MainScope().launch {
                        viewModel.action.value =
                            if (direction == Direction.LEFT) Action.WINDOW_LEFT
                            else Action.WINDOW_RIGHT
                    delay(3500)
                    randomWalk(window, is_meme = true, round = true, dir = dir)
                }
            } else {
                gooseSit(direction)

                // Allow dragging again when the animation finishes
                isDraggable = true
                isDragged = false
            }
        }

        // change the speed of the walk depending on the mode
        if(is_meme == true){
            animator?.duration = 2000
        } else if (viewModel.appMode.value == GooseState.PROD_GOOSE){
            animator?.duration = Random().nextInt(2000).toLong() + 1000
        } else {
            animator?.duration = Random().nextInt(2000).toLong() + 2500
        }
        animator?.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun drag() {
        rootContainer.setOnTouchListener(
        object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var updates = 0
            private var direction = Direction.RIGHT
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                // prevent touch if not draggable
                if (!isDraggable) return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isDragged = true
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragged = false
                        viewModel.action.value =
                            if (direction == Direction.LEFT) Action.SITTING_LEFT
                            else Action.SITTING_RIGHT
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val prevx = params.x
                        params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        windowManager.updateViewLayout(baseView, params)
                        // For a smoother walking animation, only change the goose img every 5 animations
                        updates += 1
                        if (updates % 5 == 0) {
                            if (params.x > prevx && (abs(params.x.minus(prevx)) >= 100f)) {
                                direction = Direction.RIGHT
                                viewModel.action.value =
                                    if (viewModel.action.value == Action.ANGRY_RIGHT) Action.ANGRY_RIGHT2
                                    else Action.ANGRY_RIGHT
                            } else if (params.x < prevx && (abs(params.x.minus(prevx)) <= 100f)) {
                                direction = Direction.LEFT
                                gooseWalkImageSetter(isAngry = true, isRight = false)
                            } else {
                                // Make the goose face the right when swiping vertically
                                direction = Direction.RIGHT
                                gooseWalkImageSetter(isAngry = true, isRight = true)
                            }
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun gooseSit(direction: Direction){
        val mediaPlayer = MediaPlayer()
        var afd = context?.getAssets()?.openFd("honk.mp3")
        mediaPlayer.setDataSource(afd?.getFileDescriptor())
        mediaPlayer.prepare()
        mediaPlayer.start()
        // After walking, make the goose sit sometimes
        viewModel.action.value = if (Random().nextInt(10) > 5) {
            if (direction == Direction.LEFT) Action.SITTING_LEFT
            else Action.SITTING_RIGHT
        } else {
                if (direction == Direction.LEFT && viewModel.appMode.value == GooseState.PROD_GOOSE) Action.ANGRY_LEFT
                else if (viewModel.appMode.value == GooseState.PROD_GOOSE) Action.ANGRY_RIGHT
                else if (direction == Direction.LEFT) Action.WALKING_LEFT
                else Action.WALKING_RIGHT
        }
    }

    private fun gooseWalkImageSetter(isAngry: Boolean, isRight: Boolean){
        viewModel.action.value = if(isAngry || viewModel.appMode.value == GooseState.PROD_GOOSE){
            if(isRight){
                when (viewModel.action.value) {
                    Action.ANGRY_RIGHT -> Action.ANGRY_RIGHT_MIDDLE
                    Action.ANGRY_RIGHT_MIDDLE -> Action.ANGRY_RIGHT2
                    else -> Action.ANGRY_RIGHT
                }
            }else{
                when (viewModel.action.value) {
                    Action.ANGRY_LEFT -> Action.ANGRY_LEFT_MIDDLE
                    Action.ANGRY_LEFT_MIDDLE -> Action.ANGRY_LEFT2
                    else -> Action.ANGRY_LEFT
                }
            }
        }else{
            if(isRight){
                when (viewModel.action.value) {
                    Action.WALKING_RIGHT -> Action.WALKING_RIGHT_MIDDLE
                    Action.WALKING_RIGHT_MIDDLE -> Action.WALKING_RIGHT2
                    else -> Action.WALKING_RIGHT
                }
            }else{
                when (viewModel.action.value) {
                    Action.WALKING_LEFT -> Action.WALKING_LEFT_MIDDLE
                    Action.WALKING_LEFT_MIDDLE -> Action.WALKING_LEFT2
                    else -> Action.WALKING_LEFT
                }
            }
        }
    }
}