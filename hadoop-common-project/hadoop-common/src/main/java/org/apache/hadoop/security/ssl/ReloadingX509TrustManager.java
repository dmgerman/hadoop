begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.ssl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|ssl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManagerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * A {@link TrustManager} implementation that reloads its configuration when  * the truststore file on disk changes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ReloadingX509TrustManager
specifier|public
specifier|final
class|class
name|ReloadingX509TrustManager
implements|implements
name|X509TrustManager
implements|,
name|Runnable
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReloadingX509TrustManager
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|RELOAD_ERROR_MESSAGE
specifier|static
specifier|final
name|String
name|RELOAD_ERROR_MESSAGE
init|=
literal|"Could not load truststore (keep using existing one) : "
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|password
specifier|private
name|String
name|password
decl_stmt|;
DECL|field|lastLoaded
specifier|private
name|long
name|lastLoaded
decl_stmt|;
DECL|field|reloadInterval
specifier|private
name|long
name|reloadInterval
decl_stmt|;
DECL|field|trustManagerRef
specifier|private
name|AtomicReference
argument_list|<
name|X509TrustManager
argument_list|>
name|trustManagerRef
decl_stmt|;
DECL|field|running
specifier|private
specifier|volatile
name|boolean
name|running
decl_stmt|;
DECL|field|reloader
specifier|private
name|Thread
name|reloader
decl_stmt|;
comment|/**    * Creates a reloadable trustmanager. The trustmanager reloads itself    * if the underlying trustore file has changed.    *    * @param type type of truststore file, typically 'jks'.    * @param location local path to the truststore file.    * @param password password of the truststore file.    * @param reloadInterval interval to check if the truststore file has    * changed, in milliseconds.    * @throws IOException thrown if the truststore could not be initialized due    * to an IO error.    * @throws GeneralSecurityException thrown if the truststore could not be    * initialized due to a security error.    */
DECL|method|ReloadingX509TrustManager (String type, String location, String password, long reloadInterval)
specifier|public
name|ReloadingX509TrustManager
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|password
parameter_list|,
name|long
name|reloadInterval
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|trustManagerRef
operator|=
operator|new
name|AtomicReference
argument_list|<
name|X509TrustManager
argument_list|>
argument_list|()
expr_stmt|;
name|trustManagerRef
operator|.
name|set
argument_list|(
name|loadTrustManager
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reloadInterval
operator|=
name|reloadInterval
expr_stmt|;
block|}
comment|/**    * Starts the reloader thread.    */
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
name|reloader
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
literal|"Truststore reloader thread"
argument_list|)
expr_stmt|;
name|reloader
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
name|reloader
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops the reloader thread.    */
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|reloader
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the reload check interval.    *    * @return the reload check interval, in milliseconds.    */
DECL|method|getReloadInterval ()
specifier|public
name|long
name|getReloadInterval
parameter_list|()
block|{
return|return
name|reloadInterval
return|;
block|}
annotation|@
name|Override
DECL|method|checkClientTrusted (X509Certificate[] chain, String authType)
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|X509Certificate
index|[]
name|chain
parameter_list|,
name|String
name|authType
parameter_list|)
throws|throws
name|CertificateException
block|{
name|X509TrustManager
name|tm
init|=
name|trustManagerRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|tm
operator|!=
literal|null
condition|)
block|{
name|tm
operator|.
name|checkClientTrusted
argument_list|(
name|chain
argument_list|,
name|authType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CertificateException
argument_list|(
literal|"Unknown client chain certificate: "
operator|+
name|chain
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkServerTrusted (X509Certificate[] chain, String authType)
specifier|public
name|void
name|checkServerTrusted
parameter_list|(
name|X509Certificate
index|[]
name|chain
parameter_list|,
name|String
name|authType
parameter_list|)
throws|throws
name|CertificateException
block|{
name|X509TrustManager
name|tm
init|=
name|trustManagerRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|tm
operator|!=
literal|null
condition|)
block|{
name|tm
operator|.
name|checkServerTrusted
argument_list|(
name|chain
argument_list|,
name|authType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CertificateException
argument_list|(
literal|"Unknown server chain certificate: "
operator|+
name|chain
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|X509Certificate
index|[]
name|EMPTY
init|=
operator|new
name|X509Certificate
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|getAcceptedIssuers ()
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
name|X509Certificate
index|[]
name|issuers
init|=
name|EMPTY
decl_stmt|;
name|X509TrustManager
name|tm
init|=
name|trustManagerRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|tm
operator|!=
literal|null
condition|)
block|{
name|issuers
operator|=
name|tm
operator|.
name|getAcceptedIssuers
argument_list|()
expr_stmt|;
block|}
return|return
name|issuers
return|;
block|}
DECL|method|needsReload ()
name|boolean
name|needsReload
parameter_list|()
block|{
name|boolean
name|reload
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|file
operator|.
name|lastModified
argument_list|()
operator|==
name|lastLoaded
condition|)
block|{
name|reload
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|lastLoaded
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|reload
return|;
block|}
DECL|method|loadTrustManager ()
name|X509TrustManager
name|loadTrustManager
parameter_list|()
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
name|X509TrustManager
name|trustManager
init|=
literal|null
decl_stmt|;
name|KeyStore
name|ks
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|lastLoaded
operator|=
name|file
operator|.
name|lastModified
argument_list|()
expr_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|ks
operator|.
name|load
argument_list|(
name|in
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded truststore '"
operator|+
name|file
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|TrustManagerFactory
name|trustManagerFactory
init|=
name|TrustManagerFactory
operator|.
name|getInstance
argument_list|(
name|SSLFactory
operator|.
name|SSLCERTIFICATE
argument_list|)
decl_stmt|;
name|trustManagerFactory
operator|.
name|init
argument_list|(
name|ks
argument_list|)
expr_stmt|;
name|TrustManager
index|[]
name|trustManagers
init|=
name|trustManagerFactory
operator|.
name|getTrustManagers
argument_list|()
decl_stmt|;
for|for
control|(
name|TrustManager
name|trustManager1
range|:
name|trustManagers
control|)
block|{
if|if
condition|(
name|trustManager1
operator|instanceof
name|X509TrustManager
condition|)
block|{
name|trustManager
operator|=
operator|(
name|X509TrustManager
operator|)
name|trustManager1
expr_stmt|;
break|break;
block|}
block|}
return|return
name|trustManager
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|reloadInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//NOP
block|}
if|if
condition|(
name|running
operator|&&
name|needsReload
argument_list|()
condition|)
block|{
try|try
block|{
name|trustManagerRef
operator|.
name|set
argument_list|(
name|loadTrustManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|RELOAD_ERROR_MESSAGE
operator|+
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

