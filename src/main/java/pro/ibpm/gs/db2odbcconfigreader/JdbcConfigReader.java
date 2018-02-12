package pro.ibpm.gs.db2odbcconfigreader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcConfigReader {

	private static Logger logger = LoggerFactory
			.getLogger(JdbcConfigReader.class);

	private static JdbcConfigReader instance;
	private static final Object instanceLock = new Object();

	private final String configFileName;
	private final Map<String, JdbcConfigData> db2ConfigsMap = new Hashtable<>();
	private static final String PORT = "PORT";
	private static final String HOSTNAME = "HOSTNAME";
	private static final String DATABASE = "DATABASE";
	private static final String DBALIAS = "DBALIAS";
	private static final String PWD = "PWD";
	private static final String UID = "UID";
	private static final String DESCRIPTION = "DESCRIPTION";

	private JdbcConfigReader(String configFileName) {
		this.configFileName = configFileName;
	}

	public static JdbcConfigReader getInstance() {
		synchronized (instanceLock) {
			return instance;
		}
	}

	public static JdbcConfigReader getInstance(String configFileName) {
		synchronized (instanceLock) {
			if (instance == null) {
				instance = new JdbcConfigReader(configFileName);
				instance.init();
			}
			return instance;
		}
	}

	private void init() {
		try {
			logger.info("--> init: Reading configuration from {}",
					configFileName);
			loadJdbcConfigData(getClass(), this.configFileName);
		} finally {

		}
	}

	private void loadJdbcConfigData(Class<?> clazz, String fileName) {
		Map<String, Map<String, String>> envVars = new Hashtable<String, Map<String, String>>();
		BufferedReader d = null;
		try {
			d = getBufferedReader(clazz, fileName);
			String inputLine;
			String currConfigName = null;

			while ((inputLine = d.readLine()) != null) {
				/* pętla po liniach - START */
				if ((!(inputLine.startsWith("#"))) && (inputLine.length() > 0)) {
					/* czytanie linii - START */
					if (inputLine.startsWith("[")) {
						/* wycinam nawiasy kwadratowe */
						currConfigName = inputLine.substring(1,
								inputLine.length() - 1);
						logger.debug(
								"-->loadJdbcConfigData: mam konfigurację {}",
								currConfigName);
					} else if (currConfigName != null) {
						Map<String, String> configProps = envVars
								.get(currConfigName);
						if (configProps == null) {
							configProps = new Hashtable<>();
							envVars.put(currConfigName, configProps);
						}
						int comaPos = inputLine.indexOf(61);
						if (comaPos > 0) {
							String key = inputLine.substring(0, comaPos)
									.toUpperCase();
							String value = "";
							if (comaPos < inputLine.length()) {
								value = inputLine.substring(comaPos + 1);
							}
							configProps.put(key, value);
						}
					}
					/* czytanie linii - KONIEC */
				}
				/* pętla po liniach - KONIEC */
			}

			Set<Entry<String, Map<String, String>>> entrySet = envVars
					.entrySet();
			Map<String, JdbcConfigData> db2CompleteConfigsMapByAlias = new Hashtable<>();
			Map<String, JdbcConfigData> db2NotCompleteConfigsMap = new Hashtable<>();
			for (Entry<String, Map<String, String>> entry : entrySet) {
				currConfigName = entry.getKey();
				Map<String, String> props = entry.getValue();

				/* czytam alias bazy danych */
				String dbAlias = props.get(DBALIAS);
				Map<String, String> aliasProps = null;
				if (dbAlias != null) {
					aliasProps = readDbAliasProps(envVars, props,
							currConfigName);
				}
				/* czytam nazwę hosta */
				String hostName = props.get(HOSTNAME);
				if (hostName == null && aliasProps != null) {
					hostName = aliasProps.get(HOSTNAME);
				}
				/* czytam nazwę bazy */
				String database = props.get(DATABASE);
				if (database == null && aliasProps != null) {
					database = aliasProps.get(DATABASE);
				}
				/* czytam numer portu */
				String port = props.get(PORT);
				if (port == null && aliasProps != null) {
					port = aliasProps.get(PORT);
				}
				/* czytam nazwę użtkownika */
				String uid = props.get(UID);
				if (uid == null && aliasProps != null) {
					uid = aliasProps.get(UID);
				}
				/* czytam hasło użytkownika */
				String pwd = props.get(PWD);
				if (pwd == null && aliasProps != null) {
					pwd = aliasProps.get(PWD);
				}
				/* czytam opis */
				String description = props.get(DESCRIPTION);

				JdbcConfigData data = new JdbcConfigData(port, hostName,
						database, uid, pwd, description, currConfigName,
						dbAlias);
				this.db2ConfigsMap.put(currConfigName, data);
				if (data.isComplete()) {
					if (dbAlias != null) {
						db2CompleteConfigsMapByAlias.put(dbAlias, data);
					}
				} else {
					db2NotCompleteConfigsMap.put(currConfigName, data);
				}
			}
			if (!db2NotCompleteConfigsMap.isEmpty()) {
				/* są konfiguracje niekompletne - START */
				for (Entry<String, JdbcConfigData> entry : db2NotCompleteConfigsMap
						.entrySet()) {
					/* uzupełnianie niekompletnych danych - START */
					JdbcConfigData data = entry.getValue();
					if (data.getDbAlias() != null) {
						/* Pobieram dane kompletne */
						JdbcConfigData completeData = db2CompleteConfigsMapByAlias
								.get(data.getDbAlias());
						if (completeData != null) {
							/* Mam dane kompletne odpowiadające aliasowi */
							/* weryfikuję nazwę hosta */
							String hostName = data.getHostName();
							if (hostName == null) {
								data.setHostName(completeData.getHostName());
							}
							/* weryfikuję nazwę bazy */
							String database = data.getDatabase();
							if (database == null) {
								data.setDatabase(completeData.getDatabase());
							}
							/* weryfikuję numer portu */
							String port = data.getPort();
							if (port == null) {
								data.setPort(completeData.getPort());
							}
							/* weryfikuję nazwę użtkownika */
							String uid = data.getUid();
							if (uid == null) {
								data.setUid(completeData.getUid());
							}
							/* weryfikuję hasło użytkownika */
							String pwd = data.getPwd();
							if (pwd == null) {
								data.setPwd(completeData.getPwd());
							}
						}
					}
					if (!data.isComplete()) {
						logger.warn(
								"-->loadJdbcConfigData: nie udało się załadować kompletnej konfiguracji {}",
								data.toString());
					}
					/* uzupełnianie niekompletnych danych - KONIEC */
				}
				/* są konfiguracje niekompletne - KONIEC */
			}
			if (logger.isDebugEnabled()) {
				for (Entry<String, JdbcConfigData> entry : this.db2ConfigsMap
						.entrySet()) {
					JdbcConfigData data = entry.getValue();
					logger.debug(
							"-->loadJdbcConfigData: załadowałem {} konfigurację {}",
							new Object[] {
									data.isComplete() ? "kompletną"
											: "NIE kompletną", data.toString() });
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (d != null)
					d.close();
			} catch (IOException e) {
				/* ignore */
			}
		}
	}

	private Map<String, String> readDbAliasProps(
			Map<String, Map<String, String>> envVars,
			Map<String, String> currProps, String currConfigName) {
		Map<String, String> aliasProps = null;
		String currAlias = currProps.get(DBALIAS);
		logger.debug(
				"-->readDbAliasProps: analiza aliasu '{}' dla konfiguracji '{}'",
				new Object[] { currAlias, currConfigName });
		if (currAlias == null
				|| (currAlias != null && currAlias
						.equalsIgnoreCase(currConfigName))) {
			return currProps;
		}
		aliasProps = envVars.get(currAlias);
		return readDbAliasProps(envVars, aliasProps, currAlias);
	}

	private static BufferedReader getBufferedReader(Class<?> clazz,
			String fileName) throws FileNotFoundException {
		BufferedReader d = null;
		URL resource = clazz.getResource(fileName);
		if (resource == null) {
			FileInputStream fis = new FileInputStream(fileName);
			try {
				d = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		} else {
			try {
				d = new BufferedReader(new InputStreamReader(
						clazz.getResourceAsStream(fileName), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return d;
	}

	public JdbcConfigData getJdbcConfigData(String name) {
		return this.db2ConfigsMap.get(name);
	}
}
