#This script will build "TE-cipherlab-release-[version name]-unaligned.apk"
#and "TE-densowave-release-[version name]-unaligned.apk"
#under "\app\build\outputs\apk\"
import datetime
import glob
from tempfile import mkstemp
from shutil import move
import os
from os import remove, close, system
import subprocess

def presstoexit():
    print('Press to exit:')
    input()
    exit(-1)

def executecmd(command):
    p = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    output, stderr = p.communicate()
    p.wait()
    status = p.returncode
    return (status, output)

print('Enter New version code(integer):')
new_version_code = input()
print('Enter New version name(ex: 0.0.1):')
new_version_name = input()
os.chdir('..\\')

#Step1:svn update code
print('====== Step1:Execute [svn update] ========')
svn_update_command ='svn update'
status, output = executecmd(svn_update_command)
if status != 0:
    print('update fail, message = ' + output)
    presstoexit()

#Step2:Write version name and version code to gradle.properties
print('====== Step2:Write version name and version code to gradle.properties ========')
old_gradlePropt_path = '.\\gradle.properties'
fh, new_gradlePropt_path = mkstemp() #Create temp file
with open(new_gradlePropt_path,'w') as new_gradlePropt:
        with open(old_gradlePropt_path) as old_gradlePropt:
                for line in old_gradlePropt:
                        if line.count('TE_VERSION_CODE') > 0:
                                new_gradlePropt.write('TE_VERSION_CODE=' + str(new_version_code) + '\n')
                        elif line.count('TE_VERSION_NAME') > 0:
                                new_gradlePropt.write('TE_VERSION_NAME=' + str(new_version_name) + '\n')
                        else:
                                new_gradlePropt.write(line)
close(fh)
remove(old_gradlePropt_path) #Remove original file
move(new_gradlePropt_path, old_gradlePropt_path)#Move new file
  
#Step3:call gradlew to clean and build apks
print('====== Step3:Execute [clean old apks and build apks] ========')
for file in glob.glob("app/build/outputs/apk/*.apk"):
    os.remove(file)

build_apk_command = '.\gradlew -PversCode=' + str(new_version_code)+ ' -PversName=' + new_version_name +' clean assembleRelease' #ex: gradlew -PversCode=1 -PversName=0.0.1 clean assembleRelease
status, output = executecmd(build_apk_command)
if status != 0:
    print('build apks fail, message = ' + str(output))
    presstoexit()

#Step4: Move apks
print('====== Step4:Move file ========')
now = datetime.datetime.now()
despath = 'tools/Build_apks_' + str(now.strftime("%Y-%m-%d_%H_%M")) + '/'
if not os.path.exists(despath):
    os.makedirs(despath)
for srcfile in glob.glob('app/build/outputs/apk/*.apk'):
    os.rename(srcfile,  despath + os.path.basename(srcfile))
print('====== Done! ======')
presstoexit()