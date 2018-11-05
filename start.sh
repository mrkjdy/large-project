#!/bin/sh
if [ "$NODE_ENV" != "production" ]; then
	undefinedVars=""
	while read -r line; do
		eval "varValue=\$${line%%=*}"
		if [ -z $varValue ]; then
			undefinedVars="$undefinedVars $line"
		fi
	done <<-E
		$(heroku config -s)
	E
fi
eval $undefinedVars node WebServer/server.js