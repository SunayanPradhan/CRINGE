package com.sunayanpradhan.cringe.Utils

import com.sunayanpradhan.cringe.Interfaces.AutocompletePresenter
import com.sunayanpradhan.cringe.Interfaces.HashContract
import java.math.BigInteger
import java.security.MessageDigest

class HashPresenter(private val view: HashContract.View, private val hash: String) : HashContract.Presenter,
    AutocompletePresenter<String> {

    override fun calculateHash(input: String) {
        // Perform hash calculation here
        val hashedValue = hashString(input)

        // Pass the hashed value to the view
        view.displayHashedValue(hashedValue)
    }

    override fun getAutocompleteSuggestions(input: String): List<String> {
        // Implement autocomplete logic here based on the input
        // Return a list of autocomplete suggestions
        return listOf("Suggestion 1", "Suggestion 2", "Suggestion 3")
    }

    private fun hashString(input: String): String {
        // Perform hash calculation using desired algorithm (e.g., MD5, SHA-1, etc.)
        // Here's an example using MD5 algorithm
        val md = MessageDigest.getInstance(hash)
        val digest = md.digest(input.toByteArray())
        val hashedValue = BigInteger(1, digest).toString(16)

        // Pad with leading zeros if necessary
        return hashedValue.padStart(32, '0')
    }
}

