#include <stdio.h>
#include <stdlib.h>

int main(int argc, char* argv[]) {
    int chunkCount = 500000;
    int chunkSize = 1024;
    FILE *fp;
    fp = fopen("/dev/urandom", "r");
    char* data = (char*) malloc(sizeof(char) * chunkSize * chunkCount);
    for (int iChunk = 0; iChunk < chunkCount; iChunk++) {
      fgets(data + iChunk * chunkSize, chunkSize, (FILE*) fp);
    }
}
