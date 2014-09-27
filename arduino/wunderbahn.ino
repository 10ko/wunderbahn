#include <SoftwareSerial.h>
#include <WunderbarBridge.h>

// Stuff dealing with Charlieplexing. This is a clever scheme for controlling
// diodes (usually LEDs, but not necessarily) in a multiplexed manner, using
// a minimum number of pins. A pin can have one of 3 states: +5v, 0v, or high
// impedance. To switch a given LED on, you forward-bias it's pins, and set the
// remainder of the pins to high impedance. Generally only one LED can be
// forward biased, especially with the low current found on microcontroller IO
// pins. Some combinations are possible, but it is preferable to strobe the
// LEDs to give the appearance of being on at the same time.
struct CharliePin {
    byte high;
    byte low;
    byte Z;
    byte Z2;
};

class Charlieplex {
public:
    Charlieplex(byte* userPins, byte numberOfUserPins) {
        pins = userPins;
        numberOfPins = numberOfUserPins;
        clear();
    }

    void charlieWrite(CharliePin pin, bool state) {
        if (state) {
            set_high(pin.high);
            set_low(pin.low);
            set_Z(pin.Z);
            set_Z(pin.Z2);
        } else {
            pinMode(pin.high, INPUT);
        }
    }

    void clear() {
        for (byte i = 0; i < numberOfPins; i++) {
            pinMode(pins[i], INPUT);
        }
    }

private:
    void set_high(byte pin) {
        pinMode(pins[pin], OUTPUT);
        digitalWrite(pins[pin], HIGH);
    }

    void set_low(byte pin) {
        pinMode(pins[pin], OUTPUT);
        digitalWrite(pins[pin], LOW);
    }

    void set_Z(byte pin) {
        pinMode(pins[pin], INPUT);
        digitalWrite(pins[pin], LOW);
    }

    byte numberOfPins;
    byte* pins;
};

// Delay between LEDs lighting, in ms.
#define DELAY 100

// Debug UART pins for the Wunderbar bridge.
#define DEBUG_RX 10
#define DEBUG_TX 11

byte U2_pins[] = {2, 3, 4, 5};
byte U8_pins[] = {6, 7, 8, 9};
const byte NUMBER_OF_U2_PINS = sizeof(U2_pins) / sizeof(byte);
const byte NUMBER_OF_U8_PINS = sizeof(U8_pins) / sizeof(byte);

Charlieplex U2_charlieplex = Charlieplex(U2_pins, NUMBER_OF_U2_PINS);
Charlieplex U8_charlieplex = Charlieplex(U8_pins, NUMBER_OF_U8_PINS);

// Charlieplexed pin indices.
CharliePin U2_leds[] = {
    {1, 0, 2, 3},
    {0, 1, 2, 3},
    {3, 2, 0, 1},
    {2, 3, 0, 1},
    {2, 1, 0, 3},
    {1, 2, 0, 3},
    {2, 0, 1, 3},
    {0, 2, 1, 3},
    {3, 1, 0, 2},
    {1, 3, 0, 2},
    {3, 0, 1, 2},
    {0, 3, 1, 2}
};

const byte num_U2_leds = sizeof(U2_leds) / sizeof(CharliePin);

// Charlieplexed pin indices.
CharliePin U8_leds[] = {
    {1, 0, 2, 3},
    {0, 1, 2, 3},
    {3, 2, 0, 1},
    {2, 3, 0, 1},
    {2, 1, 0, 3},
    {1, 2, 0, 3},
    {2, 0, 1, 3},
    {0, 2, 1, 3},
    {3, 1, 0, 2},
    {1, 3, 0, 2},
    {3, 0, 1, 2},
    {0, 3, 1, 2}
};

const byte num_U8_leds = sizeof(U8_leds) / sizeof(CharliePin);

// The Wunderbar bridge device.
Bridge bridge = Bridge(DEBUG_RX, DEBUG_TX, 115200);

// The start and end LEDs for each line. 255 means don't highlight that line.
byte U2_ping = 255;
byte U2_pong = 255;
byte U8_ping = 255;
byte U8_pong = 255;

void setup() {
}

void serialEvent() {
    bridge.processSerial();
}

void loop() {
    U2_charlieplex.clear();
    U8_charlieplex.clear();

    if (bridge.newData) {
        bridge_payload_t payload = bridge.getData();
        U2_ping = payload.payload[0];
        U2_pong = payload.payload[1];
        U8_ping = payload.payload[2];
        U8_pong = payload.payload[3];
    }

    if (U2_ping != 255 && U2_pong != 255) {
        for (byte i = U2_ping; i < U2_pong; ++i) {
            U2_charlieplex.charlieWrite(U2_leds[i], HIGH);
            delay(DELAY);
        }
    }

    if (U8_ping != 255 && U8_pong != 255) {
        for (byte i = U8_ping; i < U8_pong; ++i) {
            U8_charlieplex.charlieWrite(U8_leds[i], HIGH);
            delay(DELAY);
        }
    }
}
