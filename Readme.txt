Jeremy Coffman

Operating Systems

PA1


This is an implementation of a very basic REPL command line interface. It was written for a class in operating systems I took a few years ago. The purpose of the exercise was to write a multithreaded command line interface which would support piping output from one command to the next. Each command is given its own thread to execute within. Supported commands are: pwd, lc, ls, cd, cat, grep (a basic implementation), history (output history of commands), and !n (where n is an integer), which prints the nth command executed. Communication between threads connected by a pipe is handled through LinkedBlockingQueues, which are synchronized. Commands connected by a pipe share a blocking queue, with the first command writing output to the input blocking queue of the second command.

To run, compile all java files with javac, then run: java CommandLine
Enter 'exit' to exit the REPL.