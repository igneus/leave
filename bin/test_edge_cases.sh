#!/bin/bash

run_cli="java -Don_alarm_exit=true -cp target/leave-1.0-SNAPSHOT.jar cz.yakub.leave.Main"
run_gui="java -Don_alarm_exit=true -jar target/leave-1.0-SNAPSHOT.jar"

echo "(This test suite takes a few minutes to finish.)"

$run_cli +0000

$run_gui +0000
$run_gui +0001
$run_gui +0002

echo
echo "If no uncaught exceptions occurred so far, this is success."
