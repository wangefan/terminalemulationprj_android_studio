import glob
from utility import presstoexit, executecmd, dlgChooseItem

#Find apks from current directory
apks = glob.glob('*.apk')

#List apks to choose if there are more than one.
dest_apk = ''
apk_num = len(apks)
if apk_num > 1:
    dest_apk = dlgChooseItem('Please choose apk to install', apks)
elif apk_num == 1:
    dest_apk = apks[0]

if len(dest_apk) <= 0:
    presstoexit('=== no apks====')

#Use adb shell to get android devices
list_devices_command = 'adb devices'
status, output = executecmd(list_devices_command)
if status != 0:
    presstoexit('=== adb devices fail====, message = ' + output)
devices = output.splitlines()
if len(devices) <= 2:
    presstoexit('=== no devices====')

devices.pop() #remove last item which contains '\r\n\r\n'
devices.pop(0) #remove first item which contains 'List of devices attached\r\n'
for index, path in enumerate(devices):
    devices[index] = path.replace('\tdevice', '')

#List devices  to choose if there are more than one.
if len(devices) > 1:
    dest_device = dlgChooseItem('Please choose device', devices)
else:
    dest_device = devices[0]


if len(dest_device) <= 0:
    presstoexit('=== not choose device====')

#use adb shell to install
uninstall_apk_command = 'adb -s ' + dest_device + ' uninstall com.cipherlab.terminalemulation'
executecmd(uninstall_apk_command) #uninstall package name for debug version
uninstall_apk_command = 'adb -s ' + dest_device + ' uninstall com.densowave.terminalemulation'
executecmd(uninstall_apk_command) #uninstall package name for debug version

install_apk_command = 'adb -s ' + dest_device + ' install ' + dest_apk

status, output = executecmd(install_apk_command)
if status != 0:
    presstoexit('=== install ' + dest_apk + ' to ' + dest_device + ' fail====, message = ' + output)
else:
    presstoexit('=== install ' + dest_apk + ' to ' + dest_device + ' ok!====')