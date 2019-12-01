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
        os.system('mkdir ' + containerPath)
        targetPath = containerPath + 'base.tar.gz'
        # create dir if it does not exist
        wget.download(DOWNLOAD_URL, targetPath)
        tar = tarfile.open(targetPath, 'r:gz')
        tar.extractall(path=containerPath)
        tar.close()
        os.remove(targetPath)

    def map(self, containerPath, hostPath, targetPath):t
        os.system('mkdir ' + containerPath + targetPath)
        mount('--bind', '-o', 'ro', hostPath, containerPath + targetPath)

    def run(self, containerPath, executable, args='', namespace=None, limit=None):
        absolutePath = os.path.abspath(containerPath)
        command = 'unshare --mount --pid --fork --mount-proc='+absolutePath+'/proc chroot '+containerPath+' '+executable+' '+args
        if not namespace is None:
            command = 'nsenter --'+namespace.split('=')[0]+'=/proc/'+namespace.split('=')[1]+'/ns/'+namespace.split('=')[0]+' '+command.replace('--pid ', '').replace('--mount ', '')
        current_time = str(int(round(time.time() * 1000)))
        if not limit is None:
            group = limit.split('=')[0].split('.')[0]
            os.system('cgcreate -g '+group+':'+current_time)
            os.system('echo '+limit.split('=')[1]+' | sudo tee /sys/fs/cgroup/'+group+'/'+current_time+'/'+limit.split('=')[0])
	    command = 'sudo cgexec -g ' + group + ':' + current_time + ' /bin/bash -c ' + command
        os.system('mount -t proc proc '+absolutePath+'/proc')
        os.system(command)
        os.system('wait $!')
        os.system('umount '+absolutePath+'/proc')
        if not limit is None:
            os.system('sudo cgdelete ' + group +':' + current_time)


if __name__ == '__main__':
    fire.Fire(MyCT)
