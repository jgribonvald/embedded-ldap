package org.esco.test.ldap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author GIP RECIA 2016 - Julien Gribonvald
 *
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ldapContext.xml")
public class LdapDaoTest {

	private static int port = 42539;
	private static String defaultPartitionSuffix = "dc=esco-centre,dc=fr";

	@Autowired
	private LdapTemplate ldapTemplate;

	@ClassRule
	public static final LdapServerRule LDAP_RULE = new LdapServerRule(defaultPartitionSuffix, ClassLoader
			.getSystemResource("init_test.ldif").getPath(), LdapDaoTest.port, true, ClassLoader.getSystemResource(
			"eduMember.ldif").getPath());

	@Test
	public void testFindAllUsers() throws Exception {
		assertNotNull(ldapTemplate);
		List result = ldapTemplate.search("ou=people," + defaultPartitionSuffix, "(objectClass=person)",
				new ContextMapper() {
					public Object mapFromContext(Object arg0) {
						DirContextAdapter ctx = (DirContextAdapter) arg0;
						return ctx.getNameInNamespace();
					}
				});
		log.info("returned all users result is {}", result.toString());
		assertTrue("The returned result must contains 3 entries", result.size() == 3);
	}

	@Test
	public void testFindAllGroups() throws Exception {
		assertNotNull(ldapTemplate);
		List result = ldapTemplate.search("ou=groups," + defaultPartitionSuffix, "(objectClass=eduMember)",
				new ContextMapper() {
					public Object mapFromContext(Object arg0) {
						DirContextAdapter ctx = (DirContextAdapter) arg0;
						return ctx.getNameInNamespace();
					}
				});
		log.info("returned all groups result is {}", result.toString());
		assertTrue("The returned result must contains 8 entries", result.size() == 8);
	}
}
