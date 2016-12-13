import tkinter
from tkinter import filedialog
import os

from utility import presstoexit, executecmd

root = tkinter.Tk()
root.withdraw() #use to hide tkinter window

currdir = os.getcwd()
tempdir = filedialog.askopenfilename(parent=root, initialdir=currdir, title='Please select a directory')
if len(tempdir) > 0:
    print ('You chose %s' % tempdir)
#else:
#    presstoexit('')

list_devices_command = 'adb devices'
status, output = executecmd(list_devices_command)
if status != 0:
    presstoexit('update fail, message = ' + output)
print ('Devices = ' + output)
presstoexit('')