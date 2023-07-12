# ISA-Tasks
Tasks for Info Storage &amp; Analysis course



## Java Tasks

### Задание №1
Напишите программу, которая создаёт нить. Родительская и вновь созданная нити должны распечатать десять строк текста.


### Задание №2
Модифицируйте программу из “Задания №1” так, чтобы вывод родительской нити производился после завершения дочерней.

### Задание №3
Напишите программу, которая создаёт четыре нити, исполняющие один и тот же метод.
Этот метод должен распечатать последовательность текстовых строк, переданных как параметр. Каждая из созданных нитей должна распечатать различные последовательности строк.

### Задание №4
Дочерняя нить должна распечатывать текст на экран. Через две секунды после создания дочерней нити, родительская нить должна прервать её.

### Задание №5
Модифицируйте программу из “Задания 4” так, чтобы дочерняя нить перед завершением распечатывала сообщение об этом.

### Задание №6
В приложении №1 находится код программы, которую вам необходимо доработать.

Легенда:
1. Класс Company характеризует компанию, разделённую на отделы.
2. Каждый отдел (класс Department) умеет вычислять сумму от 1 до n, где n — это рандомное число от 1 до 6.
3. Каждая итерация суммирования занимает 1 секунду. Поэтому: если n равно 3, то на вычисление суммы 0 + 1 + 2 уйдёт 3 секунды (метод performCalculations).

Вам необходимо дописать реализацию класса Founder, в котором:
1. У вас будет находиться список со всеми Worker’ами (Runnable).
2. За каждым Worker’ом должен быть закреплён свой Department.
3. Каждый Worker должен инициировать запуск вычислений.
4. После того как во всех нитях завершатся вычисления, нужно вывести результат (метод showCollaborativeResult)
Примечание:
Следует разобраться как работают барьеры в Java.

### Задание №7
Напишите программу, которая вычисляет число Пи при помощи ряда Лейбница (рис. 1).
Количество потоков программы должно определяться параметром командной строки.
Количество итераций может определяться во время компиляции.

### Задание №8
Модифицируйте программу из “Задания №7” так, чтобы сама по себе она не завершалась.
Вместо этого, после получения сигнала SIGINT программа должна как можно скорее завершаться, собирать частичные суммы ряда и выводить полученное приближение числа.

Рекомендации:
Ожидаемое решение состоит в том, что вы установите обработчик SIGINT. Подумайте, как минимизировать ошибку, обусловленную тем, что разные потоки к моменту завершения успели пройти разное количество итераций. Скорее всего, такая минимизация должна обеспечиваться за счет увеличения времени между получением сигнала и выходом.

### Задание №9
Напишите программу, которая будет симулировать известную задачу про обедающих философов. Пять философов сидят за круглым столом и едят спагетти. Спагетти едят при помощи двух вилок. Каждые двое философов, сидящих рядом, пользуются общей вилкой. Философ некоторое время размышляет, потом пытается взять вилки и принимается за еду. Съев некоторое количество спагетти, философ освобождает вилки и снова начинает размышлять. Еще через некоторое время он снова принимается за еду, и т.д., пока спагетти не кончатся. Если одну из вилок взять не получается, философ ждет, пока она освободится. Как вариант реализации: философы симулируются при помощи нитей, периоды размышлений и еды – при помощи засыпаний, а вилки – при помощи мьютексов. Философы всегда берут сначала левую вилку, а потом правую.

Дополнительно:
Объясните, при каких обстоятельствах это может приводить к мертвой блокировке.
Измените протокол взаимодействия философов с вилками таким образом, чтобы мертвых
блокировок не происходило.

### Задание №10
Модифицируйте программу из “Задания №1” так, чтобы вывод родительской и дочерней
нитей был синхронизирован: сначала родительская нить выводила первую строку, затем
дочерняя, затем родительская вторую строку и т.д. Используйте мьютексы.

Примечание:
Явные и неявные передачи управления между нитями и холостые циклы разрешается
использовать только на этапе инициализации.

### Задание №11
Решите “Задание №10” с использованием двух семафоров-счетчиков.

### Задание №12
Родительская нить программы должна считывать вводимые пользователем строки
и помещать их в начало связанного списка. Строки длиннее 80 символов можно разрезать
на несколько строк. При вводе пустой строки программа должна выдавать текущее
состояние списка. Дочерняя нить пробуждается каждые пять секунд и сортирует список
в лексикографическом порядке (используйте пузырьковую сортировку).

Примечание:
Все операции над списком должны синхронизоваться при помощи синхронизованных
блоков на объектах головы.

### Задание №13
Решите задачу из “Задания №9” при помощи атомарного захвата вилок. Когда философ
может взять одну вилку, но не может взять другую, он должен положить вилку на стол
и ждать, пока освободятся обе вилки.
Рекомендация: создайте еще мьютекс forks и условную переменную. При попытке взять
вилку философ должен захватывать forks и проверять доступность обоих вилок. Если одна
из вилок недоступна, философ должен освободить вторую вилку (если он успел
ее захватить) и заснуть на условной переменной. Освобождая вилки, философ должен
оповещать остальных философов об этом при помощи условной переменной. Тщательно
продумайте процедуру захвата и освобождения мьютексов, чтобы избежать ошибок
потерянного пробуждения.

### Задание №14
Разработайте имитатор производственной линии, изготавливающей винтики (widget).
Винтик собирается из детали C и модуля, который, в свою очередь, состоит из деталей A и
B. Для изготовления детали A требуется 1 секунда, В – две секунды, С – три секунды.
Задержку изготовления деталей имитируйте при помощи засыпания. Используйте
семафоры-счётчики.

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
