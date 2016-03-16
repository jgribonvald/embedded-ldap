package org.esco.test.ldap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.junit.rules.ExternalResource;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;

@Slf4j
@Getter
@Setter
@ToString
public class LdapServerRule extends ExternalResource {

	public static final String DefaultDn = "cn=Directory Manager";
	public static final String DefaultPassword = "password";

	private String baseDn;
	private String dn;
	private String password;
	private String lDiffPath;
	private String schemaFilePath;
	private boolean validateSchema;
	private int listenPort;
	private InMemoryDirectoryServer server;

	public LdapServerRule(String baseDn, String lDiffPath, int listenPort, boolean validateSchema, String schemaFilePath) {
		this.lDiffPath = lDiffPath;
		this.baseDn = baseDn;
		this.dn = DefaultDn;
		this.password = DefaultPassword;
		this.listenPort = listenPort;
		this.schemaFilePath = schemaFilePath;
		this.validateSchema = validateSchema;
	}

	@Override
	protected void before() {
		start();
	}

	@Override
	protected void after() {
		stop();
	}

	public int getRunningPort() {
		return getServer().getListenPort();
	}

	public String getRunningAdresse() throws LDAPException {
		return getServer().getConnection().getConnectedAddress();
	}

	private void start() {
		InMemoryDirectoryServerConfig config;

		try {
			log.info("LDAP server " + toString() + " starting...");
			config = new InMemoryDirectoryServerConfig(getBaseDn());
			config.addAdditionalBindCredentials(getDn(), getPassword());
			if (!validateSchema) {
				config.setSchema(null);
			} else if (schemaFilePath != null) {
				config.setSchema(Schema.mergeSchemas(Schema.getDefaultStandardSchema(),
						Schema.getSchema(schemaFilePath)));
			} else {
				config.setSchema(Schema.getDefaultStandardSchema());
			}
			config.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("LDAP", getListenPort()));
			setServer(new InMemoryDirectoryServer(config));
			getServer().importFromLDIF(true, getLDiffPath());
			getServer().startListening();
			log.info("LDAP server " + toString() + " started. Listen on port " + getServer().getListenPort()
					+ " and address " + getRunningAdresse());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void stop() {
		server.shutDown(true);
		log.info("LDAP server " + toString() + " stopped");
	}
}
