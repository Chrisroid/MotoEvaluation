package dev.chris.motoevaluation.data.remote

import dev.chris.motoevaluation.data.dto.SocketEventDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlin.coroutines.coroutineContext

class KtorRealTimeService {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            pingInterval = 20_000 // Keep connection alive
        }
        install(ContentNegotiation) {
            json(jsonParser)
        }
    }

    fun observeEvents(): Flow<SocketEventDto> = flow {
        while (coroutineContext.isActive) {
            try {
                println("🔌 Connecting to WebSocket...")

                // 10.0.2.2 is the Emulator's alias for localhost
                client.webSocket(host = "192.168.0.185", port = 8080, path = "/") {
                    println("✅ Connected!")

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            try {
                                val dto = jsonParser.decodeFromString<SocketEventDto>(text)
                                emit(dto)
                            } catch (e: Exception) {
                                println("Parse Error: ${e.message}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ Connection Error: ${e.message}")
                // Retry Logic (Requirement 1)
                delay(3000)
            }
        }
    }

    fun close() {
        client.close()
    }
}