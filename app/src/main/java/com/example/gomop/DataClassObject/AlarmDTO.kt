package com.example.gomop.DataClassObject

data class AlarmDTO(
    var destinationUid: String? = null,
    var userId: String? = null,
    var uid : String? = null,
    var kind : Int? = null,

    // 0 : Like
    // 1 : Comment
    // 2 : Follow
    var message : String?= null,
    var timestamp : Long?=null,
) {
}