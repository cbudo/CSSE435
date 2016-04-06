/*
 Name:		PartH.ino
 Created:	3/30/2016 1:45:21 PM
 Author:	drong, budo
*/
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

#define DEAD_ZONE 100

int joyY = 0;
int joyX = 0;
int restY;
int restX;
int degs[] = { 90, 90, 90, 90, 90, 90 };

int incrAmt = 0;

int selection = 5;
int lastSelection = 0;

int buttonLatch = 0;

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

	restY = analogRead(PIN_VERT_ANALOG);
	restX = analogRead(PIN_HORZ_ANALOG);
	// set up the LCD's number of columns and rows:
	lcd.begin(16, 2);

}

void loop() {
	lcd.setCursor(0, 0);
	lcd.print(String(degs[0])+"  ");
	lcd.setCursor(5, 0);
	lcd.print(String(degs[1])+"  ");
	lcd.setCursor(10, 0);
	lcd.print(String(degs[2])+"  ");

	lcd.setCursor(0, 1);
	lcd.print(String(degs[3])+"  ");
	lcd.setCursor(5, 1);
	lcd.print(String(degs[4])+"  ");
	lcd.setCursor(10, 1);
	lcd.print(String(degs[5])+"  ");

	joyY = analogRead(PIN_VERT_ANALOG);
	joyX = analogRead(PIN_HORZ_ANALOG);

	if (!digitalRead(PIN_LEFT_BUTTON) && buttonLatch == 1) {
		buttonLatch = 0;
		lastSelection = selection;
		selection--;
		if (selection < 0) {
			selection = 5;
		}
	}
	else if (!digitalRead(PIN_RIGHT_BUTTON) && buttonLatch == 1) {
		buttonLatch = 0;
		lastSelection = selection;
		selection++;
		if (selection > 5) {
			selection = 0;
		}

	}
	else {
		buttonLatch = 1;
	}
	if (!digitalRead(PIN_SELECT_BUTTON)) {
		degs[selection] = 90;
	}

	incrementValues();
}
void incrementValues() {
	if (abs(joyX - restX) > DEAD_ZONE && abs(joyX - restX) > abs(joyY - restY)) {
		// X dominant. increment by +-1
		if ((joyX - restX) > 0) {
			incrAmt = -1;
		}
		else {
			incrAmt = 1;
		}
	}
	else if (abs(joyY - restY) > DEAD_ZONE && abs(joyY - restY) > abs(joyX - restX)) {
		// Y dominant. increment by +-4
		if ((joyY - restY) > 0) {
			incrAmt = 4;
		}
		else {
			incrAmt = -4;
		}

	}
	else {

		incrAmt = 0;
	}
	degs[selection] += incrAmt;
	if (degs[selection] > 180) {
		degs[selection] = 180;
	}
	else if (degs[selection] < 1) {
		degs[selection] = 0;
	}
	digitalWrite(64 + selection, HIGH);
	digitalWrite(64 + lastSelection, LOW);	
	delay(100);
}
