const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 8080 });

console.log('🚀 Moto Mock Server started on port 8080');
console.log('👉 Local URL: ws://localhost:8080');
console.log('👉 Android Emulator URL: ws://10.0.2.2:8080');

wss.on('connection', (ws) => {
    console.log('📱 Client connected');

    // State for this connection
    let basePrice = 300.0;
    let isNegotiationPhase = true;
    let negotiationCount = 0;

    // --- PHASE 1: NEGOTIATION (First 5 seconds) ---
    const sendNegotiation = () => {
        if (!isNegotiationPhase || ws.readyState !== WebSocket.OPEN) return;

        // Counter offers drop slightly
        basePrice = basePrice - (Math.random() * 10);
        
        const negotiationEvent = {
            type: "NEGOTIATION",
            driverId: `Driver_${Math.floor(Math.random() * 50)}`,
            amount: parseFloat(basePrice.toFixed(2)),
            timestamp: Date.now(),
            message: `Counter offer: ${basePrice.toFixed(2)}`
        };

        ws.send(JSON.stringify(negotiationEvent));
        console.log(`[Negotiation] Sent: ${negotiationEvent.amount}`);

        negotiationCount++;
        
        // After 3 negotiation steps, switch phases
        if (negotiationCount >= 3) {
            isNegotiationPhase = false;
            console.log('⚠️ Switching to LIVE BIDDING Phase in 2s...');
            setTimeout(loopBidding, 2000);
        } else {
            setTimeout(sendNegotiation, 1500); 
        }
    };

    // --- PHASE 2: LIVE BIDDING (Infinite) ---
    const loopBidding = () => {
        if (ws.readyState !== WebSocket.OPEN) return;

        // Bids fluctuate, generally trending down (lowest wins logic)
        const drop = Math.random() > 0.3; 
        const change = (Math.random() * 10);
        basePrice = drop ? (basePrice - change) : (basePrice + 5);

        // Reset if it gets too low/unrealistic
        if(basePrice < 50) basePrice = 280.0;

        const bidEvent = {
            type: "BID",
            driverId: `Driver_${Math.floor(Math.random() * 5) + 1}`, // 5 Drivers competing
            amount: parseFloat(basePrice.toFixed(2)),
            timestamp: Date.now()
        };

        ws.send(JSON.stringify(bidEvent));
        console.log(`[Bid] Sent: ${bidEvent.amount} (${bidEvent.driverId})`);

        // Random delay 1s - 2s (as per requirements)
        const randomDelay = Math.floor(Math.random() * 1000) + 1000;
        setTimeout(loopBidding, randomDelay);
    };

    // Start the flow immediately on connection
    sendNegotiation();

    ws.on('close', () => {
        console.log('Client disconnected');
    });
});