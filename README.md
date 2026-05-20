Многопоточная система обработки заказов (Task Manager)

Это консольная Java-программа для учебного задания. Она имитирует работу склада: заказы создаются, попадают в очередь, обрабатываются несколькими потоками и сохраняются в коллекцию.

Как запустить программу

1. Нужны Java 17 и Maven.
2. Откройте терминал в папке проекта.
3. Выполните:

mvn compile exec:java -Dexec.mainClass="ru.student.taskmanager.Main"

Или:

mvn package
java -jar target/task-manager-1.0-SNAPSHOT.jar

Как запустить тесты

mvn test

Что есть в проекте

- Custom annotations: @Validate и @OrderType на полях класса Order.
- OrderValidator проверяет заказ через Reflection.
- BlockingQueue (LinkedBlockingQueue) — очередь заказов между Producer и Consumer.
- ConcurrentHashMap — потокобезопасное хранение обработанных заказов.
- Несколько потоков: OrderProducer создаёт заказы, OrderConsumer обрабатывает их через ExecutorService.

Структура

src/main/java/ru/student/taskmanager — основной код
src/test/java/ru/student/taskmanager — unit-тесты JUnit 5
