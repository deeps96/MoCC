#!/bin/bash
#sudo nginx
#sudo nginx -s reload
#sudo nginx -s quit
#head -c 500M </dev/urandom >random_file.txt
#default.conf:
#server {
#    listen 3333;
#    root /usr/share/nginx/html;

#    location /file/ {
#        root /home/;
#    }
#}
#File Location: /home/file/random_file.txt

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
        before=$(($(date +"%s%N")/1000000))
        wget -qO- http://"$1":3333/file/random_file.txt &> /dev/null | xargs -P10 > /dev/null 2>&1
        after=$(($(date +"%s%N")/1000000))
        result=$(echo "$after - $before" | bc -l)
        current=$(date +"%s")
        diff=$(($current-$start))
        results+=($result)
done

median "${results[@]}"


