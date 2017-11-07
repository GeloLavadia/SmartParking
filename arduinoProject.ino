int ultrasonicTriggerPin1 = 2, ultrasonicEchoPin1 = 3, ultrasonicTriggerPin2 = 8, ultrasonicEchoPin2 = 9;
boolean ultrasonic1 = true;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(ultrasonicTriggerPin1, OUTPUT);
  pinMode(ultrasonicEchoPin1, INPUT);
  pinMode(ultrasonicTriggerPin2, OUTPUT);
  pinMode(ultrasonicEchoPin2, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(ultrasonic1 ? ultrasonicTriggerPin1 : ultrasonicTriggerPin2, LOW);
  delayMicroseconds(2);
  digitalWrite(ultrasonic1 ? ultrasonicTriggerPin1 : ultrasonicTriggerPin2, HIGH);
  delayMicroseconds(10);
  digitalWrite(ultrasonic1 ? ultrasonicTriggerPin1 : ultrasonicTriggerPin2, LOW);

  long duration = pulseIn(ultrasonic1 ? ultrasonicEchoPin1 : ultrasonicEchoPin2, HIGH);

  if (Serial.available() > 0) {
    Serial.print(ultrasonic1 ? "1 " : "2 ");
    Serial.print(distanceInInches(duration));
    Serial.println('~');
  }
  ultrasonic1 = !ultrasonic1;
  delay(500);
}

long distanceInCentimeter(long duration) {
  return (duration/2) / 29.1;
}

long distanceInInches(long duration) {
  return (duration/2) / 73.914;
}

