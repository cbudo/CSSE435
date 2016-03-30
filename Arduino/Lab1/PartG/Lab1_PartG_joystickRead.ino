// include the library code:
#include <LiquidCrystal.h>

#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69
#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3
#define PIN_SELECT_BUTTON 21
#define PIN_CONTRAST_ANALOG 8
#define PIN_HORZ_ANALOG 0
#define PIN_VERT_ANALOG 1

#define LINE_1 0
#define LINE_2 1

int joyY = 0;
int joyX = 0;
LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);

void setup() {

  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_SELECT_BUTTON, INPUT_PULLUP);

  pinMode(PIN_LED_1, OUTPUT);
  pinMode(PIN_LED_2, OUTPUT);
  pinMode(PIN_LED_3, OUTPUT);
  pinMode(PIN_LED_4, OUTPUT);
  pinMode(PIN_LED_5, OUTPUT);
  pinMode(PIN_LED_6, OUTPUT);
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);

}

void loop() {
  joyY = analogRead(PIN_VERT_ANALOG);
  joyX = analogRead(PIN_HORZ_ANALOG);
  lcd.setCursor(0, LINE_1);
  lcd.print("Horz = ");
  lcd.print(joyX);
  lcd.print("  ");
  lcd.setCursor(0, LINE_2);
  lcd.print("Vert = ");
  lcd.print(joyY);
  lcd.print("  ");

  if( !digitalRead(PIN_LEFT_BUTTON) ){
    digitalWrite(PIN_LED_4, HIGH);    

    digitalWrite(PIN_LED_2, LOW);
    digitalWrite(PIN_LED_6, LOW); 
  }
  if( !digitalRead(PIN_RIGHT_BUTTON) ){
    digitalWrite(PIN_LED_6, HIGH);    

    digitalWrite(PIN_LED_2, LOW);
    digitalWrite(PIN_LED_4, LOW);
  }
  if( !digitalRead(PIN_SELECT_BUTTON) ){
    digitalWrite(PIN_LED_2, HIGH);   

    digitalWrite(PIN_LED_4, LOW);
    digitalWrite(PIN_LED_6, LOW); 
  }  
}

