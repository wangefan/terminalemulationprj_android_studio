import subprocess
from tkinter import *

def presstoexit(message):
    print(message + ', Press Enter to exit:')
    input()
    exit(-1)

def executecmd(command):
    p = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    output, stderr = p.communicate()
    p.wait()
    status = p.returncode
    return (status, output.decode("utf-8"))


def dlgChooseItem(title, pathes):
    if len(pathes) == 0:
        return
    elif len(pathes) == 1:
        return pathes[0]
    returned_value = {}
    returned_value['path'] = ''
    root = Tk()
    root.wm_title(title)
    def btnCallBack(patharg):
        returned_value['path'] = patharg
        root.destroy()

    for path in pathes:
        frame = Frame(root)
        frame.pack()
        button = Button(frame, text=path, command=lambda pathTemp = path: btnCallBack(pathTemp))
        button.pack()

    root.mainloop()
    return returned_value['path']
