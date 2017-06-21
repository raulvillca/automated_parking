import RPi.GPIO as GPIO
import time
GPIO.cleanup()
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
print "Comenzar con prueba"
PIN_SENSOR_A=33

PIN_SERVO_A=11

GPIO.setup(PIN_SERVO_A,GPIO.OUT)
GPIO.setup(PIN_SENSOR_A,GPIO.IN, pull_up_down=GPIO.PUD_UP)

servo_a = GPIO.PWM(PIN_SERVO_A, 50)

servo_a.start(0.5)

while True:
	print GPIO.input(PIN_SENSOR_A)
	
	if GPIO.input(PIN_SENSOR_A) == 1:
		servo_a.ChangeDutyCycle(3.7)
		time.sleep(0.5)
	else:
		servo_a.ChangeDutyCycle(8.2)
		time.sleep(0.5)
