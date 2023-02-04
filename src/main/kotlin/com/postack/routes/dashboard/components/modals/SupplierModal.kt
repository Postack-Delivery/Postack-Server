package com.postack.routes.dashboard.components.modals

import com.postack.routes.dashboard.components.inputField
import com.postack.routes.dashboard.components.modal
import com.postack.util.C
import kotlinx.html.*

fun MAIN.supplierModal(
    title: String,
    id: String,
    action: String
) {
    modal(
        title = title,
        action = action,
        identifier = id,
    ) {
        inputField(
            label = C.SUPPLIER_NAME,
            named = C.SUPPLIER_NAME,
            identifier = "${title.split(" ").first().lowercase()}-supplier-name"
        )

        inputField(
            label = C.SUPPLIER_LOCATION,
            annotation = "(comma separated i.e 32º,-12º):",
            inputHeight = "sm-10",
            named = C.SUPPLIER_LOCATION.split(" ").first(),
            identifier = "${title.split(" ").first().lowercase()}-supplier-location"
        )

        inputField(
            label = C.SUPPLIER_CITY,
            named = C.SUPPLIER_CITY,
            identifier = "${title.split(" ").first().lowercase()}-supplier-city"
        )
    }
}


