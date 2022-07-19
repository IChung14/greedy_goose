package com.example.greedygoose.timer

class TimerStateContext {

   private var state: TimerState? = null

   fun setState(state: TimerState?) {
      this.state = state
   }

   fun getState(): TimerState? {
      return state
   }
}