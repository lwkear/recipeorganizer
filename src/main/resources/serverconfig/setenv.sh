# -----------------------------------------------------------------------------
# Environment variables script for the CATALINA Server
# -----------------------------------------------------------------------------
#JAVA_OPTS=" $JAVA_OPTS -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=256m -Djava.awt.headless=true -Djavax.net.debug=ssl -Djavax.net.ssl.keyStore=/usr/local/tomcat/keystore/recipeorganizer.jks -Djavax.net.ssl.keyStorePassword=evanston -Djavax.net.ssl.keyAlias=tomcat -Djavax.net.ssl.keyPass=evanston "
#JAVA_OPTS=" $JAVA_OPTS -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=256m -Djava.awt.headless=true -Djavax.net.debug=all "
JAVA_OPTS=" $JAVA_OPTS -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=256m -Djava.awt.headless=true "
export JAVA_OPTS
