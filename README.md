# onemap.md

This is an online map application (like google.maps or open.street.map).  
It uses raw OSM data and can render any dataset in mercator projection.  
Typical tile size is 512x512px, and it can be configured to any size and zoom level.

The backend is in plain Java + PostGIS.  
Frontent is based on Leaflet.

The project is running on [onemap.md](http://onemap.md)

### Как запустить проект:
1. Установить JDK 8
    1. Скачать JDK 8 и установить любым удобным способом
    2. Создать системную переменную `JAVA_HOME=путь_к_папке_с_jdk`
    3. Добавить `JAVA_HOME/bin` в системную переменную `PATH`
2. Установить `maven`
    1. Скачать и распаковать свежий архив с `maven`
    2. Создать переменую `M2_HOME=путь_к_папке_с_maven`
    3. Добавить `M2_HOME/bin` в `PATH`
3. Установить свежий `PostgreSQL + PostGIS`
    1. `login: postgres`
    2. `pass: postgres`
4. Сгерерировать БД из OSM файла
    1. Проверить настройки в `application.properties`
    2. Запустить `OsmToPostgisExporter` в IDEA
    3. Дождаться окончания импорта
5. Запустить проект: `mvn clean install jetty:run`
6. Открыть карту: `localhost:8080/onemap`
 