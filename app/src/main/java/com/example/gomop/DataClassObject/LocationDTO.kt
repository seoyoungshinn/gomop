package com.example.gomop.DataClassObject

data class LocationDTO(
    //DTO (ID,UID,X,Y,TIMEDATA)
    var id : String,
  //  var uid : String,
    var lat : Double,
    var lon : Double,
    //var timeData : String

) {
    constructor() : this("defultID",-1.1,-1.1)

}