# ISA-Tasks
Tasks for Info Storage &amp; Analysis course



## Java Tasks

### Task J1
Write a program that creates a thread. The parent and newly created threads should print out ten lines of text.

### Task J2
Modify the program from "Task #1" so that the parent thread outputs after the child terminates.

### Task J3
Write a program that creates four threads that execute the same method.
This method should print the sequence of text strings passed as a parameter. Each of the threads created should print out a different sequence of lines.

### Task J4
The child thread should print the text to the screen. Two seconds after the creation of the child thread, the parent thread must terminate it.

### Task J5
Modify the program from "Task 4" so that the child thread prints out a message about it before terminating.

### Task J6
Appendix 1 contains the code of the program that you need to modify.

Legend:
1. The Company class characterizes a company divided into departments.
2. Each department (Department class) can calculate the sum from 1 to n, where n is a random number from 1 to 6.
3. Each summation iteration takes 1 second. Therefore: if n is 3, then it will take 3 seconds to calculate the sum 0 + 1 + 2 (the performCalculations method).

You need to add an implementation of the Founder class, in which:
1. You will have a list with all Workers (Runnable).
2. Each Worker must be assigned its own Department.
3. Each Worker must initiate the start of calculations.
4. After calculations are completed in all threads, you need to display the result (showCollaborativeResult method)
Note:
It is necessary to understand how barriers work in Java.

### Task J7
Write a program that calculates the number Pi using the Leibniz series (Fig. 1).
The number of program threads must be specified by a command line option.
The number of iterations can be determined at compile time.

### Task J8
Modify the program from “Assignment #7” so that it does not terminate on its own.
Instead, after receiving the SIGINT signal, the program should exit as soon as possible, collect the partial sums of the series, and print the resulting number approximation.

Recommendations:
The expected solution is that you install a SIGINT handler. Consider how to minimize the error caused by different threads having different number of iterations by the time they complete. Most likely, such minimization should be provided by increasing the time between receiving the signal and the output.

### Task J9
Write a program that simulates the well-known dining philosopher problem. Five philosophers sit at a round table eating spaghetti. Spaghetti is eaten with two forks. Every two philosophers sitting next to each other use a common fork. The philosopher thinks for a while, then tries to pick up the forks and starts eating. After eating some spaghetti, the philosopher releases the forks and begins to think again. After a while, he starts eating again, and so on, until the spaghetti runs out. If one of the forks cannot be taken, the philosopher waits until it is free. As an implementation option: philosophers are simulated using threads, periods of reflection and eating are simulated using falling asleep, and forks are simulated using mutexes. Philosophers always take the left fork first and then the right.

Additionally:
Explain under what circumstances this can lead to a deadlock.
Change the protocol of interaction between philosophers and forks in such a way that the dead
blocking did not occur.

### Task J10
Modify the program from “Task #1” so that the output of the parent and child
threads was synchronized: first the parent thread output the first line, then
child, then parent second row, and so on. Use mutexes.

Note:
Explicit and implicit transfers of control between threads and idle loops are allowed
only be used during the initialization phase.

### Task J11
Solve “Assignment #10” using two counter semaphores.

### Task J12
The parent thread of the program must read the lines entered by the user.
and put them at the top of the linked list. Lines longer than 80 characters can be cut
for several lines. When you enter an empty string, the program should return the current
list state. The child thread wakes up every five seconds and sorts the list
in lexicographic order (use bubble sort).

Note:
All operations on the list must be synchronized using synchronized
blocks on head objects.

### Task J13
Solve the problem from “Assignment #9” using atomic fork capture. When the philosopher
can take one fork, but cannot take another, he must put the fork on the table
and wait until both forks are released.
Recommendation: create another forks mutex and a condition variable. When trying to take
Philosopher must capture forks and check the availability of both forks. If one
from the forks is not available, the philosopher must release the second fork (if he has time to
capture it) and sleep on the condition variable. Freeing the forks, the philosopher must
notify other philosophers about it with a condition variable. Thoroughly
consider the procedure for acquiring and releasing mutexes to avoid errors
lost awakening.

### Task J14
Develop a mock production line that makes screws (widget).
The screw is assembled from part C and a module, which, in turn, consists of parts A and
B. Part A takes 1 second, B two seconds, C three seconds.
Simulate the delay in the manufacture of parts by falling asleep. Use
counter semaphores.

