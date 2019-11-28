#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int READ = 0;
int WRITE = 1;

int forksum(int lower_bound, int upper_bound);
void spawn_child(int lower_bound, int upper_bound, int* pipe);

int main(int argc, char* argv[]) {
    char *lower_bound_string = argv[1];
    int lower_bound = atoi(lower_bound_string);
    char *upper_bound_string = argv[2];
    int upper_bound = atoi(upper_bound_string);

    int sum = forksum(lower_bound, upper_bound);
    printf("%d", sum);
    return 0;
}

void spawn_child(int lower_bound, int upper_bound, int* pipe) {
    pid_t child = fork();
    if (child != 0) {
      int status;
      waitpid(child, &status, 0);
      return;
    }
    close(pipe[READ]);
    int sum = forksum(lower_bound, upper_bound);
    write(pipe[WRITE], &sum, sizeof(sum));
    close(pipe[WRITE]);
}

int forksum(int lower_bound, int upper_bound){
    if(lower_bound == upper_bound){
        return lower_bound;
    } else {
        int left_lower_bound = lower_bound;
        int left_upper_bound = lower_bound + ((upper_bound - lower_bound) / 2);
        int right_lower_bound = left_upper_bound + 1;
        int right_upper_bound = upper_bound;

        // pipe
        int leftPipe[2];
        int rightPipe[2];
        int leftSum;
        int rightSum;
        // int status;
        // int wpid;

        pipe(leftPipe);
        pipe(rightPipe);

        int pid = getppid();
        spawn_child(left_lower_bound, left_upper_bound, leftPipe);
        if (pid == getppid()) {
          // while ((wpid = wait(&status)) > 0); // wait for both to complete
          spawn_child(right_lower_bound, right_upper_bound, rightPipe);
        }
        if (pid == getppid()) {
          close(leftPipe[WRITE]);
          close(rightPipe[WRITE]);
          // while ((wpid = wait(&status)) > 0); // wait for both to complete
          read(leftPipe[READ], &leftSum, sizeof(leftSum));
          read(rightPipe[READ], &rightSum, sizeof(rightSum));
          close(leftPipe[READ]);
          close(rightPipe[READ]);
          return leftSum + rightSum;
        }
    }
    exit(0);
}
