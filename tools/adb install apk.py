import tkinter
from tkinter import filedialog
import os

from utility import presstoexit

root = tkinter.Tk()
root.withdraw() #use to hide tkinter window

currdir = os.getcwd()
tempdir = filedialog.askopenfilename(parent=root, initialdir=currdir, title='Please select a directory')
if len(tempdir) > 0:
    print ('You chose %s' % tempdir)
else:
    presstoexit('')