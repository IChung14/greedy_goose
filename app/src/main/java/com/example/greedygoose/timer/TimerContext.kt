package com.example.greedygoose.timer

class TimerContext {
   private var state: TimerState? = null
   fun setState(state: TimerState?) {
      this.state = state
   }

   fun getState(): State? {
      return state
   }
}