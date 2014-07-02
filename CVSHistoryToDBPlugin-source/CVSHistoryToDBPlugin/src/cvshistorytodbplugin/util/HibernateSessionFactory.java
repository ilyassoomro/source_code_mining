package cvshistorytodbplugin.util;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.ClassicAvgFunction;
import org.hibernate.dialect.function.ClassicCountFunction;
import org.hibernate.dialect.function.ClassicSumFunction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import cvshistorytodbplugin.model.db.Workspace;

/**
 * Configures and provides access to Hibernate sessions, tied to the
 * current thread of execution.  Follows the Thread Local Session
 * pattern, see {@link http://hibernate.org/42.html }.
 */
public class HibernateSessionFactory {

	private static HashMap<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();
	
    /**
     * Location of hibernate.cfg.xml file.
     * Location should be on the classpath as Hibernate uses
     * #resourceAsStream style lookup for its configuration file.
     * The default classpath location of the hibernate config file is
     * in the default package. Use #setConfigFile() to update
     * the location of the configuration file for the current session.
     */
    private static String CONFIG_FILE_LOCATION = "../hbm/hibernate.cfg.xml";
    private static final ThreadLocal threadLocal = new ThreadLocal();
    
    private HibernateSessionFactory() {
    }

    /**
     * Returns the ThreadLocal Session instance.  Lazy initialize
     * the <code>SessionFactory</code> if needed.
     *
     *  @return Session
     *  @throws HibernateException
     */
    public static Session getSession(String dbConfig, IProgressMonitor monitor) throws HibernateException {
        Session session = (Session)threadLocal.get();

        if(session == null || !session.isOpen()) {
        	//monitor = Policy.subMonitorFor(monitor,10);
    		monitor.setTaskName("Opening new Database Session: ");
    		
            if(!sessionFactoryMap.containsKey(dbConfig)) {
                rebuildSessionFactory(dbConfig, monitor);
            }
            SessionFactory sessionFactory =  sessionFactoryMap.get(dbConfig);
			session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.COMMIT);
			threadLocal.set(session);
			//monitor.done();
        }

        return session;
    }

    private static String getConnectionURL(String dbConfig){
    	String dbProtocol = Connector.getDBProtocol(dbConfig);
        String serverName = Connector.getServer(dbConfig);
        String port = Connector.getPort(dbConfig);
        return dbProtocol+"://"+serverName+":"+port;
    }
    
    public static void rebuildSessionFactory(String dbConfig, IProgressMonitor monitor){
        try {
        	//monitor = Policy.subMonitorFor(monitor,20);
    		//monitor.beginTask("Building Session Factory: ", 100);
    		monitor.setTaskName("Retreiving Database Connection Setting");
    		
    		String dbName = Connector.getDBName(dbConfig);
    		String login = Connector.getUser(dbConfig);
            String password = Connector.getPassword(dbConfig);
            
            String hibernateConnectionURL = getConnectionURL(dbConfig)+"/"+dbName;
            Configuration configuration = new Configuration();
            
            //monitor.worked(30);
            monitor.setTaskName("Configuring Hibernate mappings");
            
            configuration.configure(HibernateSessionFactory.class.getResource(CONFIG_FILE_LOCATION));
            
            //monitor.worked(80);
            monitor.setTaskName("Building Session Factory");
            
            configuration.setProperty("hibernate.connection.url",hibernateConnectionURL);
            configuration.setProperty("hibernate.connection.username",login);
            configuration.setProperty("hibernate.connection.password",password);
            
            configuration.addSqlFunction( "count", new ClassicCountFunction()); 
            configuration.addSqlFunction( "avg", new ClassicAvgFunction()); 
            configuration.addSqlFunction( "sum", new ClassicSumFunction());            
            assureDatabaseExists(dbConfig, configuration);
            
            //monitor.done();
        }
        catch(Exception e) { 
            Logger.error(e, "%%%% Error Creating SessionFactory %%%%");
            throw new RuntimeException(e);
        }
    }

    /**
     *  Close the single hibernate session instance.
     *
     *  @throws HibernateException
     */
    public static void closeSession() throws HibernateException {
        Session session = (Session)threadLocal.get();
        threadLocal.set(null);

        if(session != null && session.isOpen()) {
            session.close();
        }
    }

    private static void assureDatabaseExists(String dbConfig, Configuration configuration) throws Exception{
    	SessionFactory sessionFactory = null;
    	try{
    		sessionFactory = configuration.buildSessionFactory();
    		Session session = sessionFactory.openSession();
	    	Query query =session.createQuery("from Workspace");
	    	Workspace value = (Workspace)query.uniqueResult();
    	}catch(Exception e){
    		Logger.error("Database not exists for dbConfig: "+dbConfig);
    		Logger.error("Creating Database schema...");
    		// If error while selection, mean db not exist, need to create it
    		String hibernateConnectionURL = getConnectionURL(dbConfig);
    		String dbName = Connector.getDBName(dbConfig);
    		configuration.setProperty("hibernate.connection.url",hibernateConnectionURL);
    		configuration.setProperty(Environment.DEFAULT_SCHEMA,dbName );
    		sessionFactory = configuration.buildSessionFactory();
    		Session session=sessionFactory.openSession();
    		SQLQuery query = session.createSQLQuery("CREATE SCHEMA `"+dbName+"`");
    		query.executeUpdate();
    		SchemaExport schema = new SchemaExport(configuration);
    	    schema.create(true, true);
    	}
    	sessionFactoryMap.put(dbConfig, sessionFactory);
    }

 }
