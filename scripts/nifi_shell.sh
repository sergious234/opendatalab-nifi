if [[ $1 == "-h" || $1 == "--help" ]]; then
	echo "Abre una shell de bash en el contenedor de nifi"
else 
	docker exec -it nifi-opendatalab2-nifi-1 bash
fi
