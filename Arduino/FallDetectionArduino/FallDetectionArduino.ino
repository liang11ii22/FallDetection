
#include <Wire.h>
#include "I2Cdev.h"
#include "RTIMUSettings.h"
#include "RTIMU.h"
#include "RTFusionRTQF.h" 
#include "CalLib.h"
#include <EEPROM.h>
#include <SoftwareSerial.h>

RTIMU *imu;                                           // the IMU object
RTFusionRTQF fusion;                                  // the fusion object
RTIMUSettings settings;                               // the settings object

//  DISPLAY_INTERVAL sets the rate at which results are displayed

#define DISPLAY_INTERVAL  500                         // interval between pose displays

//  SERIAL_PORT_SPEED defines the speed to use for the debug serial port

#define  SERIAL_PORT_SPEED  9600

unsigned long lastDisplay;
unsigned long lastRate;
int sampleCount;

float acceler;
float acceFT = 2.2;
float gravity = 1.0;

float gyrosco;
float gyroFT = 180.0;

//int historyLength = 10;
//float motionHistory[2][10];
//int currntPoint = 0;
//int fallDuration = 5
//int judgePoint;
//int judgeWindow = 4;

SoftwareSerial mySerial(9,10);

void setup()
{
    int errcode;
  
    Serial.begin(SERIAL_PORT_SPEED);
    mySerial.begin(SERIAL_PORT_SPEED);
    Wire.begin();
    imu = RTIMU::createIMU(&settings);                        // create the imu object
  
    Serial.print("ArduinoIMU starting using device "); Serial.println(imu->IMUName());
    if ((errcode = imu->IMUInit()) < 0) {
        Serial.print("Failed to init IMU: "); Serial.println(errcode);
    }
  
    if (imu->getCalibrationValid())
        Serial.println("Using compass calibration");
    else
        Serial.println("No valid compass calibration data");

    lastDisplay = lastRate = millis();
    sampleCount = 0;

    // Slerp power controls the fusion and can be between 0 and 1
    // 0 means that only gyros are used, 1 means that only accels/compass are used
    // In-between gives the fusion mix.
    
    fusion.setSlerpPower(0.02);
    
    // use of sensors in the fusion algorithm can be controlled here
    // change any of these to false to disable that sensor
    
    fusion.setGyroEnable(true);
    fusion.setAccelEnable(true);
    fusion.setCompassEnable(true);
}

void loop()
{  
    unsigned long now = millis();
    unsigned long delta;
    int loopCount = 1;
  
    while (imu->IMURead()) {                                // get the latest data if ready yet
        // this flushes remaining data in case we are falling behind
        if (++loopCount >= 10)
            continue;
        fusion.newIMUData(imu->getGyro(), imu->getAccel(), imu->getCompass(), imu->getTimestamp());
        sampleCount++;
        if ((delta = now - lastRate) >= 1000) {
            Serial.print("Sample rate: "); Serial.print(sampleCount);
            if (imu->IMUGyroBiasValid())
                Serial.println(", gyro bias valid");
            else
                Serial.println(", calculating gyro bias");
        
            sampleCount = 0;
            lastRate = now;
        }
        
        acceler = sqrt( sq(imu->getAccel().x()) + sq(imu->getAccel().y()) + sq(imu->getAccel().z())) - gravity;
        gyrosco = sqrt( sq(imu->getGyro().x() * RTMATH_RAD_TO_DEGREE) + sq(imu->getGyro().y() * RTMATH_RAD_TO_DEGREE) + sq(imu->getGyro().z()) * RTMATH_RAD_TO_DEGREE);
        
        if ((acceler >= acceFT) && (gyrosco >= gyroFT)){
          mySerial.println("#111#");
          
          
        }
        
//        motionHistory[0][currntPoint] = acceler;
//        motionHistory[1][currntPoint] = gyrosco;
        
//        if ( acceler >= acceUFT ){
//          judgePoint = ( currntPoint - judgeWindow + historyLength) % historyLength;
//          for ( int i = 0; i ++ ; i < judgeWindow ){
//            if ( (motionHistory[0][judgePoint] > acceUFT) & (motionHistory[1][judgePoint] > gyroUFT)){
//              
//              mySerial.println("Alarm: 1");
//              break;
//            }
//            judgePoint = ( judgePoint - 1 + historyLength) % historyLength;
//          }
//        }
        
//        currntPoint = ( currntPoint + 1 ) % infoLength;
                
        if ((now - lastDisplay) >= DISPLAY_INTERVAL) {
            lastDisplay = now;
            
            mySerial.print("Acce:"); mySerial.print(acceler);  
            mySerial.print("  Gyro:"); mySerial.println(gyrosco);   
//            mySerial.print(">>GyroX :"); mySerial.println(imu->getGyro().x() * RTMATH_RAD_TO_DEGREE);
//            mySerial.print(">>GyroY :"); mySerial.println(imu->getGyro().y() * RTMATH_RAD_TO_DEGREE);
//            mySerial.print(">>GyroZ :"); mySerial.println(imu->getGyro().z() * RTMATH_RAD_TO_DEGREE);
                  
//            mySerial.print(">>AccelX:"); mySerial.println(imu->getAccel().x());
//            mySerial.print(">>AccelY:"); mySerial.println(imu->getAccel().y());
//            mySerial.print(">>AccelZ:"); mySerial.println(imu->getAccel().z());
//            
//            mySerial.print(">>Roll  :"); mySerial.println(fusion.getFusionPose().x() * RTMATH_RAD_TO_DEGREE);
//            mySerial.print(">>Pitch :"); mySerial.println(fusion.getFusionPose().y() * RTMATH_RAD_TO_DEGREE);
//            mySerial.print(">>Yaw   :"); mySerial.println(fusion.getFusionPose().z() * RTMATH_RAD_TO_DEGREE);
        }
    }
}

