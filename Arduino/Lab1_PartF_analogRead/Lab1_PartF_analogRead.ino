// include the library code:
#include <LiquidCrystal.h>

#define PIN_CONTRAST_ANALOG 8
#define LINE_1 0
#define LINE_2 1

int analogReading = 0;
LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);

void setup() {

  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);

}

void loop() {
  analogReading = analogRead(PIN_CONTRAST_ANALOG);
  lcd.setCursor(0, LINE_1);
  lcd.print("Reading = ");
  lcd.print(analogReading);
  lcd.print("  ");
  lcd.setCursor(0, LINE_2);
  lcd.print("Voltage = ");
  lcd.print( (float) analogReading / 1023.0 * 5.0);
  lcd.print("  ");
}

