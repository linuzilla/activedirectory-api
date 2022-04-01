#!/bin/bash

baseurl="http://localhost:3000/ad365"

password=$1

if [ "$password" = "" ]; then
	echo -n "Enter password: "
	read password
fi

curl -i ${baseurl}/apis/local/encrypt/${password}
