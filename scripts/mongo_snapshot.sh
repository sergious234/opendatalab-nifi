#!/bin/env bash

# ---- configuration ----
CONTAINER="nifi-opendatalab2-mongodb-1"      
DB="Bikes"                  									
OUTPUT_DIR="./mongo_sample"
USERNAME="root"
PASSWORD="example"
OUTPUT_FILE="${COLLECTION}_sample.json"
PERCENT="20" 

print_help() {
cat <<EOF
MongoDB Sampling Export Script
	
Exports a random sample of all collections from a MongoDB database
running inside a Docker container.

Usage:
	$(basename "$0") [options]

Options:
	-p, --percent <number>   Percentage of documents to export (default: 20)
	-h, --help               Show this help message and exit

Examples:
	$(basename "$0")
	$(basename "$0") -p 10 (10%)
	$(basename "$0") --percent 5 (5%)

Notes:
	- Exports a random sample of each collection.
	- Output files are saved in: $OUTPUT_DIR
EOF
}

# ---- argument parsing ----
while [[ $# -gt 0 ]]; do
    case $1 in
        -p|--percent)
            PERCENT="$2"
            shift 2
            ;;
        -h|--help)
            print_help
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use -h or --help"
            exit 1
            ;;
    esac
done

# ---- validation ----
if ! [[ "$PERCENT" =~ ^[0-9]+$ ]] || [ "$PERCENT" -le 0 ] || [ "$PERCENT" -gt 100 ]; then
    echo "Percent must be an integer between 1 and 100"
    exit 1
fi

SAMPLE_RATIO=$(awk "BEGIN {print $PERCENT/100}")
echo "Sample ratio: $SAMPLE_RATIO%"

mkdir -p "$OUTPUT_DIR"

COLLECTIONS=$(docker exec "$CONTAINER" mongo "$DB" -u "$USERNAME" -p "$PASSWORD" --authenticationDatabase "admin" --quiet --eval 'db.getCollectionNames().join(" ")')

echo "Collections found:"
echo "$COLLECTIONS"
echo ""

for COLLECTION in $COLLECTIONS
do
    echo "Exporting sample from $COLLECTION..."

    docker exec "$CONTAINER" mongoexport \
				--username "$USERNAME" \
				--password "$PASSWORD" \
				--authenticationDatabase "admin" \
        --db "$DB" \
        --collection "$COLLECTION" \
        --query "{ \"\$expr\": { \"\$lt\": [ { \"\$rand\": {} }, $SAMPLE_RATIO ] } }" \
        --out "/tmp/${COLLECTION}_sample.json"

    docker cp "$CONTAINER:/tmp/${COLLECTION}_sample.json" "$OUTPUT_DIR/${COLLECTION}.json"

    docker exec "$CONTAINER" rm "/tmp/${COLLECTION}_sample.json"

    echo "$COLLECTION done"
    echo ""
done


echo "All collections exported."
echo "Output directory: $OUTPUT_DIR"
