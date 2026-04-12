USERNAME="root"
PASSWORD="example"

if [[ $1 == "-h" || $1 == "--help" ]]; then
	echo "Abre una shell de mongo en el contenedor de mongo"
else 
	docker exec -it nifi-opendatalab2-mongodb-1 mongo -u $USERNAME -p $PASSWORD
fi
