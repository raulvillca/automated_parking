#!/usr/bin/python
import RPi.GPIO as IO
import time

class Display:
    def __init__(self, arrayPin):
        for pin in arrayPin:
            IO.setup(pin, IO.OUT)

        self.io=IO
        return

    def printMessage(self):
        return

    def clearDisplay(self):
        self.io.cleanup()
        return


# 1 : GND
# 2 : 5V
# 3 : Contrast (0-5V)*
# 4 : RS (Register Select)
# 5 : R/W (Read Write) - GND
# 6 : Enable or Strobe
# 7 : Data Bit 0 - SIN USAR
# 8 : Data Bit 1 - SIN USAR
# 9 : Data Bit 2 - SIN USAR
# 10: Data Bit 3 - SIN USAR
# 11: Data Bit 4
# 12: Data Bit 5
# 13: Data Bit 6
# 14: Data Bit 7
# 15: LCD Backlight +5V**
# 16: LCD Backlight GND

IO.cleanup()

# DEFINICIONES PARA ASIGNACION DE PINES
LCD_RS = 27
LCD_E = 22
LCD_D4 = 24
LCD_D5 = 25
LCD_D6 = 4
LCD_D7 = 17

# DEFINIR CONSTANTES DEL DISPOSITIVO
LCD_WIDTH = 16 # CARACTERES MAXIMOS POR FILA
LCD_CHR = True
LCD_CMD = False
LCD_LINE_1 = 0x80 # DIRECCION RAM PARA PRIMERA LINEA
LCD_LINE_2 = 0xC0 # DIRECCION RAM PARA SEGUNDA LINEA
E_PULSE = 0.00005 # CONSTANTES PARA RETARDOS
E_DELAY = 0.00005

def main():
    # DEFINIR GPIO COMO SALIDA PARA USAR LA LCD
    IO.setmode(GPIO.BCM) # USAR LA NOMENCLATURA BCM (HARDWARE)
    IO.setup(LCD_E, IO.OUT) # E
    IO.setup(LCD_RS, IO.OUT) # RS
    IO.setup(LCD_D4, IO.OUT) # DB4
    IO.setup(LCD_D5, IO.OUT) # DB5
    IO.setup(LCD_D6, IO.OUT) # DB6
    IO.setup(LCD_D7, IO.OUT) # DB7

    # INICIALIZAR DISPLAY
    lcd_init()

    # ENVIAR DATOS DE PRUEBA
    lcd_byte(LCD_LINE_1, LCD_CMD)
    lcd_string("Rasbperry Pi")
    lcd_byte(LCD_LINE_2, LCD_CMD)
    lcd_string("Model B")
    time.sleep(5)

    lcd_byte(LCD_LINE_1, LCD_CMD)
    lcd_string("HeTPro")
    lcd_byte(LCD_LINE_2, LCD_CMD)
    lcd_string("PiDuino / LCD")
    time.sleep(5)

def lcd_init():
    # PROCESO DE INICIALIZACION
    lcd_byte(0x33,LCD_CMD)
    lcd_byte(0x32,LCD_CMD)
    lcd_byte(0x28,LCD_CMD)
    lcd_byte(0x0C,LCD_CMD)
    lcd_byte(0x06,LCD_CMD)
    lcd_byte(0x01,LCD_CMD)

def lcd_string(message):
    # ENVIAR UN STRING A LA LCD
    message = message.ljust(LCD_WIDTH," ")
    for i in range(LCD_WIDTH):
        lcd_byte(ord(message[i]),LCD_CHR)

def lcd_byte(bits, mode):
    # ENVIAR UN BYTE A LOS PINES DE DATOS
    # bits = DATOS
    # mode = True PARA CARACTER
    # False PARA COMANDO
    # VER HOJA DE DATOS

    IO.output(LCD_RS, mode) # RS
    IO.output(LCD_D4, False)
    IO.output(LCD_D5, False)
    IO.output(LCD_D6, False)
    IO.output(LCD_D7, False)
    if bits&0x10==0x10:
        IO.output(LCD_D4, True)
    if bits&0x20==0x20:
        IO.output(LCD_D5, True)
    if bits&0x40==0x40:
        IO.output(LCD_D6, True)
    if bits&0x80==0x80:
        IO.output(LCD_D7, True)

    time.sleep(E_DELAY)
    IO.output(LCD_E, True)
    time.sleep(E_PULSE)
    IO.output(LCD_E, False)
    time.sleep(E_DELAY)

    IO.output(LCD_D4, False)
    IO.output(LCD_D5, False)
    IO.output(LCD_D6, False)
    IO.output(LCD_D7, False)

    if bits&0x01==0x01:
        IO.output(LCD_D4, True)
    if bits&0x02==0x02:
        IO.output(LCD_D5, True)
    if bits&0x04==0x04:
        IO.output(LCD_D6, True)
    if bits&0x08==0x08:
        IO.output(LCD_D7, True)

    time.sleep(E_DELAY)
    IO.output(LCD_E, True)
    time.sleep(E_PULSE)
    IO.output(LCD_E, False)
    time.sleep(E_DELAY)

if __name__ == '__main__':
    main()
