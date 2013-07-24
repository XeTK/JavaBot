#!/bin/bash

AUDIT_FILE="audit.log"

java -classpath checkstyle-5.6-all.jar com.puppycrawl.tools.checkstyle.Main -c sun_checks.xml -r src/ > $AUDIT_FILE

echo "Audit completed"
echo "Log file written to $AUDIT_FILE"
