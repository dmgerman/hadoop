begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.minikdc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|minikdc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|KrbException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|server
operator|.
name|KdcConfigKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|kerberos
operator|.
name|kerb
operator|.
name|server
operator|.
name|SimpleKdcServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|util
operator|.
name|IOUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kerby
operator|.
name|util
operator|.
name|NetworkUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FileInputStream
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
name|InputStreamReader
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Mini KDC based on Apache Directory Server that can be embedded in testcases  * or used from command line as a standalone KDC.  *<p>  *<b>From within testcases:</b>  *<p>  * MiniKdc sets one System property when started and un-set when stopped:  *<ul>  *<li>sun.security.krb5.debug: set to the debug value provided in the  *   configuration</li>  *</ul>  * Because of this, multiple MiniKdc instances cannot be started in parallel.  * For example, running testcases in parallel that start a KDC each. To  * accomplish this a single MiniKdc should be used for all testcases running  * in parallel.  *<p>  * MiniKdc default configuration values are:  *<ul>  *<li>org.name=EXAMPLE (used to create the REALM)</li>  *<li>org.domain=COM (used to create the REALM)</li>  *<li>kdc.bind.address=localhost</li>  *<li>kdc.port=0 (ephemeral port)</li>  *<li>instance=DefaultKrbServer</li>  *<li>max.ticket.lifetime=86400000 (1 day)</li>  *<li>max.renewable.lifetime=604800000 (7 days)</li>  *<li>transport=TCP</li>  *<li>debug=false</li>  *</ul>  * The generated krb5.conf forces TCP connections.  */
end_comment

