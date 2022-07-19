package com.example.greedygoose.timer

import com.example.greedygoose.timer.state.TimerState

class TimerStateContext {

   private var state: TimerState? = null

   fun setState(state: TimerState?) {
      this.state = state
   }

   fun getState(): TimerState? {
      return state
   }
}