cd /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/ && echo "$(date +\%s)"",""$(sudo /bin/bash /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/measure-cpu.sh)" >> /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/native-cpu.csv && echo "$(date +\%s)"",""$(sudo /bin/bash /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/measure-mem.sh)" >> /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/native-mem.csv && echo "$(date +\%s)"",""$(sudo /bin/bash /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/measure-disk-random.sh)" >> /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/native-disk-random.csv && echo "$(date +\%s)"",""$(sudo /bin/bash /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/measure-fork.sh)" >> /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/native-fork.csv && echo "$(date +\%s)"",""$(sudo /bin/bash /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/measure-nginx.sh 172.18.4.217)" >> /home/constantin/Documents/Master/MethodsOfCloudComputing/MoCC/exercise-2/scripts/native-nginx.csv
