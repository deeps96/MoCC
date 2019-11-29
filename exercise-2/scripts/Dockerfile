FROM ubuntu:18.04

WORKDIR /home
COPY forksum.c linpack.c memsweep.c measure-cpu.sh measure-mem.sh measure-fork.sh measure-disk-random-docker.sh ./
RUN apt-get update
RUN apt-get install -y gcc nginx fio bc
RUN mkdir file
RUN head -c 500M </dev/urandom >./file/random_file.txt

