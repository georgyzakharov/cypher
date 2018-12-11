#!/bin/bash

if [ "$1" == "start" ] ; then
	if $(javac "$HOME"/cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.java 2>/dev/null) ; then
		java -cp "$HOME"/cypher/cypher-backend/src/main/java edu.sunypoly.cypher.backend.service.DockerManager
		rm "$HOME"/cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.class
	elif $(javac "$HOME"/git/cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.java 2>/dev/null) ; then
		java -cp "$HOME"/git/cypher/cypher-backend/src/main/java edu.sunypoly.cypher.backend.service.DockerManager
		rm "$HOME"/git/cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.class
	else
		javac /cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.java
		java -cp /cypher/cypher-backend/src/main/java/ edu.sunypoly.cypher.backend.service.DockerManager
		rm /cypher/cypher-backend/src/main/java/edu/sunypoly/cypher/backend/service/DockerManager.class
	fi
elif [[ "$1" == "stop" && "$#" > 1 ]] ; then
	for cont in "$@" ; do
		if ! [ "$cont" == "stop" ] ; then
			echo
			echo "Stopping Docker container '$cont-cypher'..."
			if $(docker stop $cont 1>/dev/null 2>&1) ; then
				echo "Stopped Docker container '$cont-cypher'."
				echo "Removing Docker container '$cont-cypher'..."
				if $(docker rm $cont 1>/dev/null 2>&1) ; then
					echo "Removed Docker container '$cont-cypher'."
				else
					echo "Error: Failed to remove Docker container '$cont-cypher'."
				fi
			else
				echo "Error: Failed to stop Docker container '$cont-cypher'."
			fi
		fi
	done
elif [ "$1" == "stop" ] ; then
	declare -a conts=(python openjdk gcc)
	for cont in "${conts[@]}" ; do
		echo
		echo "Stopping Docker container '$cont-cypher'..."
		if $(docker stop "$cont"-cypher 1>/dev/null 2>&1) ; then
			echo "Stopped Docker container '$cont-cypher'."
			echo "Removing Docker container '$cont-cypher'..."
			if $(docker rm "$cont"-cypher 1>/dev/null 2>&1) ; then
				echo "Removed Docker container '$cont-cypher'."
			else
				echo "Error: Failed to remove Docker container '$cont-cypher'."
			fi
		else
			echo "Error: Failed to stop Docker container '$cont-cypher'."
		fi
	done
else
	echo "Invalid argument! '$1'"
	echo "Acceptable values: 'start', 'stop'"
fi
