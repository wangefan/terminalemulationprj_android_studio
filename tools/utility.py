import subprocess

def presstoexit(message):
    print(message + '\nPress to exit:')
    input()
    exit(-1)

def executecmd(command):
    p = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    output, stderr = p.communicate()
    p.wait()
    status = p.returncode
    return (status, output.decode("utf-8"))