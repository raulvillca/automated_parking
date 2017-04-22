#!/usr/bin/python
import RPi.GPIO as IO

class UltrasonicSensor:

    def __init__(self, pinIn, pinOut):
        #Hay que configurar ambos pines del HC-SR04
        IO.setup(pinOut, IO.OUT)
        IO.setup(pinIn, IO.IN)
        self.io=IO
        return

    #Para leer la distancia del sensor al objeto, creamos una funcion
    def detectarObstaculo(self):
       self.io.output(Trig, False) #apagamos el pin Trig
       time.sleep(2*10**-6) #esperamos dos microsegundos
       self.io.output(Trig, True) #encendemos el pin Trig
       time.sleep(10*10**-6) #esperamos diez microsegundos
       self.io.output(Trig, False) #y lo volvemos a apagar

      #empezaremos a contar el tiempo cuando el pin Echo se encienda
       while self.io.input(Echo) == 0:
          start = time.time()

       while self.io.input(Echo) == 1:
          end = time.time()

       #La duracion del pulso del pin Echo sera la diferencia entre
       #el tiempo de inicio y el final
       duracion = end-start

       #Este tiempo viene dado en segundos. Si lo pasamos
       #a microsegundos, podemos aplicar directamente las formulas
       #de la documentacion
       duracion = duracion*10**6
       medida = duracion/58 #hay que dividir por la constante que pone en la documentacion, nos dara la distancia en cm

       print "%.2f" %medida #por ultimo, vamos a mostrar el resultado por
