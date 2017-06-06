/*
Arduino Due + HC-06 (Bluetooth) -echo bluetooth data

Serial (Tx/Rx) communicate to PC via USB
Serial3 (Tx3/Rx3) connect to HC-06
HC-06 Rx - Due Tx3
HC-06 Tx - Due Rx3
HC-06 GND - Due GND
HC-06 VCC - Due 3.3V

*/
#define HC06 Serial3

void setup()
{
  delay(1000);
  Serial.begin(9600);
  HC06.begin(9600);
  
  Serial.write("\nTest Start\n");
}

void loop()
{
  while(HC06.available())
  {
    char data = HC06.read();
    Serial.write(data);
    HC06.write(data);
  }
}
