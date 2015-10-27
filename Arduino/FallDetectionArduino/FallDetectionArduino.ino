
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
float acceFT = 2.3;
float gravity = 1.0;

float gyrosco;
float gyroFT = 200.0;

float whatPose = 1.0;

boolean alarmFlag = false;

SoftwareSerial mySerial(9,10);

void setup()
{
    int errcode;
  
    Serial.begin(SERIAL_PORT_SPEED);  //print to the PC
    mySerial.begin(SERIAL_PORT_SPEED); //print to the bluetooth
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
        //gyrosco = sqrt( sq(imu->getGyro().x() * RTMATH_RAD_TO_DEGREE) + sq(imu->getGyro().y() * RTMATH_RAD_TO_DEGREE) + sq(imu->getGyro().z()) * RTMATH_RAD_TO_DEGREE);
        gyrosco = sqrt( sq(imu->getGyro().x()) + sq(imu->getGyro().y()) + sq(imu->getGyro().z())) * RTMATH_RAD_TO_DEGREE;
        
        if ((acceler >= acceFT) && (gyrosco >= gyroFT)){
          alarmFlag = true;
        }
                
        if ((now - lastDisplay) >= DISPLAY_INTERVAL) {
            lastDisplay = now;
            
            mySerial.println("Fall Detcting...");
            
            if (alarmFlag == true){
              delay(2000);
              
              for (int j = 0; j<50; j ++){
                whatPose = whatPose + fusion.getFusionPose().y() * RTMATH_RAD_TO_DEGREE;
                Serial.print("Pose Value: ");
                Serial.println(whatPose);
              }
              whatPose = whatPose / 50;
              Serial.print("Pose Value Final: ");
              Serial.println(whatPose);
              
              if ( -50 < whatPose && whatPose< 60 ){
                mySerial.println("@"); //posture after fall: up
                Serial.println("@");
              }
              else if ( -90 <= whatPose && whatPose <= -50 ){
                mySerial.println("#"); //lie on back
                Serial.println("#");
              }
              else if ( 60 <= whatPose && whatPose <= 90 ){         
               mySerial.println("$");//lie on face
               Serial.println("$");
             }
             mySerial.println("");
             whatPose = 0.0;
             alarmFlag = false;
             delay(1000*10);
            }
//            Serial.print(">>Acce:"); Serial.println(acceler);  
//            Serial.print(">>Gyro:"); Serial.println(gyrosco);   
//            Serial.print(">>GyroX :"); Serial.println(imu->getGyro().x() * RTMATH_RAD_TO_DEGREE);
//            Serial.print(">>GyroY :"); Serial.println(imu->getGyro().y() * RTMATH_RAD_TO_DEGREE);
//            Serial.print(">>GyroZ :"); Serial.println(imu->getGyro().z() * RTMATH_RAD_TO_DEGREE);
//            Serial.print(">>GyroX :"); Serial.println(imu->getGyro().x());
//            Serial.print(">>GyroY :"); Serial.println(imu->getGyro().y());
//            Serial.print(">>GyroZ :"); Serial.println(imu->getGyro().z());      
//            mySerial.print(">>AccelX:"); mySerial.println(imu->getAccel().x());
//            mySerial.print(">>AccelY:"); mySerial.println(imu->getAccel().y());
//            mySerial.print(">>AccelZ:"); mySerial.println(imu->getAccel().z());
//            
//            mySerial.print(">>Roll  :"); mySerial.println(fusion.getFusionPose().x() * RTMATH_RAD_TO_DEGREE);
//            Serial.print(">>Pitch :"); Serial.println(fusion.getFusionPose().y() * RTMATH_RAD_TO_DEGREE);
//            mySerial.print(">>Yaw   :"); mySerial.println(fusion.getFusionPose().z() * RTMATH_RAD_TO_DEGREE);
//            Serial.println("");
        }
    }
}

