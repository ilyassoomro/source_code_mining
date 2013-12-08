package cvshistorytodbplugin.util;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.function.ClassicAvgFunction;
import org.hibernate.dialect.function.ClassicCountFunction;
import org.hibernate.dialect.function.ClassicSumFunction;

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
    private static String CONFIG_FILE_LOCATION = "./plugins/cvsHistoryToDB/hibernate.cfg.xml";
    private static final ThreadLocal threadLocal = new ThreadLocal();
    private static File configFile = new File(CONFIG_FILE_LOCATION);

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
        	monitor = Policy.monitorFor(monitor);
    		monitor.beginTask("Opening new Database Session: ", 100);
    		
            if(!sessionFactoryMap.containsKey(dbConfig)) {
                rebuildSessionFactory(dbConfig, monitor);
            }
            SessionFactory sessionFactory =  sessionFactoryMap.get(dbConfig);
			session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.COMMIT);
			threadLocal.set(session);
			monitor.done();
        }

        return session;
    }

    public static void rebuildSessionFactory(String dbConfig, IProgressMonitor monitor) {
        try {
        	monitor = Policy.monitorFor(monitor);
    		monitor.beginTask("Building Session Factory: ", 100);
    		monitor.setTaskName("Retreiving Database Connection Setting");
    		
            String dbProtocol = Connector.getDBProtocol(dbConfig);
            String serverName = Connector.getServer(dbConfig);
            String port = Connector.getPort(dbConfig);
            String dbName = Connector.getDBName(dbConfig);
            String login = Connector.getUser(dbConfig);
            String password = Connector.getPassword(dbConfig);
            String hibernateConnectionURL = dbProtocol+"://"+serverName+":"+port+"/"+dbName;
            Configuration configuration = new Configuration();
            
            monitor.worked(30);
            monitor.setTaskName("Configuring Hibernate mappings");
            
            configuration.configure(configFile);
            
            monitor.worked(80);
            monitor.setTaskName("Building Session Factory");
            
            configuration.setProperty("hibernate.connection.url",hibernateConnectionURL);
            configuration.setProperty("hibernate.connection.username",login);
            configuration.setProperty("hibernate.connection.password",password);
            configuration.addSqlFunction( "count", new ClassicCountFunction()); 
            configuration.addSqlFunction( "avg", new ClassicAvgFunction()); 
            configuration.addSqlFunction( "sum", new ClassicSumFunction());            
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            sessionFactoryMap.put(dbConfig, sessionFactory);
            
            monitor.done();
        }
        catch(Exception e) { 
            Logger.error(e, "%%%% Error Creating SessionFactory %%%%");
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

 }
