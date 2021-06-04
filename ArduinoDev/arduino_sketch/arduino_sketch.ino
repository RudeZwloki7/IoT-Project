#include "BluetoothSerial.h"
//#include <TroykaLight.h>
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;

// the numbers of the LED pins
const int pinsRGB[3] = {26, 12, 17};

//Light sensor pin
const int light_sensor = 4;
int threshold = 2000;

// setting PWM properties
const int freq = 5000;

const int ledChannelR = 0;
const int ledChannelG = 1;
const int ledChannelb = 2;

const int resolution = 8;

char input[32], rgb[3] = {0};


class User{
  bool is_automatic = false;
  int last_rgb[3] = {255,0,0};
public:
  bool turn_on = false;

  void read(){
    for (byte i = 0; i < 32; i++){
      input[i] = SerialBT.read();
      Serial.print(input[i]);
    }
    Serial.println("");
    sscanf(input, "%u %u %u", &rgb[0], &rgb[1], &rgb[2]);
    String temp="";
    sscanf(input, "Automatic: %d", &is_automatic);
//    is_automatic = bool(temp);
    Serial.println(is_automatic);
    
  }

  void light(){
    if (is_automatic){    
      if(rgb[0] !=0 || rgb[1]!=0 || rgb[2]!=0){
         for (int i = 0; i < 3; i++){
            last_rgb[i] = rgb[i];
          }
        }
      if (turn_on){
          for (byte i = 0; i < 3; i++){
            ledcWrite(i, last_rgb[i]);
          }
      }
      else{
         for (byte i = 0; i < 3; i++){
            ledcWrite(i, 0);
          }
      }
    }
    else{
      for (byte i = 0; i < 3; i++){
          ledcWrite(i, rgb[i]);
      }
    }
  }
  
  
};


User user;
 
void setup(){


  for (uint8_t i = 0; i < 3; i++) {
    ledcAttachPin(pinsRGB[i], i); //connects GPIO to led driver interface
    ledcSetup(i, freq, resolution); //configures PWM stats.
    ledcWrite(i, rgb[i]); //turns off to start
  }
  
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.begin(115200);
  Serial.println("The device started, now you can pair it with bluetooth!");
}
 
void loop(){

  int lightness = analogRead(light_sensor);
//  Serial.println("Lightness is: " + String(lightness));
//  SerialBT.write(SerialBT.read());
  if (lightness > threshold) user.turn_on = true;
  else user.turn_on = false;
  user.light();
  if (SerialBT.available()) {
    user.read();
  }
  delay(50);
}
