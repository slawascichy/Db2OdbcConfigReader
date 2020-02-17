package pl.slawas.db2odbcconfigreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;
import pl.slawas.db2odbcconfigreader.JdbcConfigData;
import pl.slawas.db2odbcconfigreader.JdbcConfigReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcConfigReaderTest extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(JdbcConfigReaderTest.class);

	private static final String TEST_PROPERTIES_FILE = "target/test-classes/test.properties";
	private static final String ODBC_CONF_PROP = "odbc.config.file";

	private Properties readProperties() {

		Properties prop = new Properties();
		InputStream input = null;

		try {
			File configFile = new File(TEST_PROPERTIES_FILE);
			logger.info("configFile={}",
					new Object[] { configFile.getAbsolutePath() });

			input = new FileInputStream(configFile);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			logger.info(
					"{}={}",
					new Object[] { ODBC_CONF_PROP,
							prop.getProperty(ODBC_CONF_PROP) });

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	public void testReadFile() {
		JdbcConfigData data;
		Properties prop = readProperties();
		JdbcConfigReader reader = JdbcConfigReader.getInstance((String) prop
				.getProperty(ODBC_CONF_PROP));

		data = reader.getJdbcConfigData("MBRECORD");
		assertNotNull(data);
		assertEquals("50000", data.getPort());
		assertEquals("WARDB11", data.getHostName());
		assertEquals("MBRECORD", data.getDatabase());
		assertEquals("MBRECORD", data.getDbAlias());
		assertEquals("SRV_IIB_DB2_1", data.getUid());
		assertEquals("SRV_IIB_DB2_1.01", data.getPwd());
		
		data = reader.getJdbcConfigData("MBMPDB");
		assertNotNull(data);
		assertEquals("50000", data.getPort());
		assertEquals("WARDB11", data.getHostName());
		assertEquals("BRK1DBCT", data.getDatabase());
		assertEquals("BRK1DBCT", data.getDbAlias());
		assertEquals("SRV_IIB_DB2_1", data.getUid());
		assertEquals("SRV_IIB_DB2_1.01", data.getPwd());

		data = reader.getJdbcConfigData("BRK1DBCT_PORTAL");
		assertNotNull(data);
		assertEquals("50000", data.getPort());
		assertEquals("WARDB11", data.getHostName());
		assertEquals("BRK1DBCT", data.getDatabase());
		assertEquals("BRK1DBCT", data.getDbAlias());
		assertEquals("SRV_PR_DB2_1", data.getUid());
		assertEquals("SRV_PR_DB2_1.01", data.getPwd());

		data = reader.getJdbcConfigData("BRK1DBCT_MP");
		assertNotNull(data);
		assertEquals("50000", data.getPort());
		assertEquals("WARDB11", data.getHostName());
		assertEquals("BRK1DBCT", data.getDatabase());
		assertEquals("BRK1DBCT", data.getDbAlias());
		assertEquals("SRV_PM_DB2_1", data.getUid());
		assertEquals("SRV_PM_DB2_1.01", data.getPwd());

		data = reader.getJdbcConfigData("BRK1DBCT");
		assertNotNull(data);
		assertEquals("50000", data.getPort());
		assertEquals("WARDB11", data.getHostName());
		assertEquals("BRK1DBCT", data.getDatabase());
		assertEquals("BRK1DBCT", data.getDbAlias());
		assertEquals("SRV_IIB_DB2_1", data.getUid());
		assertEquals("SRV_IIB_DB2_1.01", data.getPwd());

	
	
	
	
	
	
	}
}
