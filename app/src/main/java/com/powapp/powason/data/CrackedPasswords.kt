package com.powapp.powason.data

class CrackedPasswords (
    var dataEntity: DataEntity?,
    var crackedCollection: String
) {
    fun isEmpty(): Boolean {
        return crackedCollection.isEmpty()
    }

    //Translates raw string data into list, then iterates over list to see if it matches
    //stored password
    fun getCrackedCount(): String {
        val crackedList: List<String> = crackedCollection.split("\n")
        for (password in crackedList) {
            val separator: Int = password.indexOf(':')
            if (password.substring(0, separator) ==
                dataEntity?.passwordHash!!.substring(5, dataEntity?.passwordHash!!.length)) {
                return password.substring(separator + 1, password.length)
            }
        }
        return "0"
    }
}