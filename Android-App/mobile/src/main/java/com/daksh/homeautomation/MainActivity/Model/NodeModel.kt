package com.daksh.homeautomation.MainActivity.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity(useNoArgConstructor = true)
class NodeModel {

    @Id var id: Long = 0

    @Expose
    @SerializedName("_id")
    var nodeId: String? = null

    @Expose
    @SerializedName("Name")
    var nodeName: String? = null

    @Expose
    @SerializedName("Description")
    var nodeDescription:String? = null

    @Expose
    @SerializedName("isNodeTurnedOn")
    var isNodeTurnedOn: Boolean? = false

    @Expose
    @SerializedName("isActivated")
    var isNodeActivated: Boolean? = false
}