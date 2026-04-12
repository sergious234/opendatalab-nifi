#!/bin/bash
# ------------------------
# MongoDB container info
# ------------------------
CONTAINER_NAME="nifi-opendatalab2-mongodb-1"
DB_NAME="Bikes"
USERNAME="root"
PASSWORD="example"
AUTH_DB="admin"


if [[ $1 == "-h" || $1 == "--help" ]]; then
    echo "Muestra un resumen con los documentos en mongoDB y la cantidad de registros"
    exit
fi

output=$(docker exec -i "$CONTAINER_NAME" mongo "$DB_NAME" \
    -u "$USERNAME" -p "$PASSWORD" --authenticationDatabase "$AUTH_DB" --quiet --eval '
var result = [];
var totalSize = 0;
db.getCollectionNames().forEach(function(collName) {
    var stats = db.getCollection(collName).stats();
    totalSize += stats.storageSize;
    var total = stats.count;
    var v3 = db.getCollection(collName).countDocuments({"version": "v3"});
    var v1 = total - v3;
    result.push(collName + " : " + total + " " + v1 + " " + v3);
});
result.push("__SIZE__ : " + totalSize + " 0 0");
result.forEach(function(l) { print(l); });
')

total_size=$(echo "$output" | grep "^__SIZE__" | awk '{print $3}')
output=$(echo "$output" | grep -v "^__SIZE__")

max_len=$(echo "$output" | awk '{print length($1)}' | sort -nr | head -1)
printf "%-${max_len}s | %12s | %12s | %12s\n" \
       "NAME" "TOTAL" "V1" "V3"
printf "%-${max_len}s-+-%12s-+-%12s-+-%12s\n" \
       "$(printf '%*s' $max_len '' | tr ' ' '-')" \
       "------------" "------------" "------------"

RED=$'\033[0;31m'
BLUE=$'\033[1;38;5;45m'
COLORV1=$'\033[38;5;178m'
COLORV3=$'\033[38;5;34m'
NC=$'\033[0m'
TOTAL_READS=0

while read -r name colon total v1 v3; do
    formatted_name=$(printf "%-${max_len}s" "$name")
    TOTAL_READS=$(( TOTAL_READS + total ))
    printf "%s | %'12d | %s%'12d%s | %s%'12d%s\n" \
        "${BLUE}${formatted_name}${NC}" \
        "$total" "$COLORV1" "$v1" "$NC" "$COLORV3" "$v3" "$NC"
done <<< "$output"

printf "Total collection size in disk: %s\n" "$(numfmt --to=iec "$total_size")"
printf "Total objects: %'d\n" "$TOTAL_READS"

if [[ $1 == "-h" || $1 == "--help" ]]; then
    echo "Muestra un resumen con los documentos en mongoDB y la cantidad de registros"
    exit
fi

output=$(docker exec -i "$CONTAINER_NAME" mongo "$DB_NAME" \
    -u "$USERNAME" -p "$PASSWORD" --authenticationDatabase "$AUTH_DB" --quiet --eval '
var result = [];
var totalSize = 0;
db.getCollectionNames().forEach(function(collName) {
    var stats = db.getCollection(collName).stats();
    totalSize += stats.storageSize;
    var total = stats.count;
    var v3 = db.getCollection(collName).countDocuments({"version": "v3"});
    var v1 = total - v3;
    result.push(collName + " : " + total + " " + v1 + " " + v3);
});
result.push("__SIZE__ : " + totalSize + " 0 0");
result.forEach(function(l) { print(l); });
')

total_size=$(echo "$output" | grep "^__SIZE__" | awk '{print $3}')
output=$(echo "$output" | grep -v "^__SIZE__")

max_len=$(echo "$output" | awk '{print length($1)}' | sort -nr | head -1)
printf "%-${max_len}s | %12s | %12s | %12s\n" \
       "NAME" "TOTAL" "V1" "V3"
printf "%-${max_len}s-+-%12s-+-%12s-+-%12s\n" \
       "$(printf '%*s' $max_len '' | tr ' ' '-')" \
       "------------" "------------" "------------"

RED=$'\033[0;31m'
BLUE=$'\033[1;38;5;45m'
COLORV1=$'\033[38;5;178m'
COLORV3=$'\033[38;5;34m'
NC=$'\033[0m'
TOTAL_READS=0

while read -r name colon total v1 v3; do
    formatted_name=$(printf "%-${max_len}s" "$name")
    TOTAL_READS=$(( TOTAL_READS + total ))
    printf "%s | %'12d | %s%'12d%s | %s%'12d%s\n" \
        "${BLUE}${formatted_name}${NC}" \
        "$total" "$COLORV1" "$v1" "$NC" "$COLORV3" "$v3" "$NC"
done <<< "$output"

printf "Total collection size in disk: %s\n" "$(numfmt --to=iec "$total_size")"
printf "Total objects: %'d\n" "$TOTAL_READS"