begin_class
DECL|class|MiniKdc
specifier|public
class|class
name|MiniKdc
block|{
DECL|field|JAVA_SECURITY_KRB5_CONF
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_SECURITY_KRB5_CONF
init|=
literal|"java.security.krb5.conf"
decl_stmt|;
DECL|field|SUN_SECURITY_KRB5_DEBUG
specifier|public
specifier|static
specifier|final
name|String
name|SUN_SECURITY_KRB5_DEBUG
init|=
literal|"sun.security.krb5.debug"
decl_stmt|;
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|4
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Arguments:<WORKDIR><MINIKDCPROPERTIES> "
operator|+
literal|"<KEYTABFILE> [<PRINCIPALS>]+"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|workDir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Specified work directory does not exists: "
operator|+
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|Properties
name|conf
init|=
name|createConf
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Specified configuration does not exists: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|Properties
name|userConf
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|InputStreamReader
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|r
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|userConf
operator|.
name|load
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|userConf
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|MiniKdc
name|miniKdc
init|=
operator|new
name|MiniKdc
argument_list|(
name|conf
argument_list|,
name|workDir
argument_list|)
decl_stmt|;
name|miniKdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|File
name|krb5conf
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"krb5.conf"
argument_list|)
decl_stmt|;
if|if
condition|(
name|miniKdc
operator|.
name|getKrb5conf
argument_list|()
operator|.
name|renameTo
argument_list|(
name|krb5conf
argument_list|)
condition|)
block|{
name|File
name|keytabFile
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|String
index|[]
name|principals
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
operator|-
literal|3
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|args
argument_list|,
literal|3
argument_list|,
name|principals
argument_list|,
literal|0
argument_list|,
name|args
operator|.
name|length
operator|-
literal|3
argument_list|)
expr_stmt|;
name|miniKdc
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|principals
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Standalone MiniKdc Running"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Realm           : "
operator|+
name|miniKdc
operator|.
name|getRealm
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Running at      : "
operator|+
name|miniKdc
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|miniKdc
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  krb5conf        : "
operator|+
name|krb5conf
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  created keytab  : "
operator|+
name|keytabFile
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  with principals : "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|principals
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" Do<CTRL-C> or kill<PID> to stop it"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|miniKdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot rename KDC's krb5conf to "
operator|+
name|krb5conf
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MiniKdc
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ORG_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ORG_NAME
init|=
literal|"org.name"
decl_stmt|;
DECL|field|ORG_DOMAIN
specifier|public
specifier|static
specifier|final
name|String
name|ORG_DOMAIN
init|=
literal|"org.domain"
decl_stmt|;
DECL|field|KDC_BIND_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|KDC_BIND_ADDRESS
init|=
literal|"kdc.bind.address"
decl_stmt|;
DECL|field|KDC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|KDC_PORT
init|=
literal|"kdc.port"
decl_stmt|;
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|String
name|INSTANCE
init|=
literal|"instance"
decl_stmt|;
DECL|field|MAX_TICKET_LIFETIME
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TICKET_LIFETIME
init|=
literal|"max.ticket.lifetime"
decl_stmt|;
DECL|field|MIN_TICKET_LIFETIME
specifier|public
specifier|static
specifier|final
name|String
name|MIN_TICKET_LIFETIME
init|=
literal|"min.ticket.lifetime"
decl_stmt|;
DECL|field|MAX_RENEWABLE_LIFETIME
specifier|public
specifier|static
specifier|final
name|String
name|MAX_RENEWABLE_LIFETIME
init|=
literal|"max.renewable.lifetime"
decl_stmt|;
DECL|field|TRANSPORT
specifier|public
specifier|static
specifier|final
name|String
name|TRANSPORT
init|=
literal|"transport"
decl_stmt|;
DECL|field|DEBUG
specifier|public
specifier|static
specifier|final
name|String
name|DEBUG
init|=
literal|"debug"
decl_stmt|;
DECL|field|PROPERTIES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PROPERTIES
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_CONFIG
specifier|private
specifier|static
specifier|final
name|Properties
name|DEFAULT_CONFIG
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|PROPERTIES
operator|.
name|add
argument_list|(
name|ORG_NAME
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|ORG_DOMAIN
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|KDC_BIND_ADDRESS
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|KDC_BIND_ADDRESS
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|KDC_PORT
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|INSTANCE
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|TRANSPORT
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|MAX_TICKET_LIFETIME
argument_list|)
expr_stmt|;
name|PROPERTIES
operator|.
name|add
argument_list|(
name|MAX_RENEWABLE_LIFETIME
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|KDC_BIND_ADDRESS
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|KDC_PORT
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|INSTANCE
argument_list|,
literal|"DefaultKrbServer"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|ORG_NAME
argument_list|,
literal|"EXAMPLE"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|ORG_DOMAIN
argument_list|,
literal|"COM"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|TRANSPORT
argument_list|,
literal|"TCP"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|MAX_TICKET_LIFETIME
argument_list|,
literal|"86400000"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|MAX_RENEWABLE_LIFETIME
argument_list|,
literal|"604800000"
argument_list|)
expr_stmt|;
name|DEFAULT_CONFIG
operator|.
name|setProperty
argument_list|(
name|DEBUG
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convenience method that returns MiniKdc default configuration.    *<p>    * The returned configuration is a copy, it can be customized before using    * it to create a MiniKdc.    * @return a MiniKdc default configuration.    */
DECL|method|createConf ()
specifier|public
specifier|static
name|Properties
name|createConf
parameter_list|()
block|{
return|return
operator|(
name|Properties
operator|)
name|DEFAULT_CONFIG
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|field|conf
specifier|private
name|Properties
name|conf
decl_stmt|;
DECL|field|simpleKdc
specifier|private
name|SimpleKdcServer
name|simpleKdc
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|realm
specifier|private
name|String
name|realm
decl_stmt|;
DECL|field|workDir
specifier|private
name|File
name|workDir
decl_stmt|;
DECL|field|krb5conf
specifier|private
name|File
name|krb5conf
decl_stmt|;
DECL|field|transport
specifier|private
name|String
name|transport
decl_stmt|;
DECL|field|krb5Debug
specifier|private
name|boolean
name|krb5Debug
decl_stmt|;
DECL|method|setTransport (String transport)
specifier|public
name|void
name|setTransport
parameter_list|(
name|String
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
block|}
comment|/**    * Creates a MiniKdc.    *    * @param conf MiniKdc configuration.    * @param workDir working directory, it should be the build directory. Under    * this directory an ApacheDS working directory will be created, this    * directory will be deleted when the MiniKdc stops.    * @throws Exception thrown if the MiniKdc could not be created.    */
DECL|method|MiniKdc (Properties conf, File workDir)
specifier|public
name|MiniKdc
parameter_list|(
name|Properties
name|conf
parameter_list|,
name|File
name|workDir
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|conf
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|PROPERTIES
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|missingProperties
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|PROPERTIES
argument_list|)
decl_stmt|;
name|missingProperties
operator|.
name|removeAll
argument_list|(
name|conf
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing configuration properties: "
operator|+
name|missingProperties
argument_list|)
throw|;
block|}
name|this
operator|.
name|workDir
operator|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|workDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|this
operator|.
name|workDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot create directory "
operator|+
name|this
operator|.
name|workDir
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Configuration:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"---------------------------------------------------------------"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|conf
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"  {}: {}"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"---------------------------------------------------------------"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|conf
operator|.
name|getProperty
argument_list|(
name|KDC_PORT
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|orgName
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|ORG_NAME
argument_list|)
decl_stmt|;
name|String
name|orgDomain
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|ORG_DOMAIN
argument_list|)
decl_stmt|;
name|realm
operator|=
name|orgName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|+
literal|"."
operator|+
name|orgDomain
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the port of the MiniKdc.    *    * @return the port of the MiniKdc.    */
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/**    * Returns the host of the MiniKdc.    *    * @return the host of the MiniKdc.    */
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|conf
operator|.
name|getProperty
argument_list|(
name|KDC_BIND_ADDRESS
argument_list|)
return|;
block|}
comment|/**    * Returns the realm of the MiniKdc.    *    * @return the realm of the MiniKdc.    */
DECL|method|getRealm ()
specifier|public
name|String
name|getRealm
parameter_list|()
block|{
return|return
name|realm
return|;
block|}
DECL|method|getKrb5conf ()
specifier|public
name|File
name|getKrb5conf
parameter_list|()
block|{
name|krb5conf
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|JAVA_SECURITY_KRB5_CONF
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|krb5conf
return|;
block|}
comment|/**    * Starts the MiniKdc.    *    * @throws Exception thrown if the MiniKdc could not be started.    */
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|simpleKdc
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Already started"
argument_list|)
throw|;
block|}
name|simpleKdc
operator|=
operator|new
name|SimpleKdcServer
argument_list|()
expr_stmt|;
name|prepareKdcServer
argument_list|()
expr_stmt|;
name|simpleKdc
operator|.
name|init
argument_list|()
expr_stmt|;
name|resetDefaultRealm
argument_list|()
expr_stmt|;
name|simpleKdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniKdc started."
argument_list|)
expr_stmt|;
block|}
DECL|method|resetDefaultRealm ()
specifier|private
name|void
name|resetDefaultRealm
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|templateResource
init|=
operator|new
name|FileInputStream
argument_list|(
name|getKrb5conf
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|IOUtil
operator|.
name|readInput
argument_list|(
name|templateResource
argument_list|)
decl_stmt|;
name|content
operator|=
name|content
operator|.
name|replaceAll
argument_list|(
literal|"default_realm = .*\n"
argument_list|,
literal|"default_realm = "
operator|+
name|getRealm
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|IOUtil
operator|.
name|writeFile
argument_list|(
name|content
argument_list|,
name|getKrb5conf
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareKdcServer ()
specifier|private
name|void
name|prepareKdcServer
parameter_list|()
throws|throws
name|Exception
block|{
comment|// transport
name|simpleKdc
operator|.
name|setWorkDir
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|simpleKdc
operator|.
name|setKdcHost
argument_list|(
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|simpleKdc
operator|.
name|setKdcRealm
argument_list|(
name|realm
argument_list|)
expr_stmt|;
if|if
condition|(
name|transport
operator|==
literal|null
condition|)
block|{
name|transport
operator|=
name|conf
operator|.
name|getProperty
argument_list|(
name|TRANSPORT
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|port
operator|==
literal|0
condition|)
block|{
name|port
operator|=
name|NetworkUtil
operator|.
name|getServerPort
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|transport
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|transport
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"TCP"
argument_list|)
condition|)
block|{
name|simpleKdc
operator|.
name|setKdcTcpPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|simpleKdc
operator|.
name|setAllowUdp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transport
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"UDP"
argument_list|)
condition|)
block|{
name|simpleKdc
operator|.
name|setKdcUdpPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|simpleKdc
operator|.
name|setAllowTcp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid transport: "
operator|+
name|transport
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Need to set transport!"
argument_list|)
throw|;
block|}
name|simpleKdc
operator|.
name|getKdcConfig
argument_list|()
operator|.
name|setString
argument_list|(
name|KdcConfigKey
operator|.
name|KDC_SERVICE_NAME
argument_list|,
name|conf
operator|.
name|getProperty
argument_list|(
name|INSTANCE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|getProperty
argument_list|(
name|DEBUG
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|krb5Debug
operator|=
name|getAndSet
argument_list|(
name|SUN_SECURITY_KRB5_DEBUG
argument_list|,
name|conf
operator|.
name|getProperty
argument_list|(
name|DEBUG
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|conf
operator|.
name|getProperty
argument_list|(
name|MIN_TICKET_LIFETIME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|simpleKdc
operator|.
name|getKdcConfig
argument_list|()
operator|.
name|setLong
argument_list|(
name|KdcConfigKey
operator|.
name|MINIMUM_TICKET_LIFETIME
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|conf
operator|.
name|getProperty
argument_list|(
name|MIN_TICKET_LIFETIME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|conf
operator|.
name|getProperty
argument_list|(
name|MAX_TICKET_LIFETIME
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|simpleKdc
operator|.
name|getKdcConfig
argument_list|()
operator|.
name|setLong
argument_list|(
name|KdcConfigKey
operator|.
name|MAXIMUM_TICKET_LIFETIME
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|conf
operator|.
name|getProperty
argument_list|(
name|MiniKdc
operator|.
name|MAX_TICKET_LIFETIME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Stops the MiniKdc    */
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|simpleKdc
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|simpleKdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KrbException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|conf
operator|.
name|getProperty
argument_list|(
name|DEBUG
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|SUN_SECURITY_KRB5_DEBUG
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|krb5Debug
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|delete
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Will be fixed in next Kerby version.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniKdc stopped."
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (File f)
specifier|private
name|void
name|delete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"WARNING: cannot delete file "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|File
index|[]
name|fileList
init|=
name|f
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileList
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|c
range|:
name|fileList
control|)
block|{
name|delete
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"WARNING: cannot delete directory "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Creates a principal in the KDC with the specified user and password.    *    * @param principal principal name, do not include the domain.    * @param password password.    * @throws Exception thrown if the principal could not be created.    */
DECL|method|createPrincipal (String principal, String password)
specifier|public
specifier|synchronized
name|void
name|createPrincipal
parameter_list|(
name|String
name|principal
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
name|simpleKdc
operator|.
name|createPrincipal
argument_list|(
name|principal
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates multiple principals in the KDC and adds them to a keytab file.    *    * @param keytabFile keytab file to add the created principals.    * @param principals principals to add to the KDC, do not include the domain.    * @throws Exception thrown if the principals or the keytab file could not be    * created.    */
DECL|method|createPrincipal (File keytabFile, String ... principals)
specifier|public
specifier|synchronized
name|void
name|createPrincipal
parameter_list|(
name|File
name|keytabFile
parameter_list|,
name|String
modifier|...
name|principals
parameter_list|)
throws|throws
name|Exception
block|{
name|simpleKdc
operator|.
name|createPrincipals
argument_list|(
name|principals
argument_list|)
expr_stmt|;
if|if
condition|(
name|keytabFile
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|keytabFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to delete keytab file: "
operator|+
name|keytabFile
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|principal
range|:
name|principals
control|)
block|{
name|simpleKdc
operator|.
name|getKadmin
argument_list|()
operator|.
name|exportKeytab
argument_list|(
name|keytabFile
argument_list|,
name|principal
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the System property; return the old value for caching.    *    * @param sysprop property    * @param debug true or false    * @return the previous value    */
DECL|method|getAndSet (String sysprop, String debug)
specifier|private
name|boolean
name|getAndSet
parameter_list|(
name|String
name|sysprop
parameter_list|,
name|String
name|debug
parameter_list|)
block|{
name|boolean
name|old
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|sysprop
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|sysprop
argument_list|,
name|debug
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
block|}
end_class

end_unit

