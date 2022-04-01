#!/bin/bash

auth="admin:hhjkUgeXJNVhXQ9Tqi_YZ"
baseurl="http://localhost:3000/ad365"

actions="home all user password suspend resume"

run_action() {
	action=$1
	if [ "$action" = "user" ]; then
		echo -n "Enter user account: "
		read acct
		if [ "$acct" != "" ]; then
			curl -su "$auth" ${baseurl}/apis/admin/user/${acct} | python -m json.tool
		fi
	elif [ "$action" = "getdrive" ]; then
		echo -n "Enter driver id: "
		read driveid
		if [ "$driveid" != "" ]; then
			curl -su "$auth" ${baseurl}/apis/admin/drive/${driveid} | python -m json.tool
		else
			echo "skip"
		fi
	elif [ "$action" = "suspend" ] || [ "$action" = "resume" ]; then
		echo -n "Enter user account: "
		read acct
		if [ "$acct" != "" ]; then
			curl -su "$auth" -X PUT ${baseurl}/apis/admin/${action}/${acct} | python -m json.tool
		fi
	elif [ "$action" = "password" ]; then
		echo -n "Enter user account: "
		read acct
		echo -n "Enter password for ${acct}: "
		read pass
		pass=$(echo -n $pass | openssl base64)
		curl -iu "$auth" \
			-H "Accept: application/json" -H "Content-Type: application/json" \
			-X POST \
			-d "{ \"account\": \"${acct}\", \"password\": \"${pass}\" }" \
			${baseurl}/apis/admin/password
		echo
	elif [ "$action" = "all" ]; then
		curl -su "$auth" ${baseurl}/apis/admin/$action  | python -m json.tool
		echo
	elif [ "$action" != "" ]; then
		echo "$action ($REPLY)"
		curl -u "$auth" -i ${baseurl}/apis/admin/$action
		echo
	else
		exit
	fi
}

if [ "$1" != "" ]; then
	while [ "$1" != "" ]; do
		run_action $1
		shift
	done
else
	select action in $actions; do
		run_action $action
	done
fi