## Clojure Tasks
### Task C1
Given an alphabet in form of a list containing 1-character strings and a number N. Define a function that returns all the possible strings of length N based on this alphabet and containing no equal subsequent characters.
Use map/reduce/filter/remove operations and basic operations for lists such as str, cons, .concat, etc.
No recursion, generators or advanced functions such as flatten!

Example: for the alphabet ("а" "b " "c") and N=2 the result bust be ("ab" "ac" "ba" "bc" "ca" "cb") up to permutation.

### Task C2
Define the infinite sequence of prime numbers. Use Sieve of Eratosthenes algorithm with infinite cap.
Cover code with unit tests.
### Task C3
Implement a parallel variant of filter using futures. Each future must process a block of elements, not just a single element. The input sequence could be either finite or infinite. Thus, the implemented filter must possess both laziness and performance improvement by utilizing parallelism.
Cover code with unit tests.
Demonstrate the efficiency using time.
### Task C4
Similar to the task 14 (Java Concurrency) implement a production line for safes and clocks (code skeleton with line structure is provided).
Implementation must use agents for processors (factories) and atoms in combination with agents for storages.
The storage’s agent must be notified by the factory that the appropriate resource is produced, the agent puts it to the storage’s atom and notifies all the engaged processors.
The task is to implement the processor’s notification message (a function).
See requirements for the notification message implementation inside the code provided.
The production line configuration is also provided within code.
Note that there is a shortage for metal. Make sure that both final wares (clocks and safes) are produced despite this (at reduced rate of course).
### Task C5
Solve the dining philosophers problem using Clojure’s STM (see Java Concurrency task 9). Each fork must be represented by a ref with counter inside that shows how many times the given fork was in use.
A number of philosophers, length of thinking and dining periods and their number must be configurable.
Measure the efficiency of the solution in terms of overall execution time and number of transaction restarts (atom counter could be used for this).
Experiment with even and odd numbers of philosophers. Try to achieve live-lock scenario.

## XML Tasks

### Task X1
- Parse the XML file provided using Java SAX/StAX.
- For each person extract all the available information: id, first name, last name, gender, spouse, parents, children, siblings
- Validate the consistency: there are auxiliary markers in XML such as number of children
- Prepare to write the collected data into another XML, where there is the only entry for each person and it contain entire information about the person. The information must be as structured as possible. For example, use brother and sister terms instead of sibling.
Each entry from the original file contains only part of information about the person. There are multiple entries for each person and they could duplicate. Format is not strict so the same type of information could be represented differently.

### Task X2
- Define a strict XML Schema to represent the data extracted in X1 in well-structured and strict form. Use ID/IDREF where possible.
- Extend X1 solution to write the data extracted using JAXB with schema validation.

### Task X3
Write the XSLT for the XML document from the task X2 that finds a person who simultaneously has parents, grand-parent and siblings. XSLT should generate HTML document with that shows information about:
- this person
- his/her father
- mother
- brothers
- sisters.
For each person in the list above the information must be the following:
- name and gender
- names of father, mother, brothers, sisters, sons, daughters
- names of grand-mother, grand-father, uncles and aunts.


Hint: XSLT functions position() and id() are useful here.

## DB Tasks
Tasks in this block are built upon the Flights database:
https://postgrespro.ru/education/demodb. Choose the database size based on the space
availability.
### Task D4
Restore the price information for each flight based on the past bookings, and build the
pricing rule table that determines the prices for all upcoming flights.
### Task D5
Design the RESTful web service to handle the following requests:
- List all the available source and destination cities
- List all the available source and destination airports
- List the airports within a city
- List the inbound schedule for an airport:
- Days of week
- Time of arrival
- Flight no
- Origin
- List the outbound schedule for an airport:
- Days of week
- Time of departure
- Flight no
- Destination
- List the routes connecting two points
- Point might be either an airport or a city. In the latter case we should search
for the flights connecting any airports within the city
- The mandatory “departure date” parameter limits the flights by the ones
departing between 0:00:00 of the specified date and 0:00:00 of the next date
- The “booking class” parameter should be one of the 'Economy',
'Comfort', 'Business'
- Additional parameter limits the number of connections: 0 (direct), 1, 2, 3,
unbound
- Create a booking for a selected route for a single passenger
- Online check-in for a flight
### Task D6
Implement the RESTful web service described above. Consider adding the appropriate
indexes to make the requests reasonably fast.
