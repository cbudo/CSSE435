#include "Arduino.h"
#include "EasyGolfBallStand.h"

EasyGolfBallStand::EasyGolfBallStand() {
    _init();
}

void EasyGolfBallStand::_init() {
  pinMode(PIN_LED_1_UNDER, OUTPUT);
  pinMode(PIN_LED_1_FRONT, OUTPUT);
  pinMode(PIN_LED_2_UNDER, OUTPUT);
  pinMode(PIN_LED_2_FRONT, OUTPUT);
  pinMode(PIN_LED_3_UNDER, OUTPUT);
  pinMode(PIN_LED_3_FRONT, OUTPUT);
  pinMode(PIN_RED, OUTPUT);
  pinMode(PIN_GREEN, OUTPUT);
  pinMode(PIN_BLUE, OUTPUT);
  pinMode(PIN_GOLF_BALL_STAND_SWITCH, INPUT_PULLUP);
  digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
  digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
  digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);


}

void EasyGolfBallStand::setLedState(int ledColor, int location, int underOrFront){
  // Start by clearing off all LEDs and colors.
  digitalWrite(PIN_RED, COLOR_TRANSISTOR_OFF);
  digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_OFF);
  digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_OFF);
  digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_OFF);

  // Decide which of the six LEDs to turn on.
  if ((location & LOCATION_1) && (underOrFront & LED_UNDER)) {
      digitalWrite(PIN_LED_1_UNDER, LED_TRANSISTOR_ON);
  }
  if ((location & LOCATION_1) && (underOrFront & LED_FRONT)) {
      digitalWrite(PIN_LED_1_FRONT, LED_TRANSISTOR_ON);
  }
  if ((location & LOCATION_2) && (underOrFront & LED_UNDER)) {
      digitalWrite(PIN_LED_2_UNDER, LED_TRANSISTOR_ON);
  }
  if ((location & LOCATION_2) && (underOrFront & LED_FRONT)) {
      digitalWrite(PIN_LED_2_FRONT, LED_TRANSISTOR_ON);
  }
  if ((location & LOCATION_3) && (underOrFront & LED_UNDER)) {
      digitalWrite(PIN_LED_3_UNDER, LED_TRANSISTOR_ON);
  }
  if ((location & LOCATION_3) && (underOrFront & LED_FRONT)) {
      digitalWrite(PIN_LED_3_FRONT, LED_TRANSISTOR_ON);
  }

  // Set the color.
  if (ledColor & LED_BLUE) {
      digitalWrite(PIN_BLUE, COLOR_TRANSISTOR_ON);
  }
  if (ledColor & LED_GREEN) {
      digitalWrite(PIN_GREEN, COLOR_TRANSISTOR_ON);
  }
  if (ledColor & LED_RED) {
      digitalWrite(PIN_RED, COLOR_TRANSISTOR_ON);
  }
}

int EasyGolfBallStand::getAnalogReading(int location) {

  int photoReading = -1;
  switch (location) {
  case LOCATION_1:
      photoReading = analogRead(PIN_PHOTO_1);
      break;
  case LOCATION_2:
      photoReading = analogRead(PIN_PHOTO_2);
      break;
  case LOCATION_3:
      photoReading = analogRead(PIN_PHOTO_3) ;
      break;
  case LOCATION_EXTERNAL:
      photoReading = analogRead(PIN_PHOTO_EXTERNAL);
      break;
  }
  return photoReading;
}

String EasyGolfBallStand::determineBallColor(int location) {
  external_reading = analogRead(PIN_PHOTO_EXTERNAL);

  //int returnBallType = BALL_NONE;
  setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);
  delay(GBS_TIME_DELAY);
  int offReading = getAnalogReading(location);

  setLedState(LED_RED, location, LED_UNDER_AND_FRONT);
  delay(GBS_TIME_DELAY);
  int redReading = getAnalogReading(location);

  setLedState(LED_GREEN, location, LED_UNDER_AND_FRONT);
  delay(GBS_TIME_DELAY);
  int greenReading = getAnalogReading(location);

  setLedState(LED_BLUE, location, LED_UNDER_AND_FRONT);
  delay(GBS_TIME_DELAY);
  int blueReading = getAnalogReading(location);

  setLedState(LED_WHITE, location, LED_UNDER_AND_FRONT);
  delay(GBS_TIME_DELAY);
  int whiteReading = getAnalogReading(location);

  setLedState(LED_OFF, location, LED_UNDER_AND_FRONT);
  int locN = -1;
  switch (location) {
    case LOCATION_1:
        locN = 1;
        break;
    case LOCATION_2:
        locN = 2;
        break;
    case LOCATION_3:
        locN = 3;
        break;
  }

  String retString = String('['+String(locN)+','+String(offReading)+','+String(redReading)+','+String(greenReading)+','+String(blueReading)+','+String(whiteReading)+','+String(external_reading)+']');
  return retString;

}
