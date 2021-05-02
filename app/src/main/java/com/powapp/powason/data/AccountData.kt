package com.powapp.powason.data

data class AccountData (
    var dataEntity: DataEntity?,
    var breach: List<Breach>
) {
    fun isEmpty(): Boolean {
        return breach.isEmpty()
    }
}