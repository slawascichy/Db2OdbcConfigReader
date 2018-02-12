package pro.ibpm.gs.db2odbcconfigreader;

public class JdbcConfigData {

	private String port;

	private String hostName;

	private String database;

	private String uid;

	private String pwd;

	private String description;

	private String name;

	private String dbAlias;

	public String getDbAlias() {
		return dbAlias;
	}

	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "JdbcConfigData [\n\tname=" + name + "\n\tdbAlias=" + dbAlias
				+ "\n\thostName=" + hostName + "\n\tport=" + port
				+ "\n\tdatabase=" + database + "\n\tuid=" + uid
				+ "\n\tpwd=****************" + "\n\tdescription=" + description
				+ "\n]";
	}

	public JdbcConfigData() {
		super();
	}

	public JdbcConfigData(String port, String hostName, String database,
			String uid, String pwd, String description, String name,
			String dbAlias) {
		super();
		this.port = port;
		this.hostName = hostName;
		this.database = database;
		this.uid = uid;
		this.pwd = pwd;
		this.description = description;
		this.name = name;
		this.dbAlias = dbAlias;
	}

	/**
	 * Czy konfiguracja jest kompletna. Konfiguracja jest kompletna wtedy gdy na
	 * podstawie zawartych w obiekcie konfiguracji wartości jesteśmy wstanie
	 * nawiązać połączenie do instancji bazy danych.
	 * 
	 * @return
	 */
	public boolean isComplete() {
		return (database != null && hostName != null && port != null
				&& uid != null && pwd != null);
	}
}
