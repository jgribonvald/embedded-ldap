# embedded-ldap
Example for unit test with embedded ldap (ApacheDS)

to debug schema import or ldap init run in debug apacheDS with theses commands :
 mvn test -Dcom.unboundid.ldap.sdk.debug.enabled=true -Dcom.unboundid.ldap.sdk.debug.leve=ALL
