package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    @Bean
    public DatabaseServiceCredentials getDatabaseServiceCredentials(@Value("${vcap.services}") String vcapServices){
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource 
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        hibernateJpaVendorAdapter.setGenerateDdl(Boolean.TRUE);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory(DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean moviesEntityManagerFactory =  new LocalContainerEntityManagerFactoryBean();
        moviesEntityManagerFactory.setDataSource(moviesDataSource);
        moviesEntityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        moviesEntityManagerFactory.setPackagesToScan("org.superbiz.moviefun.movies");
        moviesEntityManagerFactory.setPersistenceUnitName("movies_unit");
        return moviesEntityManagerFactory;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean albumsEntityManagerFactory =  new LocalContainerEntityManagerFactoryBean();
        albumsEntityManagerFactory.setDataSource(albumsDataSource);
        albumsEntityManagerFactory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        albumsEntityManagerFactory.setPackagesToScan("org.superbiz.moviefun.albums");
        albumsEntityManagerFactory.setPersistenceUnitName("albums_unit");
        return albumsEntityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory moviesEntityManagerFactory){
        JpaTransactionManager moviesPlatformTransactionManager = new JpaTransactionManager();
        moviesPlatformTransactionManager.setEntityManagerFactory(moviesEntityManagerFactory);
        return moviesPlatformTransactionManager;
    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory albumsEntityManagerFactory){
        JpaTransactionManager albumsPlatformTransactionManager = new JpaTransactionManager();
        albumsPlatformTransactionManager.setEntityManagerFactory(albumsEntityManagerFactory);
        return albumsPlatformTransactionManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
