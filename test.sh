export VCAP_SERVICES='{"p-mysql": [{"credentials": {"jdbcUrl": "jdbc:mysql://127.0.0.1:3306/albums?user=root"}, "name": "albums-mysql"}, {"credentials": {"jdbcUrl": "jdbc:mysql://127.0.0.1:3306/movies?user=root"}, "name": "movies-mysql"}]}'
mvn spring-boot:run
