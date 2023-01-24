Implementation of the leave utility
as specified in [this FreeBSD man page][manpage].

## Building

`$ mvn package`

## Usage

`$ ./bin/leave.sh 0955` to set alarm for 09:55
`$ ./bin/leave.sh +0230` to set alarm for 2 hours, 30 minutes from now

`$ ./bin/gui-leave.sh 0955` sets the same alarm, but, instead of the traditional printing to the console, creates
a green icon in the system tray, which at the specified time turns red and bombs you with notifications.

## Running tests

`$ mvn test`

## License

GNU/GPL 3.0

[manpage]: https://www.freebsd.org/cgi/man.cgi?query=leave
