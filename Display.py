import LCDDriver
from time import *

lcd = LCDDriver.Lcd()

lcd.lcd_display_string("antisteo on YT", 1)
lcd.lcd_display_string("LCD runtime is", 2)
lcd.lcd_display_string("picorder", 3)
lcd.lcd_display_string("connect me via I2C", 4)

for i in range(1,100):
    lcd.lcd_display_string(str(i), 3, 1)
    sleep (1)
