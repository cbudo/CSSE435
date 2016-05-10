#include "Arduino.h"
#include "GolfBallStand.h"

GolfBallStand::GolfBallStand() {
    _init();
}

void GolfBallStand::_init() {
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

void GolfBallStand::setLedState(int ledColor, int location, int underOrFront){
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

void GolfBallStand::calibrate(){
  external_reading = analogRead(PIN_PHOTO_EXTERNAL);
  empty_location_1 = analogRead(PIN_PHOTO_1);;
  empty_location_2 = analogRead(PIN_PHOTO_2);
  empty_location_3 = analogRead(PIN_PHOTO_3);
  Serial.print("  L1 reading   = ");
  Serial.println(empty_location_1);
  Serial.print("  L2 reading   = ");
  Serial.println(empty_location_2);
  Serial.print("  L3 reading = ");
  Serial.println(empty_location_3);
  Serial.print("  LX reading = ");
  Serial.println(external_reading);
}
int GolfBallStand::getAnalogReading(int location) {

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

int GolfBallStand::determineBallColor(int location) {
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
  //

  // Serial.println();
  // Serial.print("Readings for location ");
  // Serial.println(location == LOCATION_3 ? 3 : location);
  // Serial.print("  LED off reading   = ");
  // Serial.println(offReading);
  // Serial.print("  LED red reading   = ");
  // Serial.println(redReading);
  // Serial.print("  LED green reading = ");
  // Serial.println(greenReading);
  // Serial.print("  LED blue reading  = ");
  // Serial.println(blueReading);
  // Serial.print("  LED white reading = ");
  // Serial.println(whiteReading);


  // ##################################################
  // Nearest-neighbor fall-through logic.
  // Uses "metaScores" to see which colors are nearest.
  // Note: Sensor 3 is a finicky @$$
  // ##################################################

  int lCal = 0;  // used only for determining if slot is empty.
  switch (location){
    case LOCATION_1:
        lCal = empty_location_1;
        break;
    case LOCATION_2:
        lCal = empty_location_2;
        break;
    case LOCATION_3:
        lCal = empty_location_3;
        break;
    case LOCATION_EXTERNAL:
        lCal = external_reading;
        break;

  }
  // MetaScore Math. This filters out colors, one by one, in order NONE, WHITE, BLACK, RED, YELLOW, GREEN. Blue is returned if nothing matches.

  float metaOne = (offReading-lCal) / (float)external_reading; // Closer to 0, the more likely it is nothing is there
  int metaTwo = whiteReading + blueReading;    // Smaller the number, the more white it is
  int metaThree = redReading + greenReading + metaTwo; // Bigger the number, the blacker it is ;)
  int metaFour = redReading - greenReading; // Smaller the number, the more red it is
  int metaFive = redReading + greenReading - blueReading; // Smaller the number, the more yellow it is
  int metaSix = greenReading - blueReading; // Smaller the number, the more green it is.
  //WARNING: MetaSix is consistent on a per-sensor basis. Thus, there are three separate thresholds.


  if (metaOne < META_1_MAX_THR){
      // Serial.print("Meta1= ");
      // Serial.println(metaOne);
     return BALL_NONE;
   }
  if (metaTwo < META_2_MAX_THR){
    // Serial.print("Meta2= ");
    // Serial.println(metaOne);
    return BALL_WHITE;
  }
  if (metaThree > META_3_MIN_THR){
    // Serial.print("Meta3= ");
    // Serial.println(metaThree);
    if ((location == LOCATION_2) || (metaThree > 2700)){
		if (metaFour < 80){
			return BALL_BLACK;
		}
    }
	if ((location == LOCATION_3) && (metaFour < 0)){
		return BALL_BLACK;
	}
  }
  if (metaFour < META_4_MAX_THR){
    // Serial.print("Meta4= ");
    // Serial.println(metaFour);
    if (location != LOCATION_3 ){
      return BALL_RED;
    } else {
      if (metaFour< META_4_LOC_3_THR){
        return BALL_RED;
      }
    }
  }
  if (metaFive < META_5_MAX_THR){
    // Serial.print("Meta5= ");
    // Serial.println(metaFive);
    return BALL_YELLOW;
  }

  if (metaSix < META_6_LOC_3_THR && location == LOCATION_3){ // for peculiar reasons, m6 is inverted for location 3
    // Serial.print("Meta6L3= ");
    // Serial.println(metaSix);
    return BALL_GREEN;
  }

  if (metaSix < META_6_LOC_2_THR && location == LOCATION_2){
    // Serial.print("Meta6L2= ");
    // Serial.println(metaSix);
    return BALL_GREEN;
  }
  if (metaSix < META_6_LOC_1_THR && location == LOCATION_1){
    // Serial.print("Meta6L1= ");
    // Serial.println(metaSix);
    return BALL_GREEN;
  }
  // //If you reached here, it is blue.
   return BALL_BLUE;

}
