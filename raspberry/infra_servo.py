import RPi.GPIO as GPIO
import time
GPIO.cleanup()
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
print "Comenzar con prueba"
PIN_SENSOR_A=13

PIN_SERVO_A=17

GPIO.setup(PIN_SERVO_A,GPIO.OUT)
GPIO.setup(PIN_SENSOR_A,GPIO.IN, pull_up_down=GPIO.PUD_UP)

servo_a = GPIO.PWM(PIN_SERVO_A, 50)

servo_a.start(0.5)

def servo_infrarrojo (value):
	print GPIO.input("Infrarrojo ", PIN_SENSOR_A)

	if not value :
		if GPIO.input(PIN_SENSOR_A) == 1:
			print "Se detecta auto"
			servo_a.ChangeDutyCycle(3.7)
			time.sleep(0.5)
		else:
			print "no se detecta nada"
			servo_a.ChangeDutyCycle(8.2)
			time.sleep(0.5)

