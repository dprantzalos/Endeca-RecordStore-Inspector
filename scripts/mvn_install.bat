@echo off
setlocal

set ENDECA_VERSION=11.1.0
set ENDECA_CAS_HOME=C:/Endeca/CAS/%ENDECA_VERSION%
set ENDECA_TOOLS_HOME=C:/Endeca/ToolsAndFrameworks/%ENDECA_VERSION%

REM Install Endeca compile dependencies
REM
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/component-manager-api/component-manager-api-%ENDECA_VERSION%.jar" -DgroupId=com.endeca -DartifactId=component-manager-api -Dversion=%ENDECA_VERSION% -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-api/itl-api-common-%ENDECA_VERSION%.jar" -DgroupId=com.endeca -DartifactId=itl-api-common -Dversion=%ENDECA_VERSION% -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-api/recordstore-api-%ENDECA_VERSION%.jar" -DgroupId=com.endeca -DartifactId=recordstore-api -Dversion=%ENDECA_VERSION% -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-api/service-locator-%ENDECA_VERSION%.jar" -DgroupId=com.endeca -DartifactId=service-locator -Dversion=%ENDECA_VERSION% -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-api/slf4j-api-1.5.2.jar" -DgroupId=org.slf4j -DartifactId=slf4j-api -Dversion=1.5.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="../lib/jxl.jar" -DgroupId=net.sourceforge.jexcelapi -DartifactId=jxl -Dversion=2.6.12 -Dpackaging=jar -DgeneratePom=true

REM Install Endeca runtime dependencies
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/commons-codec-1.3.jar" -DgroupId=commons-codec -DartifactId=commons-codec -Dversion=1.3 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/commons-httpclient-3.1.jar" -DgroupId=commons-httpclient -DartifactId=commons-httpclient -Dversion=3.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/commons-io-1.4.jar" -DgroupId=commons-io -DartifactId=commons-io -Dversion=1.4 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/log4j-1.2.15.jar" -DgroupId=log4j -DartifactId=log4j -Dversion=1.2.15 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/slf4j-log4j12-1.5.2.jar" -DgroupId=org.slf4j -DartifactId=slf4j-log4j12 -Dversion=1.5.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/itl-common-%ENDECA_VERSION%.jar" -DgroupId=com.endeca -DartifactId=itl-common -Dversion=%ENDECA_VERSION% -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-api-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-api -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-bindings-soap-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-bindings-soap -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-core-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-core -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-databinding-jaxb-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-databinding-jaxb -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-frontend-jaxws-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-frontend-jaxws -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-frontend-simple-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-frontend-simple -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/cxf-rt-transports-http-2.6.1.jar" -DgroupId=org.apache.cxf -DartifactId=cxf-rt-transports-http -Dversion=2.6.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/FastInfoset-1.2.7.jar" -DgroupId=com.sun.xml.fastinfoset -DartifactId=FastInfoset -Dversion=1.2.7 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/google-collections-1.0.jar" -DgroupId=com.google.collections -DartifactId=google-collections -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/http-client-11.1.1.jar" -DgroupId=com.oracle -DartifactId=http_client -Dversion=11.1.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/jaxb-impl-2.1.12.jar" -DgroupId=com.sun.xml.bind -DartifactId=jaxb-impl -Dversion=2.1.12 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/orawsdl-11.1.1-PATCH-13336266.jar" -DgroupId=com.oracle.webservices -DartifactId=orawsdl -Dversion=11.1.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/stax2-api-3.1.1.jar" -DgroupId=org.codehaus.woodstox -DartifactId=stax2-api -Dversion=3.1.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/woodstox-core-asl-4.1.1.jar" -DgroupId=org.codehaus.woodstox -DartifactId=woodstox-core-asl -Dversion=4.1.1 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_CAS_HOME%/lib/recordstore-cmd/xmlschema-core-2.0.2.jar" -DgroupId=org.apache.ws.xmlschema  -DartifactId=xmlschema-core -Dversion=2.0.2 -Dpackaging=jar -DgeneratePom=true
call mvn install:install-file -Dfile="%ENDECA_TOOLS_HOME%/admin/lib/commons-logging-1.1.1.jar" -DgroupId=commons-logging -DartifactId=commons-logging -Dversion=1.1.1 -Dpackaging=jar -DgeneratePom=true

endlocal
