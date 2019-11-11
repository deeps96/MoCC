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
        sync
        result_string=$(dd if=/dev/zero of=tempfile bs=1M count=1024 2>&1)
        sync
        bytes=$(echo $result_string | rev | cut -d " " -f11 | rev)
        seconds=$(echo $result_string | rev | cut -d " " -f4 | rev)
        result=$(echo "scale=3;($bytes" / "$seconds)"|bc -l)
        rm tempfile
        current=$(date +"%s")
        diff=$(($current-$start))
        results+=($result)
done

median "${results[@]}"
