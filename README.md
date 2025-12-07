# Moto Real-Time Bidding Simulation (Android)

This project is a native Android (Kotlin) simulation of Moto's real-time bidding and negotiation platform. It demonstrates proficiency in concurrency, WebSocket handling, clean architecture, and lifecycle management.

## 🏗 Architecture
The app adheres to **Clean Architecture** principles, utilising **MVVM** (Model-View-ViewModel) design pattern.

* **Presentation Layer:** Jetpack Compose (UI) + ViewModel (State Management).
* **Domain Layer:** Pure Kotlin models and Repository interfaces. Contains business logic isolated from the Android framework.
* **Data Layer:** Ktor Client for WebSockets, DTOs, and Repository implementation.

## Timer Implementation (Lifecycle-Safe)
To ensure the timer remains accurate even when the app is backgrounded, or the process is paused by the OS (Doze mode), the timer **does not count ticks**.
Instead, it calculates the `targetTime` (`startTime + duration`) and derives the UI progress using `System.currentTimeMillis()`.
* **Advantage:** If the app is backgrounded for 5 seconds, upon resuming, the timer immediately reflects the correct remaining time without "freezing" or drifting.

## WebSocket Handling
* **Library:** Ktor Client with OkHttp Engine.
* **Resilience:** The connection logic is wrapped in a `while(isActive)` coroutine loop.
* **Reconnection:** If the connection drops or the server is unreachable, the app catches the exception, delays for 3 seconds, and automatically attempts to reconnect.

## State Flow
1.  **Server:** Emits JSON events (`NEGOTIATION` or `BID`).
2.  **Repository:** Maps DTOs to Domain Sealed Classes.
3.  **ViewModel:**
    * Filters duplicate bids to prevent UI key crashes.
    * Sorts bids by amount (Ascending).
    * Updates the `MutableStateFlow<AuctionState>`.
4.  **UI:** Consumes state via `collectAsStateWithLifecycle` to ensure UI updates stop when the Activity is stopped.

## Known Limitations & Setup
* **Backend:** This app requires a local WebSocket server to function.
* **Server Script:** A Node.js mock server script (`server.js`) is included in the MockServer directory.
* **Running:**
    1.  Ensure your phone/emulator and computer are on the same Wi-Fi.
    2.  Update the IP address in `KtorRealTimeService.kt` to your local machine's IP.
    3.  Run `node server.js` before launching the app.
