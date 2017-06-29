import RPi.GPIO as GPIO
import time

piezo = 29
fotocelda = 31

GPIO.setmode(GPIO.BOARD)

GPIO.setup(piezo,GPIO.OUT)
GPIO.setup(fotocelda,GPIO.IN)

current_state = 0

def luces(valor):
	try:
		while True:
			print GPIO.input(fotocelda)
			if GPIO.input(fotocelda) == 1 :
				time.sleep(0.1)
				GPIO.output(piezo,True)
				print("encendido")
				time.sleep(2)
			else:
				GPIO.output(piezo,False)
				print("apagado")

	except KeyboardInterrupt:
		pass
