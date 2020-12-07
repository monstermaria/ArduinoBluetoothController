/*
 * Bluetooth project for controlling a frequency converter
 */

#include <SoftwareSerial.h>

// I/O
#define SOFTWARE_SERIAL_RX 2
#define SOFTWARE_SERIAL_TX 3
#define DIGITAL_IN_RUNNING 4
#define DIGITAL_IN_ALARM 5
#define DIGITAL_OUT_START 8
#define ANALOG_OUT_SETPOINT 9
#define ANALOG_IN_PRESSURE 1
#define ANALOG_IN_CURRENT 2


// data handling
SoftwareSerial BluetoothSerial(SOFTWARE_SERIAL_RX, SOFTWARE_SERIAL_TX);
char c;
char data[64];
String received = "";
int status[6];
int length;

int setpoint = 0;
int start = LOW;


void setup() {
  BluetoothSerial.begin(9600);
  pinMode(DIGITAL_IN_RUNNING, INPUT);
  pinMode(DIGITAL_IN_ALARM, INPUT);
  pinMode(DIGITAL_OUT_START, OUTPUT);
  pinMode(ANALOG_OUT_SETPOINT, OUTPUT);
  setOutputs();
}

void loop() {  
  // data handling
  if (BluetoothSerial.available()) {
    c = BluetoothSerial.read();
    received += c;
    
  } else {
    length = received.length();
    if (length > 0) {
      // handle input
      if (received.charAt(0) == '1' && length == 1) {
        BluetoothSerial.write(1);
      } else if (received.charAt(0) == '2' && length == 3) {
        start = received.charAt(1);
        setpoint = received.charAt(2);
        setOutputs();
        BluetoothSerial.write(2);
      }
      
      setStatus();

      for (int i = 0; i < 6; i++) {
        BluetoothSerial.write(status[i]);        
      }
      received = "";
    }
  }
}

void setOutputs() {
  digitalWrite(DIGITAL_OUT_START, start);
  analogWrite(ANALOG_OUT_SETPOINT, setpoint);  
}

void setStatus() {
  status[0] = digitalRead(DIGITAL_IN_RUNNING);
  status[1] = digitalRead(DIGITAL_IN_ALARM);
  status[2] = digitalRead(DIGITAL_OUT_START);
  status[3] = setpoint;
  status[4] = analogRead(ANALOG_IN_PRESSURE);
  status[5] = analogRead(ANALOG_IN_CURRENT);
}
