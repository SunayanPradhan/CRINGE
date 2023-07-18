package com.sunayanpradhan.cringe.Interfaces

interface AutocompletePresenter<T> {
    fun getAutocompleteSuggestions(input: String): List<T>
}