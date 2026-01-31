package codesign.redtiger.jetbrainslivemarkdown

import java.util.UUID
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ImageNameGenerator {
    fun generate(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val randomStr = UUID.randomUUID().toString().substring(0, 8)
        return "IMG_${timestamp}_${randomStr}.png"
    }
}