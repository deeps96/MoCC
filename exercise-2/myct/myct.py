import fire # https://github.com/google/python-fire
import wget
import tarfile
import os
import subprocess
import time
from sh import chroot, mount, unshare

class MyCT(object):

    def init(self, containerPath):
        DOWNLOAD_URL = 'http://cdimage.ubuntu.com/ubuntu-base/releases/18.04/release/ubuntu-base-18.04-base-amd64.tar.gz'
        targetPath = containerPath + 'base.tar.gz'
        # create dir if it does not exist
        wget.download(DOWNLOAD_URL, targetPath)
        tar = tarfile.open(targetPath, 'r:gz')
        tar.extractall(path=containerPath)
        tar.close()
        os.remove(targetPath)

    def map(self, containerPath, hostPath, targetPath):
        # create dir if it does not exist
        mount('--bind', '-o', 'ro', hostPath, containerPath + targetPath)

    def run(self, containerPath, executable, args='', namespace=None, limit=None):
        absolutePath = os.path.abspath(containerPath)
        #sudo unshare --mount --pid --fork --mount-proc=$PWD/test/proc chroot ./test/ /bin/bash
        command = 'unshare --mount --pid --fork --mount-proc='+absolutePath+'/proc chroot '+containerPath+' '+executable+' '+args
        if not namespace is None:
            command = 'nsenter --'+namespace.split('=')[0]+'=/proc/'+namespace.split('=')[1]+'/ns/'+namespace.split('=')[0]+' '+command.replace('--pid ', '').replace('--mount ', '')  
        current_time = str(int(round(time.time() * 1000)))
        if not limit is None:
            group = limit.split('=')[0].split('.')[0]
            os.system('sudo mkdir /sys/fs/cgroup/'+group+'/'+current_time)
            print('echo '+limit.split('=')[1]+' | sudo tee /sys/fs/cgroup/'+group+'/'+current_time+'/'+limit.split('=')[0])
            os.system('echo '+limit.split('=')[1]+' | sudo tee /sys/fs/cgroup/'+group+'/'+current_time+'/'+limit.split('=')[0])
        os.system('mount -t proc proc '+absolutePath+'/proc')
        #process = subprocess.call(command.split(' '))
        os.system(command)
        #os.system('echo $! | sudo tee /sys/fs/cgroup/'+group+'/'+current_time+'/tasks')
        #os.system('wait $pid')
        #os.system('umount '+absolutePath+'/proc')
        #if not limit is None:
        #    os.system('rmdir /sys/fs/cgroup/'+group+'/'+current_time)



if __name__ == '__main__':
    fire.Fire(MyCT)
