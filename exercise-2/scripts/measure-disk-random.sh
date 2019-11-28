#!/bin/bash
# This function is based on: https://stackoverflow.com/questions/41750008/get-median-of-unsorted-array-in-one-line-of-bash
function median(){
  arr=($(printf '%lf\n' "${@}" | sort -n))
  nel=${#arr[@]}
  if (( $nel % 2 == 1 )); then     # Odd number of elements
    val="${arr[ $(($nel/2)) ]}"
  else                            # Even number of elements
    (( j=nel/2 ))
    (( k=j-1 ))
    val=$(echo "scale=3;(${arr[j]}" + "${arr[k]})"/2|bc -l)
  fi
  echo $val
}

results=()
start=$(date +"%s")
current=$start
diff=$(($current-$start))
while (($diff < 20))
do
        result=$(fio --name=test --size=1G --filename=file1 --direct=1 --rw=randwrite --bs=1M | tail -4 | head -1 | rev | cut -d " " -f7 | rev | tr -dc '0-9')
        rm file1
        current=$(date +"%s")
        diff=$(($current-$start))
        results+=($result)
done


median "${results[@]}"
