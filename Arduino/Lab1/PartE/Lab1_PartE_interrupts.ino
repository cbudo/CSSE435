// include the library code:
#include <LiquidCrystal.h>

#define FLAG_INTERRUPT_0 0x01
#define FLAG_INTERRUPT_1 0x02
#define FLAG_INTERRUPT_2 0x04

#define PIN_RIGHT_BUTTON 2
#define PIN_LEFT_BUTTON 3

volatile int mainEventFlags = 0;
int age = 50;
LiquidCrystal lcd(14, 15, 16, 17, 18, 19, 20);

void setup() {
  pinMode(PIN_LEFT_BUTTON, INPUT_PULLUP);
  pinMode(PIN_RIGHT_BUTTON, INPUT_PULLUP);
  attachInterrupt(0, int0_isr, FALLING);
  attachInterrupt(1, int1_isr, FALLING);
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.print("Your Mom is");
  lcd.setCursor(0,1);
  lcd.print(String(String(age) + " years old"));
}

void loop() {
    if (mainEventFlags & FLAG_INTERRUPT_0) {
      delay(20);
      mainEventFlags &= ~FLAG_INTERRUPT_0;
      if (!digitalRead(PIN_RIGHT_BUTTON)) {
        age++;
      }
    }
    if (mainEventFlags & FLAG_INTERRUPT_1) {
      delay(20);
      mainEventFlags &= ~FLAG_INTERRUPT_1;
      if (!digitalRead(PIN_LEFT_BUTTON)) {
        age--;
      }
    }  lcd.setCursor(0,1);
  lcd.print(String(String(age) + " years old"));
}
void int0_isr() {
  mainEventFlags |= FLAG_INTERRUPT_0;
}
void int1_isr() {
  mainEventFlags |= FLAG_INTERRUPT_1;
}
