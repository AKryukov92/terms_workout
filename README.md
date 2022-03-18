# Сфинкс
Инструмент, который научит студента читать и анализировать код.

Тестируемый получает вопрос и фрагмент текста. Для ответа нужно выделить часть текста во фрагменте.

В отличие от вопросов с вариантами ответа, угадать правильный ответ сложнее. Чем больше фрагмент, тем больше возможных вариантов ответа.

Типы возможных заданий:
* Какой фрагмент соответствует термину из программирования?
* Какой фрагмент соответствует алгоритму на естественном языке?
* Какая команда будет работать при конкретных исходных данных?
* Какая область видимости у переменной?

## Запуск
Для запуска требуется JRE 1.8.

* Скачайте и распакуйте [архив](https://github.com/AKryukov92/terms_workout/releases/download/v0.0.5-alpha/0.0.5-alpha.zip). Например в папку "Сфинкс".
  * В папке Сфинкс должна содержаться папка resources, в ней - папка haystacks.
  * В папке haystacks должны лежать файлы с расширением .xml. Один из файлов должен называться meta.xml.
  * В папке Сфинкс должны также содержаться файлы run.bat, Сфинкс.url и terms_workout-0.0.5-alpha.jar
* На Windows запустите "run.bat". При этом:
  * Возможно сработает антивирус и брандмауэр. Разрешить работу этой программы.
  * Откроется консоль
  * Будет запущен веб-сервер, занимающий порт 8080.
  * Будет создан файл log.txt
* Откройте ссылку 'Сфинкс.url".
  * Будет открыт стандартный браузер по адресу "localhost:8080/home"

ВНИМАНИЕ: В данный момент все файлы заданий и ответов находятся в открытом виде в папке haystacks.
Чтобы скрыть ответы от студента, обеспечьте доступ к вашему веб-серверу по локальной сети.

## Создание заданий
Чтобы приступить к созданию заданий, нужно на главной странице перейти по адресу "Создать новое задание".
Эта страница требует авторизации. Имя пользователя riddler, пароль - riddler.

## Самостоятельная сборка
Для сборки требуется JDK 1.8, maven 3, подключение к Интернету.

Чтобы подготовить проект к запуску:
1. Выполните команду "mvn clean package".
2. Копируйте target/terms_workout-0.0.5-alpha.jar в целевую директорию.
3. Копируйте все файлы из директории "resources/haystacks", а также "Сфинкс.url" и "run.bat" в целевую директорию.
