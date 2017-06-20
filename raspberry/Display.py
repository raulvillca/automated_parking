import LCDDriver
from time import *

lcd = LCDDriver.Lcd()

lcd.lcd_display_string("antisteo on YT", 1)
lcd.lcd_display_string("LCD runtime is", 2)

for i in range(1,100):
    lcd.lcd_display_string(str(i), 3, 1)
    sleep (1)
