#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

// the numbers of the LED pins
const int pinsRGB[3] = {26, 12, 17};

// setting PWM properties
const int freq = 5000;

const int ledChannelR = 0;
const int ledChannelG = 1;
const int ledChannelb = 2;

const int resolution = 8;

char input[9], rgb[3] = {0};
 
void setup(){


  for (uint8_t i = 0; i < 3; i++) {
    ledcAttachPin(pinsRGB[i], i); //connects GPIO to led driver interface
    ledcSetup(i, freq, resolution); //configures PWM stats.
    ledcWrite(i, rgb[i]); //turns off to start
  }
  
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");
}
 
void loop(){

  if (SerialBT.available()) {
    for (byte i = 0; i < 11; i++) input[i] = SerialBT.read();
    sscanf(input, "%3u%3u%3u", &rgb[0], &rgb[1], &rgb[2]);
    for (byte i = 0; i < 3; i++) ledcWrite(i, rgb[i]);
  }
  delay(50);
}
