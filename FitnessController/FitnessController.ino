#include <SoftwareSerial.h>
#include <Wire.h>
const int rx = 6;
const int tx = 5;
const int MPU = 0x68;
int16_t AcX, AcY, AcZ;
int16_t AcXi, AcYi, AcZi;
SoftwareSerial blue(rx, tx);
static String posrepeticion;
boolean start;
const int echosonic = 9;
const int trigsonic = 8;
long timesonic; //timepo que demora en llegar el eco
long distanciasonic; //distancia en centimetros

void setup() {
  start = false;
  pinMode(13, OUTPUT);
  Serial.begin(9600);
  inibluetooth();
  inigiroscopio();
  iniultrasound();
  blue.print("Conectado+");
}

void inibluetooth() {
  blue.begin(9600);
}
void inigiroscopio() {
  Wire.begin();
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);  // PWR_MGMT_1 register
  Wire.write(0);     // set to zero (wakes up the MPU-6050)
  Wire.endTransmission(true);
}

void iniultrasound() {
  pinMode(trigsonic, OUTPUT); //pin como salida
  pinMode(echosonic, INPUT);  //pin como entrada
  digitalWrite(trigsonic, LOW);//Inicializamos el pin con 0
}

void loop() {
  String us = Ultrasound();
  Serial.println(us);
  if (distanciasonic <= 19) {
    blue.println("1+");
    Serial.println("Repeticion completa");
    Serial.println("envie el dato");
    Serial.println(us);
    delay(500);
  } else {
    blue.println("0+");
    Serial.println("Esperando repeticion");
    Serial.println("Esperando la repeticion");
    delay(500);
  }
  delay(800);

}

void readBluetooth() {
  if (blue.available()) {
    char n = blue.read();
    switch (n) {
      case '1':
        start = true;
        Giroscopio();
        AcXi = AcX;
        AcYi = AcY;
        AcZi = AcZ;
        Serial.println(mostrar(2));
        break;
      case '2':
        start = false;
        break;
      default:
        return;
        break;
    }
  }
}

void Giroscopio() {
  Wire.beginTransmission(MPU);
  Wire.write(0x3B);  // starting with register 0x3B (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 14, true); // request a total of 14 registers
  AcX = Wire.read() << 8 | Wire.read(); // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L)
  AcY = Wire.read() << 8 | Wire.read(); // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L)
  AcZ = Wire.read() << 8 | Wire.read(); // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L)
  //Tmp=Wire.read()<<8|Wire.read();  // 0x41 (TEMP_OUT_H) & 0x42 (TEMP_OUT_L)
  //GyX=Wire.read()<<8|Wire.read();  // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L)
  //GyY=Wire.read()<<8|Wire.read();  // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L)
  //GyZ=Wire.read()<<8|Wire.read();  // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
}

String Ultrasound() {
  digitalWrite(trigsonic, HIGH);
  delayMicroseconds(10);          //Enviamos un pulso de 10us
  digitalWrite(trigsonic, LOW);
  timesonic = pulseIn(echosonic, HIGH); //obtenemos el ancho del pulso
  distanciasonic = timesonic / 59;           //escalamos el tiempo a una distancia en cm
  String rultra = String(distanciasonic);
  return rultra;
}

boolean JuezxD() {
  boolean valida = true;
  /*int j=AcX-AcXi;
    if(j>60){
    valida=true;
    }*/
  return valida;
}

String mostrar(int i) {
  String x; String y; String z;
  String data;
  switch (i) {
    case 1:
      x = String(AcX);
      y = String(AcY);
      z = String(AcZ);
      data = x + "| " + y + "| " + z;
      break;
    case 2:
      x = String(AcXi);
      y = String(AcYi);
      z = String(AcZi);
      data = x + "| " + y + "| " + z;
      break;
  }
  return data;
}

