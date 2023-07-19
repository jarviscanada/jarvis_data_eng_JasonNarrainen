#!/bin/bash

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

get_second_field () {
  func_result="$(echo "$1" | grep -e "$2" | awk -F ': ' '{print $2}' | xargs)"
}

hostname=$(hostname -f)

lscpu_out=$(lscpu)

get_second_field "$lscpu_out", "^CPU(s):" 2
cpu_number="$func_result"

get_second_field "$lscpu_out" "^Architecture:" 2
cpu_architecture="$func_result"

get_second_field "$lscpu_out" "^Model name:" 2
cpu_model="$func_result"

get_second_field "$lscpu_out" "^CPU MHz:" 2
cpu_mhz="$func_result"

get_second_field "$lscpu_out" "^L2 cache" 2
l2_cache=$(echo "$func_result" | rev | cut -c 2- | rev)

timestamp="$(date -u '+%Y-%m-%d %H:%M:%S')"

total_mem=$(vmstat -s --unit M | grep "total memory" | awk '{print $1}')

insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz,
  l2_cache, timestamp, total_mem) VALUES('$hostname', '$cpu_number', '$cpu_architecture',
  '$cpu_model', '$cpu_mhz', '$l2_cache', '$timestamp', '$total_mem');"

export PGPASSWORD="$psql_password"

psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
exit $?