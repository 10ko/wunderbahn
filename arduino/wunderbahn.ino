struct CharliePin {
    byte high;
    byte low;
    byte Z;
    byte Z2;
};

typedef CharliePin charliePin;

class Charlieplex {
#define INDEX 1
#define NUMBER_OF_STATUSES ((_numberOfPins*_numberOfPins)-_numberOfPins)

public:
    Charlieplex(byte* userPins, byte numberOfUserPins) {
        pins = userPins;
        numberOfPins = numberOfUserPins;
        clear();
    }

    void charlieWrite(charliePin pin, bool state) {
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
        for (byte i=0; i<numberOfPins; i++){
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


#define DELAY 30
//define pins in the order you want to adress them
byte pins[] = {2, 3, 4, 5};
const byte NUMBER_OF_PINS = sizeof(pins) / sizeof(byte);

Charlieplex charlieplex = Charlieplex(pins, NUMBER_OF_PINS);

charliePin leds[] = {
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

const int num_leds = sizeof(leds) / sizeof(charliePin);

void setup() {
}

void loop() {
  charlieplex.clear();

  for (int i = -(num_leds - 1); i < num_leds; ++i) {
    charlieplex.charlieWrite(leds[abs(i)], HIGH);
    delay(DELAY);
  }
}
