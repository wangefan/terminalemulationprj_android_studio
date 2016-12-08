#This script will build "TE-cipherlab-release-[version name]-unaligned.apk"
#and "TE-densowave-release-[version name]-unaligned.apk"
#under "\app\build\outputs\apk\"

from tempfile import mkstemp
from shutil import move
import os
from os import remove, close, system
from time import sleep

print('Enter New version code(integer):')
new_version_code = input()
print('Enter New version name(ex: 0.0.1):')
new_version_name = input()
os.chdir('..\\')
#Write version name and version code to gradle.properties
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
remove(old_gradlePropt_path)#Remove original file
move(new_gradlePropt_path, old_gradlePropt_path)#Move new file
  
#call gradlew to clean and build apks
build_apk_command = '.\\gradlew -PversCode=' + str(new_version_code)+ ' -PversName=' + new_version_name +' clean assembleRelease' #ex: gradlew -PversCode=1 -PversName=0.0.1 clean assembleRelease

print('execute [' + build_apk_command_one + ']')
system(build_apk_command)

#wait 20 min, print fail if not timeout
