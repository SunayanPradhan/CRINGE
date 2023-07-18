package com.sunayanpradhan.cringe.Interfaces

interface HashContract {

    interface View {
        fun displayHashedValue(hashedValue: String)
    }

    interface Presenter {
        fun calculateHash(input: String)
    }
}