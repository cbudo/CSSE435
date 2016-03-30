#define PIN_LED_1 64
#define PIN_LED_2 65
#define PIN_LED_3 66
#define PIN_LED_4 67
#define PIN_LED_5 68
#define PIN_LED_6 69

#define DELAY_TIME 75
int lastI = 69;
void setup() {
  // put your setup code here, to run once:
  pinMode(PIN_LED_1, OUTPUT);
  pinMode(PIN_LED_2, OUTPUT);
  pinMode(PIN_LED_3, OUTPUT);
  pinMode(PIN_LED_4, OUTPUT);
  pinMode(PIN_LED_5, OUTPUT);
  pinMode(PIN_LED_6, OUTPUT);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  for (int i = 64; i < 70; i++){
    digitalWrite(i, HIGH);
    digitalWrite(lastI, LOW);
    lastI = i;
    delay(DELAY_TIME);
  }
    for (int i = 69; i > 63; i--){
    digitalWrite(i, HIGH);
    digitalWrite(lastI, LOW);
    lastI = i;
    delay(DELAY_TIME);
  }

}
