echo "$(date +\%s)"",""$(sudo /bin/bash /home/hans/MoCC/exercise-2/scripts/measure-cpu.sh)" >> /home/hans/MoCC/exercise-2/scripts/kvm-cpu.csv
echo "$(date +\%s)"",""$(sudo /bin/bash /home/hans/MoCC/exercise-2/scripts/measure-mem.sh)" >> /home/hans/MoCC/exercise-2/scripts/kvm-mem.csv
echo "$(date +\%s)"",""$(sudo /bin/bash /home/hans/MoCC/exercise-2/scripts/measure-disk-random.sh)" >> /home/hans/MoCC/exercise-2/scripts/kvm-disk-random.csv
echo "$(date +\%s)"",""$(sudo /bin/bash /home/hans/MoCC/exercise-2/scripts/measure-fork.sh)" >> /home/hans/MoCC/exercise-2/scripts/kvm-fork.csv