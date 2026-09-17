package com.alfredang.sgbus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

data class BusStop(
    val id: String,
    val roadName: String,
    val description: String,
) {
    val title: String = description
    val subtitle: String = "$roadName · $id"

    fun matches(query: String): Boolean {
        return id.contains(query, ignoreCase = true) ||
            roadName.contains(query, ignoreCase = true) ||
            description.contains(query, ignoreCase = true)
    }

    companion object {
        val popular = listOf(
            BusStop("01012", "Victoria St", "Hotel Grand Pacific"),
            BusStop("01013", "Victoria St", "St. Joseph's Ch"),
            BusStop("01112", "Victoria St", "Bugis Stn Exit A"),
            BusStop("02049", "Bras Basah Rd", "Raffles Hotel"),
            BusStop("02151", "Stamford Rd", "Capitol Bldg"),
            BusStop("03217", "Orchard Rd", "Opp Somerset Stn"),
            BusStop("09047", "Bayfront Ave", "Marina Bay Sands"),
            BusStop("14141", "Holland Rd", "Holland Village"),
            BusStop("22009", "Jurong Gateway Rd", "Jurong East Int"),
            BusStop("75009", "Woodlands Rd", "Bt Panjang Temp Bus Pk"),
        )
    }
}

@Serializable
data class BusArrivalResponse(
    @SerialName("Services") val services: List<BusService> = emptyList(),
)

@Serializable
data class BusService(
    @SerialName("ServiceNo") val serviceNo: String,
    @SerialName("Operator") val operatorName: String,
    @SerialName("NextBus") val nextBus: NextBus = NextBus(),
    @SerialName("NextBus2") val nextBus2: NextBus = NextBus(),
    @SerialName("NextBus3") val nextBus3: NextBus = NextBus(),
)

@Serializable
data class NextBus(
    @SerialName("EstimatedArrival") val estimatedArrival: String = "",
    @SerialName("Load") val load: String = "",
    @SerialName("Feature") val feature: String = "",
    @SerialName("Type") val type: String = "",
) {
    val arrivalText: String
        get() {
            if (estimatedArrival.isBlank()) return "-"
            val arrival = try {
                OffsetDateTime.parse(estimatedArrival)
            } catch (_: DateTimeParseException) {
                return "-"
            }
            val seconds = Duration.between(OffsetDateTime.now(), arrival).seconds.coerceAtLeast(0)
            val minutes = kotlin.math.ceil(seconds / 60.0).toInt()
            return if (minutes == 0) "Arr" else "${minutes}m"
        }

    val loadText: String
        get() = when (load) {
            "SEA" -> "Seats"
            "SDA" -> "Standing"
            "LSD" -> "Limited"
            else -> ""
        }

    val typeText: String
        get() = when (type) {
            "SD" -> "Single"
            "DD" -> "Double"
            "BD" -> "Bendy"
            else -> type
        }
}
