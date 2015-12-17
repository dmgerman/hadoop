begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSRESTConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
operator|.
name|SslSocketConnectorSecure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ThreadUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Connector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|SslSocketConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_class
DECL|class|MiniKMS
specifier|public
class|class
name|MiniKMS
block|{
DECL|method|createJettyServer (String keyStore, String password, int inPort)
specifier|private
specifier|static
name|Server
name|createJettyServer
parameter_list|(
name|String
name|keyStore
parameter_list|,
name|String
name|password
parameter_list|,
name|int
name|inPort
parameter_list|)
block|{
try|try
block|{
name|boolean
name|ssl
init|=
name|keyStore
operator|!=
literal|null
decl_stmt|;
name|String
name|host
init|=
literal|"localhost"
decl_stmt|;
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
name|inPort
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ssl
condition|)
block|{
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SslSocketConnector
name|c
init|=
operator|new
name|SslSocketConnectorSecure
argument_list|()
decl_stmt|;
name|c
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|c
operator|.
name|setNeedClientAuth
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeystore
argument_list|(
name|keyStore
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeystoreType
argument_list|(
literal|"jks"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setKeyPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|c
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not start embedded servlet container, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|getJettyURL (Server server)
specifier|private
specifier|static
name|URL
name|getJettyURL
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
name|boolean
name|ssl
init|=
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getClass
argument_list|()
operator|==
name|SslSocketConnectorSecure
operator|.
name|class
decl_stmt|;
try|try
block|{
name|String
name|scheme
init|=
operator|(
name|ssl
operator|)
condition|?
literal|"https"
else|:
literal|"http"
decl_stmt|;
return|return
operator|new
name|URL
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getLocalPort
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"It should never happen, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|kmsConfDir
specifier|private
name|File
name|kmsConfDir
decl_stmt|;
DECL|field|log4jConfFile
specifier|private
name|String
name|log4jConfFile
decl_stmt|;
DECL|field|keyStoreFile
specifier|private
name|File
name|keyStoreFile
decl_stmt|;
DECL|field|keyStorePassword
specifier|private
name|String
name|keyStorePassword
decl_stmt|;
DECL|field|inPort
specifier|private
name|int
name|inPort
decl_stmt|;
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{
name|kmsConfDir
operator|=
operator|new
name|File
argument_list|(
literal|"target/test-classes"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|log4jConfFile
operator|=
literal|"kms-log4j.properties"
expr_stmt|;
block|}
DECL|method|setKmsConfDir (File confDir)
specifier|public
name|Builder
name|setKmsConfDir
parameter_list|(
name|File
name|confDir
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|confDir
argument_list|,
literal|"KMS conf dir is NULL"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|confDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"KMS conf dir does not exist"
argument_list|)
expr_stmt|;
name|kmsConfDir
operator|=
name|confDir
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLog4jConfFile (String log4jConfFile)
specifier|public
name|Builder
name|setLog4jConfFile
parameter_list|(
name|String
name|log4jConfFile
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|log4jConfFile
argument_list|,
literal|"log4jconf file is NULL"
argument_list|)
expr_stmt|;
name|this
operator|.
name|log4jConfFile
operator|=
name|log4jConfFile
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPort (int port)
specifier|public
name|Builder
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|port
operator|>
literal|0
argument_list|,
literal|"input port must be greater than 0"
argument_list|)
expr_stmt|;
name|this
operator|.
name|inPort
operator|=
name|port
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSslConf (File keyStoreFile, String keyStorePassword)
specifier|public
name|Builder
name|setSslConf
parameter_list|(
name|File
name|keyStoreFile
parameter_list|,
name|String
name|keyStorePassword
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keyStoreFile
argument_list|,
literal|"keystore file is NULL"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keyStorePassword
argument_list|,
literal|"keystore password is NULL"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|keyStoreFile
operator|.
name|exists
argument_list|()
argument_list|,
literal|"keystore file does not exist"
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyStoreFile
operator|=
name|keyStoreFile
expr_stmt|;
name|this
operator|.
name|keyStorePassword
operator|=
name|keyStorePassword
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|MiniKMS
name|build
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|kmsConfDir
operator|.
name|exists
argument_list|()
argument_list|,
literal|"KMS conf dir does not exist"
argument_list|)
expr_stmt|;
return|return
operator|new
name|MiniKMS
argument_list|(
name|kmsConfDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|log4jConfFile
argument_list|,
operator|(
name|keyStoreFile
operator|!=
literal|null
operator|)
condition|?
name|keyStoreFile
operator|.
name|getAbsolutePath
argument_list|()
else|:
literal|null
argument_list|,
name|keyStorePassword
argument_list|,
name|inPort
argument_list|)
return|;
block|}
block|}
DECL|field|kmsConfDir
specifier|private
name|String
name|kmsConfDir
decl_stmt|;
DECL|field|log4jConfFile
specifier|private
name|String
name|log4jConfFile
decl_stmt|;
DECL|field|keyStore
specifier|private
name|String
name|keyStore
decl_stmt|;
DECL|field|keyStorePassword
specifier|private
name|String
name|keyStorePassword
decl_stmt|;
DECL|field|jetty
specifier|private
name|Server
name|jetty
decl_stmt|;
DECL|field|inPort
specifier|private
name|int
name|inPort
decl_stmt|;
DECL|field|kmsURL
specifier|private
name|URL
name|kmsURL
decl_stmt|;
DECL|method|MiniKMS (String kmsConfDir, String log4ConfFile, String keyStore, String password, int inPort)
specifier|public
name|MiniKMS
parameter_list|(
name|String
name|kmsConfDir
parameter_list|,
name|String
name|log4ConfFile
parameter_list|,
name|String
name|keyStore
parameter_list|,
name|String
name|password
parameter_list|,
name|int
name|inPort
parameter_list|)
block|{
name|this
operator|.
name|kmsConfDir
operator|=
name|kmsConfDir
expr_stmt|;
name|this
operator|.
name|log4jConfFile
operator|=
name|log4ConfFile
expr_stmt|;
name|this
operator|.
name|keyStore
operator|=
name|keyStore
expr_stmt|;
name|this
operator|.
name|keyStorePassword
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|inPort
operator|=
name|inPort
expr_stmt|;
block|}
DECL|method|copyResource (String inputResourceName, File outputFile)
specifier|private
name|void
name|copyResource
parameter_list|(
name|String
name|inputResourceName
parameter_list|,
name|File
name|outputFile
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|ThreadUtil
operator|.
name|getResourceAsStream
argument_list|(
name|inputResourceName
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KMSConfiguration
operator|.
name|KMS_CONFIG_DIR
argument_list|,
name|kmsConfDir
argument_list|)
expr_stmt|;
name|File
name|aclsFile
init|=
operator|new
name|File
argument_list|(
name|kmsConfDir
argument_list|,
literal|"kms-acls.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|aclsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|copyResource
argument_list|(
literal|"mini-kms-acls-default.xml"
argument_list|,
name|aclsFile
argument_list|)
expr_stmt|;
block|}
name|File
name|coreFile
init|=
operator|new
name|File
argument_list|(
name|kmsConfDir
argument_list|,
literal|"core-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|coreFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Configuration
name|core
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|coreFile
argument_list|)
decl_stmt|;
name|core
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|File
name|kmsFile
init|=
operator|new
name|File
argument_list|(
name|kmsConfDir
argument_list|,
literal|"kms-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|kmsFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Configuration
name|kms
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|kms
operator|.
name|set
argument_list|(
name|KMSConfiguration
operator|.
name|KEY_PROVIDER_URI
argument_list|,
literal|"jceks://file@"
operator|+
operator|new
name|Path
argument_list|(
name|kmsConfDir
argument_list|,
literal|"kms.keystore"
argument_list|)
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|kms
operator|.
name|set
argument_list|(
literal|"hadoop.kms.authentication.type"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|kmsFile
argument_list|)
decl_stmt|;
name|kms
operator|.
name|writeXml
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
literal|"log4j.configuration"
argument_list|,
name|log4jConfFile
argument_list|)
expr_stmt|;
name|jetty
operator|=
name|createJettyServer
argument_list|(
name|keyStore
argument_list|,
name|keyStorePassword
argument_list|,
name|inPort
argument_list|)
expr_stmt|;
comment|// we need to do a special handling for MiniKMS to work when in a dir and
comment|// when in a JAR in the classpath thanks to Jetty way of handling of webapps
comment|// when they are in the a DIR, WAR or JAR.
name|URL
name|webXmlUrl
init|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"kms-webapp/WEB-INF/web.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|webXmlUrl
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find kms-webapp/ dir in test classpath"
argument_list|)
throw|;
block|}
name|boolean
name|webXmlInJar
init|=
name|webXmlUrl
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
literal|".jar!/"
argument_list|)
decl_stmt|;
name|String
name|webappPath
decl_stmt|;
if|if
condition|(
name|webXmlInJar
condition|)
block|{
name|File
name|webInf
init|=
operator|new
name|File
argument_list|(
literal|"target/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/kms-webapp/WEB-INF"
argument_list|)
decl_stmt|;
name|webInf
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|webInf
argument_list|,
literal|"web.xml"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|copyResource
argument_list|(
literal|"kms-webapp/WEB-INF/web.xml"
argument_list|,
operator|new
name|File
argument_list|(
name|webInf
argument_list|,
literal|"web.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|webappPath
operator|=
name|webInf
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|webappPath
operator|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"kms-webapp"
argument_list|)
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
name|WebAppContext
name|context
init|=
operator|new
name|WebAppContext
argument_list|(
name|webappPath
argument_list|,
literal|"/kms"
argument_list|)
decl_stmt|;
if|if
condition|(
name|webXmlInJar
condition|)
block|{
name|context
operator|.
name|setClassLoader
argument_list|(
name|cl
argument_list|)
expr_stmt|;
block|}
name|jetty
operator|.
name|addHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|kmsURL
operator|=
operator|new
name|URL
argument_list|(
name|getJettyURL
argument_list|(
name|jetty
argument_list|)
argument_list|,
literal|"kms"
argument_list|)
expr_stmt|;
block|}
DECL|method|getKMSUrl ()
specifier|public
name|URL
name|getKMSUrl
parameter_list|()
block|{
return|return
name|kmsURL
return|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
operator|&&
name|jetty
operator|.
name|isRunning
argument_list|()
condition|)
block|{
try|try
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not stop MiniKMS embedded Jetty, "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

