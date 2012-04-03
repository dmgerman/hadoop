begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|Options
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
name|fs
operator|.
name|FileSystem
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|HftpFileSystem
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|CancelDelegationTokenServlet
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|GetDelegationTokenServlet
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|RenewDelegationTokenServlet
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
name|hdfs
operator|.
name|web
operator|.
name|URLUtils
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
name|io
operator|.
name|Text
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
name|net
operator|.
name|NetUtils
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
name|Credentials
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|token
operator|.
name|Token
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
name|GenericOptionsParser
import|;
end_import

begin_comment
comment|/**  * Fetch a DelegationToken from the current Namenode and store it in the  * specified file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DelegationTokenFetcher
specifier|public
class|class
name|DelegationTokenFetcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DelegationTokenFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|WEBSERVICE
specifier|private
specifier|static
specifier|final
name|String
name|WEBSERVICE
init|=
literal|"webservice"
decl_stmt|;
DECL|field|RENEWER
specifier|private
specifier|static
specifier|final
name|String
name|RENEWER
init|=
literal|"renewer"
decl_stmt|;
DECL|field|CANCEL
specifier|private
specifier|static
specifier|final
name|String
name|CANCEL
init|=
literal|"cancel"
decl_stmt|;
DECL|field|RENEW
specifier|private
specifier|static
specifier|final
name|String
name|RENEW
init|=
literal|"renew"
decl_stmt|;
DECL|field|PRINT
specifier|private
specifier|static
specifier|final
name|String
name|PRINT
init|=
literal|"print"
decl_stmt|;
static|static
block|{
comment|// Enable Kerberos sockets
name|System
operator|.
name|setProperty
argument_list|(
literal|"https.cipherSuites"
argument_list|,
literal|"TLS_KRB5_WITH_3DES_EDE_CBC_SHA"
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage (PrintStream err)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|PrintStream
name|err
parameter_list|)
throws|throws
name|IOException
block|{
name|err
operator|.
name|println
argument_list|(
literal|"fetchdt retrieves delegation tokens from the NameNode"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"fetchdt<opts><token file>"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"Options:"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --webservice<url>  Url to contact NN on"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --renewer<name>    Name of the delegation token renewer"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --cancel            Cancel the delegation token"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --renew             Renew the delegation token.  Delegation "
operator|+
literal|"token must have been fetched using the --renewer<name> option."
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --print             Print the delegation token"
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|GenericOptionsParser
operator|.
name|printGenericCommandUsage
argument_list|(
name|err
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
DECL|method|readTokens (Path file, Configuration conf)
specifier|private
specifier|static
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|readTokens
parameter_list|(
name|Path
name|file
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|file
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|creds
operator|.
name|getAllTokens
argument_list|()
return|;
block|}
comment|/**    * Command-line interface    */
DECL|method|main (final String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|Options
name|fetcherOptions
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|fetcherOptions
operator|.
name|addOption
argument_list|(
name|WEBSERVICE
argument_list|,
literal|true
argument_list|,
literal|"HTTPS url to reach the NameNode at"
argument_list|)
expr_stmt|;
name|fetcherOptions
operator|.
name|addOption
argument_list|(
name|RENEWER
argument_list|,
literal|true
argument_list|,
literal|"Name of the delegation token renewer"
argument_list|)
expr_stmt|;
name|fetcherOptions
operator|.
name|addOption
argument_list|(
name|CANCEL
argument_list|,
literal|false
argument_list|,
literal|"cancel the token"
argument_list|)
expr_stmt|;
name|fetcherOptions
operator|.
name|addOption
argument_list|(
name|RENEW
argument_list|,
literal|false
argument_list|,
literal|"renew the token"
argument_list|)
expr_stmt|;
name|fetcherOptions
operator|.
name|addOption
argument_list|(
name|PRINT
argument_list|,
literal|false
argument_list|,
literal|"print the token"
argument_list|)
expr_stmt|;
name|GenericOptionsParser
name|parser
init|=
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|fetcherOptions
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|CommandLine
name|cmd
init|=
name|parser
operator|.
name|getCommandLine
argument_list|()
decl_stmt|;
comment|// get options
specifier|final
name|String
name|webUrl
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|WEBSERVICE
argument_list|)
condition|?
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|WEBSERVICE
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|String
name|renewer
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|RENEWER
argument_list|)
condition|?
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|RENEWER
argument_list|)
else|:
literal|null
decl_stmt|;
specifier|final
name|boolean
name|cancel
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|CANCEL
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|renew
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|RENEW
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|print
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|PRINT
argument_list|)
decl_stmt|;
name|String
index|[]
name|remaining
init|=
name|parser
operator|.
name|getRemainingArgs
argument_list|()
decl_stmt|;
comment|// check option validity
if|if
condition|(
name|cancel
operator|&&
name|renew
operator|||
name|cancel
operator|&&
name|print
operator|||
name|renew
operator|&&
name|print
operator|||
name|cancel
operator|&&
name|renew
operator|&&
name|print
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Only specify cancel, renew or print."
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remaining
operator|.
name|length
operator|!=
literal|1
operator|||
name|remaining
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Must specify exactly one token file"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
comment|// default to using the local file system
name|FileSystem
name|local
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|tokenFile
init|=
operator|new
name|Path
argument_list|(
name|local
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|,
name|remaining
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// Login the current user
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|print
condition|)
block|{
name|DelegationTokenIdentifier
name|id
init|=
operator|new
name|DelegationTokenSecretManager
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
operator|.
name|createIdentifier
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|readTokens
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
control|)
block|{
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Token ("
operator|+
name|id
operator|+
literal|") for "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|cancel
condition|)
block|{
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|readTokens
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
control|)
block|{
if|if
condition|(
name|token
operator|.
name|isManaged
argument_list|()
condition|)
block|{
name|token
operator|.
name|cancel
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cancelled token for "
operator|+
name|token
operator|.
name|getService
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|renew
condition|)
block|{
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|readTokens
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
control|)
block|{
if|if
condition|(
name|token
operator|.
name|isManaged
argument_list|()
condition|)
block|{
name|long
name|result
init|=
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Renewed token for "
operator|+
name|token
operator|.
name|getService
argument_list|()
operator|+
literal|" until: "
operator|+
operator|new
name|Date
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// otherwise we are fetching
if|if
condition|(
name|webUrl
operator|!=
literal|null
condition|)
block|{
name|Credentials
name|creds
init|=
name|getDTfromRemote
argument_list|(
name|webUrl
argument_list|,
name|renewer
argument_list|)
decl_stmt|;
name|creds
operator|.
name|writeTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|creds
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fetched token via "
operator|+
name|webUrl
operator|+
literal|" for "
operator|+
name|token
operator|.
name|getService
argument_list|()
operator|+
literal|" into "
operator|+
name|tokenFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|fs
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
decl_stmt|;
name|Credentials
name|cred
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|cred
operator|.
name|addToken
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|cred
operator|.
name|writeTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fetched token for "
operator|+
name|token
operator|.
name|getService
argument_list|()
operator|+
literal|" into "
operator|+
name|tokenFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getDTfromRemote (String nnAddr, String renewer)
specifier|static
specifier|public
name|Credentials
name|getDTfromRemote
parameter_list|(
name|String
name|nnAddr
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|dis
init|=
literal|null
decl_stmt|;
name|InetSocketAddress
name|serviceAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|nnAddr
argument_list|)
decl_stmt|;
try|try
block|{
name|StringBuffer
name|url
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|renewer
operator|!=
literal|null
condition|)
block|{
name|url
operator|.
name|append
argument_list|(
name|nnAddr
argument_list|)
operator|.
name|append
argument_list|(
name|GetDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|)
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|GetDelegationTokenServlet
operator|.
name|RENEWER
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|url
operator|.
name|append
argument_list|(
name|nnAddr
argument_list|)
operator|.
name|append
argument_list|(
name|GetDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrieving token from: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
name|URL
name|remoteURL
init|=
operator|new
name|URL
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|fetchServiceTicket
argument_list|(
name|remoteURL
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|URLUtils
operator|.
name|openConnection
argument_list|(
name|remoteURL
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|Credentials
name|ts
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|dis
operator|=
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|ts
operator|.
name|readFields
argument_list|(
name|dis
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
argument_list|<
name|?
argument_list|>
name|token
range|:
name|ts
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|token
operator|.
name|setKind
argument_list|(
name|HftpFileSystem
operator|.
name|TOKEN_KIND
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|serviceAddr
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to obtain remote token"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|dis
operator|!=
literal|null
condition|)
name|dis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Renew a Delegation Token.    * @param nnAddr the NameNode's address    * @param tok the token to renew    * @return the Date that the token will expire next.    * @throws IOException    */
DECL|method|renewDelegationToken (String nnAddr, Token<DelegationTokenIdentifier> tok )
specifier|static
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|String
name|nnAddr
parameter_list|,
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|tok
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|nnAddr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|RenewDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|RenewDelegationTokenServlet
operator|.
name|TOKEN
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|tok
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|fetchServiceTicket
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|URLUtils
operator|.
name|openConnection
argument_list|(
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error renewing token: "
operator|+
name|connection
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|result
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|in
operator|.
name|readLine
argument_list|()
argument_list|)
decl_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"error in renew over HTTP"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|IOException
name|e
init|=
name|getExceptionFromResponse
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"rethrowing exception from HTTP request: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
throw|throw
name|ie
throw|;
block|}
block|}
comment|// parse the message and extract the name of the exception and the message
DECL|method|getExceptionFromResponse (HttpURLConnection con)
specifier|static
specifier|private
name|IOException
name|getExceptionFromResponse
parameter_list|(
name|HttpURLConnection
name|con
parameter_list|)
block|{
name|IOException
name|e
init|=
literal|null
decl_stmt|;
name|String
name|resp
decl_stmt|;
if|if
condition|(
name|con
operator|==
literal|null
condition|)
return|return
literal|null
return|;
try|try
block|{
name|resp
operator|=
name|con
operator|.
name|getResponseMessage
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|resp
operator|==
literal|null
operator|||
name|resp
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
name|String
name|exceptionClass
init|=
literal|""
decl_stmt|,
name|exceptionMsg
init|=
literal|""
decl_stmt|;
name|String
index|[]
name|rs
init|=
name|resp
operator|.
name|split
argument_list|(
literal|";"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|length
operator|<
literal|2
condition|)
return|return
literal|null
return|;
name|exceptionClass
operator|=
name|rs
index|[
literal|0
index|]
expr_stmt|;
name|exceptionMsg
operator|=
name|rs
index|[
literal|1
index|]
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Error response from HTTP request="
operator|+
name|resp
operator|+
literal|";ec="
operator|+
name|exceptionClass
operator|+
literal|";em="
operator|+
name|exceptionMsg
argument_list|)
expr_stmt|;
if|if
condition|(
name|exceptionClass
operator|==
literal|null
operator|||
name|exceptionClass
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
comment|// recreate exception objects
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|ec
init|=
name|Class
operator|.
name|forName
argument_list|(
name|exceptionClass
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// we are interested in constructor with String arguments
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|constructor
init|=
operator|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
operator|)
name|ec
operator|.
name|getConstructor
argument_list|(
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
comment|// create an instance
name|e
operator|=
operator|(
name|IOException
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|exceptionMsg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"failed to create object of this class"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|e
operator|.
name|setStackTrace
argument_list|(
operator|new
name|StackTraceElement
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// local stack is not relevant
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception from HTTP response="
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
comment|/**    * Cancel a Delegation Token.    * @param nnAddr the NameNode's address    * @param tok the token to cancel    * @throws IOException    */
DECL|method|cancelDelegationToken (String nnAddr, Token<DelegationTokenIdentifier> tok )
specifier|static
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|String
name|nnAddr
parameter_list|,
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|tok
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|nnAddr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|CancelDelegationTokenServlet
operator|.
name|PATH_SPEC
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|CancelDelegationTokenServlet
operator|.
name|TOKEN
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|tok
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedReader
name|in
init|=
literal|null
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|fetchServiceTicket
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|(
name|HttpURLConnection
operator|)
name|URLUtils
operator|.
name|openConnection
argument_list|(
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error cancelling token: "
operator|+
name|connection
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"error in cancel over HTTP"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|IOException
name|e
init|=
name|getExceptionFromResponse
argument_list|(
name|connection
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"rethrowing exception from HTTP request: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
throw|throw
name|ie
throw|;
block|}
block|}
block|}
end_class

end_unit

