import fire # https://github.com/google/python-fire
import wget
import tarfile
import os
from sh import mount

class MyCT(object):

    def init(self, containerPath):
        DOWNLOAD_URL = 'http://cdimage.ubuntu.com/ubuntu-base/releases/18.04/release/ubuntu-base-18.04-base-amd64.tar.gz'
        targetPath = containerPath + 'base.tar.gz'
        wget.download(DOWNLOAD_URL, targetPath)
        tar = tarfile.open(targetPath, 'r:gz')
        tar.extractall(path=containerPath)
        tar.close()
        os.remove(targetPath)

    def map(self, containerPath, hostPath, targetPath):
        mount('--bind', '-o ro', hostPath, containerPath + targetPath)

    def run(self, containerPath, executable, args=[], namespace='kind=pid', limit='controllerkey=somekey'):
        return 'run'

if __name__ == '__main__':
    fire.Fire(MyCT)