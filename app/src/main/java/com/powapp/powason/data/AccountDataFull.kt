package com.powapp.powason.data

data class AccountDataFull (
    var dataEntity: DataEntity?,
    var breachInfoData: List<BreachInfo>
) {
    fun isEmpty(): Boolean {
        return breachInfoData.isEmpty()
    }
}
