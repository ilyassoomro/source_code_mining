package viewplugin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.dbcp.AbandonedObjectPool;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;

public class Connector {
	
	private static HashMap<String, String> connectionPoolURLMap = new HashMap<String, String>();    
    private static String driver = "org.gjt.mm.mysql.Driver";
    
	//for dbcp connection pool params:
    private static int maxWait;
    private static int maxActive;
    private static int maxIdle;
    private static boolean testOnBorrow;
    private static boolean testOnReturn;
    private static boolean testWhileIdle;

    private static PoolingDataSource dataSource;

    public static Connection getConnection(String url) {
    	String poolURL = null;
    	if(connectionPoolURLMap.containsKey(url)){
    		poolURL = connectionPoolURLMap.get(url);
    	}else{
    		poolURL = getConnectionPoolURL(url);
    		connectionPoolURLMap.put(url, poolURL);
    		initPool(url, poolURL);
    	}
    	
        Connection cnxn = null;
        try {
        	 cnxn = DriverManager.getConnection(poolURL);
        }
        catch(Exception e) {
            Logger.error( e.getMessage(), e );
        }
        return cnxn;
    }

    public static String getDBProtocol(String url){
    	return url.split(":")[0]+":"+url.split(":")[1];
    }
    public static String getServer(String url){
    	return url.split(":")[2].replaceAll("/", "");
    }
    public static String getPort(String url){
    	return url.split(":")[3].substring(0, url.split(":")[3].indexOf("/"));
    }
    public static String getDBName(String url){
    	String str = url.split(":")[3];
    	return str.substring(str.indexOf("/")+1, str.indexOf("?"));
    }
    public static String getUser(String url){
    	String[] params = url.split("\\?")[1].split("&");
    	for(String param: params){
    		String[] p = param.split("=");
    		if(p[0].equals("user")){
    			return p[1];
    		}
    	}
    	return "";
    }
    
    public static String getPassword(String url){
    	String[] params = url.split("\\?")[1].split("&");
    	for(String param: params){
    		String[] p = param.split("=");
    		if(p[0].equals("password")){
    			return p[1];
    		}
    	}
    	return "";
    }
    
    private static void initPool(String url, String poolURL){
    	try {
    		String dbProtocol = getDBProtocol(url);
    		String server = getServer(url);
    		String port = getPort(url);
    		String dbname = getDBName(url);
    		String user = getUser(url);
    		String password = getPassword(url);
    		
    		
            //ConnectionCount = 0;
            //String validationQuery = "select 1";
            String validationQuery = null;//MT#0013304: POS Terminal Performance Improvements
            Class mmsql = Class.forName(driver);

            AbandonedConfig aConfig = new AbandonedConfig();
            aConfig.setLogAbandoned(true);
            aConfig.setRemoveAbandoned(true);
            AbandonedObjectPool connectionPool = new AbandonedObjectPool(null, aConfig);

            //set the connection pool's properties from the conf file
            connectionPool.setMaxWait(maxWait);
            connectionPool.setMaxActive(maxActive);
            connectionPool.setMaxIdle(maxIdle);
            connectionPool.setTestOnBorrow(testOnBorrow);
            connectionPool.setTestWhileIdle(testWhileIdle);
            connectionPool.setTestOnReturn(testOnReturn);
            connectionPool.setWhenExhaustedAction(AbandonedObjectPool.WHEN_EXHAUSTED_GROW);

            DriverManagerConnectionFactory connectionFactory =
                    new DriverManagerConnectionFactory(dbProtocol + "://" + server + ":" + port + "/" + dbname+"?zeroDateTimeBehavior=convertToNull&jdbcCompliantTruncation=false",
                    		user, password);
            PoolableConnectionFactory poolableConnectionFactory =
                    new PoolableConnectionFactory(connectionFactory, connectionPool, null,
                            validationQuery, false, true);
            PoolingDriver driver = new PoolingDriver();
            driver.setAccessToUnderlyingConnectionAllowed(true);
            driver.registerPool(poolURL, connectionPool);

            Logger.debug("\tConnection Pool Created \n\t\t\t\tDB Server : " + server +
                    "\n\t\t\t\tDB : " + dbname +
                    "\n\t\t\t\tLOGIN : " + user);
        }
        catch(Exception e) {
            Logger.error(e);
            e.printStackTrace();
        }
    }
    /**
     * @return String URL of connection pool that can be passed to DriverManager to retrieve a connection from pool
     */
    public static String getConnectionPoolURL(String url) {
        return "jdbc:apache:commons:dbcp:" + connectionPoolURLMap.get(url);
    }
    
    public static void releaseConnection(Connection cnxn) {
        try {        	
        	cnxn.close();
        }
        catch(Exception e) {
            Logger.error( e.getMessage(), e );
            
        }
    }
   

    private static void printPoolStatus() {
        PoolingDriver driver = new PoolingDriver();

        ObjectPool gop = driver.getPool("northstar");
        if(gop != null) {
            Logger.debug("=== Connection Pool: northstar:  Active: " +
                    gop.getNumActive() + " Idle: " + gop.getNumIdle() + " ===");
        }

    }
    public static int getActiveConnectionCount(){
        int count = 0;
        try{
            PoolingDriver driver = new PoolingDriver();
            ObjectPool gop = driver.getPool("northstar");
            if(gop != null) {
                count = gop.getNumActive();
            }
        }catch(Exception e){
            Logger.error(e);
        }
        Logger.debug("Active Connections="+count);
        return count;
    }
    
    public static int getIdleConnectionCount(){
        int count = 0;
        try{
            PoolingDriver driver = new PoolingDriver();
            ObjectPool gop = driver.getPool("northstar");
            if(gop != null) {
                count = gop.getNumIdle();
            }
        }catch(Exception e){
            Logger.error(e);
        }
        Logger.debug("Idle Connections="+count);
        return count;
    }
    
}
