package com.example.greedygoose.foreground.movementModule

import android.app.Service
import android.media.MediaPlayer
import com.example.greedygoose.data.Action
import com.example.greedygoose.data.Direction
import com.example.greedygoose.data.GooseState
import java.util.*

fun gooseWalkImageSetter(isAngry: Boolean, isRight: Boolean,
                         action: Action?, gooseState: GooseState?): Action {
    var nextState = if (isAngry || gooseState == GooseState.PROD_GOOSE) {
        if (isRight) {
            when (action) {
                Action.ANGRY_RIGHT -> Action.ANGRY_RIGHT_MIDDLE
                Action.ANGRY_RIGHT_MIDDLE -> Action.ANGRY_RIGHT2
                else -> Action.ANGRY_RIGHT
            }
        } else {
            when (action) {
                Action.ANGRY_LEFT -> Action.ANGRY_LEFT_MIDDLE
                Action.ANGRY_LEFT_MIDDLE -> Action.ANGRY_LEFT2
                else -> Action.ANGRY_LEFT
            }
        }
    } else {
        if (isRight) {
            when (action) {
                Action.WALKING_RIGHT -> Action.WALKING_RIGHT_MIDDLE
                Action.WALKING_RIGHT_MIDDLE -> Action.WALKING_RIGHT2
                else -> Action.WALKING_RIGHT
            }
        } else {
            when (action) {
                Action.WALKING_LEFT -> Action.WALKING_LEFT_MIDDLE
                Action.WALKING_LEFT_MIDDLE -> Action.WALKING_LEFT2
                else -> Action.WALKING_LEFT
            }
        }
    }
    return nextState
}

fun gooseSitImageSetter(direction: Direction, gooseState: GooseState?, context: Service?): Action {
    if (gooseState == GooseState.PROD_GOOSE) {
        val mediaPlayer = MediaPlayer()
        var afd = context?.getAssets()?.openFd("honk.mp3")
        mediaPlayer.setDataSource(afd?.getFileDescriptor())
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
    // After walking, make the goose sit sometimes
    var nextAction = if (Random().nextInt(10) > 5) {
        if (direction == Direction.LEFT) Action.SITTING_LEFT else Action.SITTING_RIGHT
    } else {
        if (direction == Direction.LEFT && gooseState == GooseState.PROD_GOOSE) Action.ANGRY_LEFT
        else if (gooseState == GooseState.PROD_GOOSE) Action.ANGRY_RIGHT
        else if (direction == Direction.LEFT) Action.WALKING_LEFT else Action.WALKING_RIGHT
    }
    return nextAction
}

fun gooseFlyImageSetter(direction: Direction, action: Action?): Action {
    var nextState =
        if (direction == Direction.RIGHT) {
            when (action) {
                Action.FLYING_RIGHT -> Action.FLYING_RIGHT_MIDDLE
                Action.FLYING_RIGHT_MIDDLE -> Action.FLYING_RIGHT2
                else -> Action.FLYING_RIGHT
            }
        } else {
            when (action) {
                Action.FLYING_LEFT -> Action.FLYING_LEFT_MIDDLE
                Action.FLYING_LEFT_MIDDLE -> Action.FLYING_LEFT2
                else -> Action.FLYING_LEFT
            }
        }

    return nextState
}