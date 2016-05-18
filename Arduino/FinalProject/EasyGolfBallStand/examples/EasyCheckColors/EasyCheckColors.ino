#include <EasyGolfBallStand.h>

String ballColor_1, ballColor_2, ballColor_3;
EasyGolfBallStand stand;

void setup(){
  Serial.begin(9600);
  delay(25);
  Serial.println("ready");
}

void loop() {
  while(digitalRead(PIN_GOLF_BALL_STAND_SWITCH)) {
    // Do nothing until the switch is pressed.
    }
    // Optional external reading (just as a reference).
  int externalPhotoCellReading = stand.getAnalogReading(LOCATION_EXTERNAL);
  Serial.print("External photo cell reading = ");
  Serial.println(externalPhotoCellReading);
  Serial.print("---------------------------------\n");
  ballColor_1 = stand.determineBallColor(LOCATION_1);
  Serial.print("  Location 1 ball   = ");
    Serial.println(ballColor_1);
  delay(1000);
  ballColor_2 = stand.determineBallColor(LOCATION_2);
  Serial.print("  Location 2 ball   = ");
    Serial.println(ballColor_2);
  delay(1000);
  ballColor_3 = stand.determineBallColor(LOCATION_3);
  Serial.print("  Location 3 ball   = ");
  Serial.println(ballColor_3);
  delay(1000);
  stand.setLedState(LED_GREEN, LOCATION_3, LED_FRONT);
  Serial.print("---------------------------------\n\n");



}
