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

### Setup Environment (Ubuntu 18.04.3 (LTS) x64):
1. Create project dir:
    ```bash
    mkdir /onemap
    cd /onemap
    ```
2. Download, extract and configure JDK: 
    ```bash
    wget https://download.java.net/java/GA/jdk13.0.2/d4173c853231432d94f001e99d882ca7/8/GPL/openjdk-13.0.2_linux-x64_bin.tar.gz
    tar xvfz openjdk-13.0.2_linux-x64_bin.tar.gz
    vi ~/.bashrc
    export JAVA_HOME="/onemap/jdk-13.0.2"
    export PATH="$JAVA_HOME/bin:$PATH"
    ```
3. Download and configure Maven:
    ```bash
    wget http://apache.ip-connect.vn.ua/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip
    unzip apache-maven-3.6.3-bin.zip
    vi ~/.bashrc
    export M2_HOME="/onemap/apache-maven-3.6.3"
    export PATH="$M2_HOME/bin:$PATH"
    ```
4. Install `PostgreSQL-12` and `PostGIS-2.5`
    ```bash
    # Import the repository key from https://www.postgresql.org/media/keys/ACCC4CF8.asc:
    sudo apt-get install curl ca-certificates gnupg
    curl https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
    
    # Create /etc/apt/sources.list.d/pgdg.list. The distributions are called codename-pgdg. In the example, replace buster with the actual distribution you are using:
    # (You may determine the codename of your distribution by running lsb_release -c).
    # For a shorthand version of the above, presuming you are using a supported release:
    # deb http://apt.postgresql.org/pub/repos/apt bionic-pgdg main
    sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

    # Finally, update the package lists, and start installing packages:
    sudo apt-get update
    sudo apt install postgresql-12
    # Success. You can now start the database server using: pg_ctlcluster 12 main start
    # PostgresSQL installation directory: /var/lib/postgresql/12/main
    sudo apt install postgresql-12-postgis-2.5
    ```