package com.ninezero.cream.base

interface MviAction
interface MviResult
interface MviEvent : MviResult
interface MviViewState
interface MviStateReducer<S : MviViewState, R : MviResult> {
    infix fun S.reduce(result: R): S
}