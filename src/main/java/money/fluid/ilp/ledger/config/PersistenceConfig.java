package money.fluid.ilp.ledger.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@ComponentScan({
        "money.fluid.ilp.ledger.datastore",
        "money.fluid.ledger.datastore"
})
@EnableJpaRepositories({
        "money.fluid.ilp.ledger.datastore",
        "money.fluid.ledger.datastore"
})
@EntityScan({
        "money.fluid.ilp.ledger.datastore",
        "money.fluid.ledger.datastore"
})
@EnableTransactionManagement
public class PersistenceConfig {

//    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
//    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
//    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
//    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
//
//    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
//    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
//    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
//    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
//    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

//    @Resource
//    private Environment environment;

    @Bean
    public DataSource dataSource() {

        // TODO: This exists only for testing, but should probably not exist in the final product...
        // no need to set shutdown, EmbeddedDatabaseFactoryBean will take care of this
        final EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("db/sql/create-tables.sql")
                .addScript("db/sql/insert-data.sql")
                .build();
        return db;
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
//            final DataSource dataSource
//    ) throws ClassNotFoundException {
//        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//
//        entityManagerFactoryBean.setDataSource(dataSource);
//        //entityManagerFactoryBean.setPackagesToScan(environment.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
//        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
//
//        //entityManagerFactoryBean.setPersistenceUnitName("spring-jpa");
//
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//        vendorAdapter.setShowSql(true);
//        vendorAdapter.setGenerateDdl(true);
//        //vendorAdapter.setDatabase();
//
//
//        Properties jpaProterties = new Properties();
//        jpaProterties.put(
//                PROPERTY_NAME_HIBERNATE_DIALECT, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
//        jpaProterties.put(
//                PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
//                environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL)
//        );
//        jpaProterties.put(
//                PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
//                environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY)
//        );
//        jpaProterties.put(
//                PROPERTY_NAME_HIBERNATE_SHOW_SQL, environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
//
//        entityManagerFactoryBean.setJpaProperties(jpaProterties);
//
//        return entityManagerFactoryBean;
//    }
//
//    @Bean
//    public EntityManagerFactory entityManagerFactory(final DataSource dataSource) {
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("money.fluid.ilp.ledger.datastore", "money.fluid.ledger.datastore");
//        factory.setDataSource(dataSource);
//        factory.afterPropertiesSet();
//        //factory.setJpaProperties();
//
//        return factory.getObject();
//    }
//

//    /**
//     * Declare the transaction manager.
//     */
//    @Bean
//    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
//        final JpaTransactionManager txManager = new JpaTransactionManager();
//        txManager.setEntityManagerFactory(entityManagerFactory);
//        return txManager;
//    }

//    /**
//     * PersistenceExceptionTranslationPostProcessor is a bean post processor
//     * which adds an advisor to any bean annotated with Repository so that any
//     * platform-specific exceptions are caught and then rethrown as one
//     * Spring's unchecked data access exceptions (i.e. a subclass of
//     * DataAccessException).
//     */
//    @Bean
//    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//        return new PersistenceExceptionTranslationPostProcessor();
//    }
}
