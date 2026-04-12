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
db.getCollectionNames().forEach(function(collName) {
    var total = db.getCollection(collName).count();
		var v3 = db.getCollection(collName).find({"version": "v3"}).count();
		var v1 = total - v3;
    print(collName + " : " + total + " " + v1 + " " + v3);
});
')



# Find max length of collection names
max_len=$(echo "$output" | awk '{print length($1)}' | sort -nr | head -1)

printf "%-${max_len}s | %12s | %12s | %12s\n" \
       "NAME" "TOTAL" "V1" "V3"

printf "%-${max_len}s-+-%12s-+-%12s-+-%12s\n" \
       "$(printf '%*s' $max_len '' | tr ' ' '-')" \
       "------------" "------------" "------------"

# Colors
# 	\033[38;5;196m
# 	This is the SGR (Select Graphic Rendition) sequence for colors.
# 	\033[ mandatory scape sequence
# 	38 → Set foreground color (text color).
# 	5 → Use 256-color mode (indexed color).
# 	196 → Color index in the 256-color palette (a pure bright red).
# 	m → Apply the style.
# 	So 38;5;196m = “Set text color to color #196 in 256-color mode”.
RED=$'\033[0;31m'
BLUE=$'\033[1;38;5;45m'
COLORV1=$'\033[38;5;178m'
COLORV3=$'\033[38;5;34m'
NC=$'\033[0m'

TOTAL_READS=0;
while read -r name colon total v1 v3; do
	formatted_name=$(printf "%-${max_len}s" "$name")
	TOTAL_READS=$(( TOTAL_READS+$total ))
	printf "%s | %'12d | %s%'12d%s | %s%'12d%s\n" \
		"${BLUE}${formatted_name}${NC}" \
		"$total" "$COLORV1" "$v1" "$NC" "$COLORV3" "$v3" "$NC"
done <<< "$output"

total=$(docker exec -i "$CONTAINER_NAME" mongo "$DB_NAME" \
	-u "$USERNAME" -p "$PASSWORD" --authenticationDatabase "$AUTH_DB" --quiet --eval '
db.getCollectionNames().map(e => db.getCollection(e).storageSize()).reduce((acc,x) => acc+x, 0)
')

printf "Total collection size in disk: %s\n" $(numfmt --to=iec $total)
printf "Total objects: %'d\n" $TOTAL_READS
