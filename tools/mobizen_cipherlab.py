import pyautogui
import os
from time import sleep
os.system("TASKKILL /F /IM Mobizen.exe")
sleep(3)
os.startfile('C:\\Program Files (x86)\\RSUPPORT\\Mobizen\\Mobizen.exe')
print('Launch Mobizen')
sleep(4)
print('After sleep 5 sec')
pyautogui.moveTo(780, 630, 0.4)
pyautogui.click()
sleep(1.5)
pyautogui.moveTo(870, 460, 0.4)
pyautogui.click()
sleep(1.5)
pyautogui.typewrite('gmailsw23058518', interval=0.01)
sleep(0.1)
pyautogui.press('enter') 
