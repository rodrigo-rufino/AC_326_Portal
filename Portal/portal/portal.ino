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

int p0 = 49;
int p1 = 51;
int p2 = 53;

void setup()
{
  delay(1000);
  Serial.begin(9600);
  HC06.begin(9600);
  pinMode(p0, OUTPUT);
  pinMode(p1, OUTPUT);
  pinMode(p2, OUTPUT);

  Serial.write("\nTest Start\n");
}

void loop()
{
  while(HC06.available())
  {
    char data = HC06.read();
    Serial.write(data);
    HC06.write(data);
    if(data == '0'){
      Serial.write("Porta 0");
      digitalWrite(p0, HIGH);
      delay(1000);
      digitalWrite(p0, LOW);
    }

    if(data == '1'){
      Serial.write("Porta 1");
      digitalWrite(p1, HIGH);
      delay(1000);
      digitalWrite(p1, LOW);
    }

    if(data == '2'){
      Serial.write("Porta 2");
      digitalWrite(p2, HIGH);
      delay(1000);
      digitalWrite(p2, LOW);
    }
  }
}
