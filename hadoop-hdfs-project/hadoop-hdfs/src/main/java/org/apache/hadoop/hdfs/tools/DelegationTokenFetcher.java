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
name|PrintStream
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
name|web
operator|.
name|WebHdfsConstants
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
name|ExitUtil
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
DECL|field|WEBSERVICE
specifier|private
specifier|static
specifier|final
name|String
name|WEBSERVICE
init|=
literal|"webservice"
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
DECL|field|HELP
specifier|private
specifier|static
specifier|final
name|String
name|HELP
init|=
literal|"help"
decl_stmt|;
DECL|field|HELP_SHORT
specifier|private
specifier|static
specifier|final
name|String
name|HELP_SHORT
init|=
literal|"h"
decl_stmt|;
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
DECL|field|PRINT
specifier|private
specifier|static
specifier|final
name|String
name|PRINT
init|=
literal|"print"
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
DECL|field|RENEWER
specifier|private
specifier|static
specifier|final
name|String
name|RENEWER
init|=
literal|"renewer"
decl_stmt|;
DECL|field|VERBOSE
specifier|private
specifier|static
specifier|final
name|String
name|VERBOSE
init|=
literal|"verbose"
decl_stmt|;
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
literal|"HTTP url to reach the NameNode at"
argument_list|)
operator|.
name|addOption
argument_list|(
name|RENEWER
argument_list|,
literal|true
argument_list|,
literal|"Name of the delegation token renewer"
argument_list|)
operator|.
name|addOption
argument_list|(
name|CANCEL
argument_list|,
literal|false
argument_list|,
literal|"cancel the token"
argument_list|)
operator|.
name|addOption
argument_list|(
name|RENEW
argument_list|,
literal|false
argument_list|,
literal|"renew the token"
argument_list|)
operator|.
name|addOption
argument_list|(
name|PRINT
argument_list|,
literal|false
argument_list|,
literal|"print the token"
argument_list|)
operator|.
name|addOption
argument_list|(
name|VERBOSE
argument_list|,
literal|false
argument_list|,
literal|"print verbose output"
argument_list|)
operator|.
name|addOption
argument_list|(
name|HELP_SHORT
argument_list|,
name|HELP
argument_list|,
literal|false
argument_list|,
literal|"print out help information"
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
specifier|final
name|boolean
name|verbose
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|VERBOSE
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|help
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|HELP
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
name|help
condition|)
block|{
name|printUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|commandCount
init|=
operator|(
name|cancel
condition|?
literal|1
else|:
literal|0
operator|)
operator|+
operator|(
name|renew
condition|?
literal|1
else|:
literal|0
operator|)
operator|+
operator|(
name|print
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|commandCount
operator|>
literal|1
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
name|printTokens
argument_list|(
name|conf
argument_list|,
name|tokenFile
argument_list|,
name|verbose
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cancel
condition|)
block|{
name|cancelTokens
argument_list|(
name|conf
argument_list|,
name|tokenFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|renew
condition|)
block|{
name|renewTokens
argument_list|(
name|conf
argument_list|,
name|tokenFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise we are fetching
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|(
name|conf
argument_list|,
name|webUrl
argument_list|)
decl_stmt|;
name|saveDelegationToken
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|renewer
argument_list|,
name|tokenFile
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileSystem (Configuration conf, String url)
specifier|private
specifier|static
name|FileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|// For backward compatibility
name|URI
name|fsUri
init|=
name|URI
operator|.
name|create
argument_list|(
name|url
operator|.
name|replaceFirst
argument_list|(
literal|"^http://"
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
operator|+
literal|"://"
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"^https://"
argument_list|,
name|WebHdfsConstants
operator|.
name|SWEBHDFS_SCHEME
operator|+
literal|"://"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|fsUri
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|cancelTokens (final Configuration conf, final Path tokenFile)
specifier|static
name|void
name|cancelTokens
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Path
name|tokenFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
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
annotation|@
name|VisibleForTesting
DECL|method|renewTokens (final Configuration conf, final Path tokenFile)
specifier|static
name|void
name|renewTokens
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Path
name|tokenFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
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
annotation|@
name|VisibleForTesting
DECL|method|saveDelegationToken (Configuration conf, FileSystem fs, final String renewer, final Path tokenFile)
specifier|static
name|void
name|saveDelegationToken
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|String
name|renewer
parameter_list|,
specifier|final
name|Path
name|tokenFile
parameter_list|)
throws|throws
name|IOException
block|{
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
if|if
condition|(
literal|null
operator|!=
name|token
condition|)
block|{
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
comment|// dtutil is replacing this tool; preserve legacy functionality
name|cred
operator|.
name|writeTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|,
name|Credentials
operator|.
name|SerializedFormat
operator|.
name|WRITABLE
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
literal|"Fetched token "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
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
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: Failed to fetch token from "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|printTokensToString ( final Configuration conf, final Path tokenFile, final boolean verbose)
specifier|static
name|String
name|printTokensToString
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Path
name|tokenFile
parameter_list|,
specifier|final
name|boolean
name|verbose
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sbld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|nl
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
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
name|String
name|idStr
init|=
operator|(
name|verbose
condition|?
name|id
operator|.
name|toString
argument_list|()
else|:
name|id
operator|.
name|toStringStable
argument_list|()
operator|)
decl_stmt|;
name|sbld
operator|.
name|append
argument_list|(
literal|"Token ("
argument_list|)
operator|.
name|append
argument_list|(
name|idStr
argument_list|)
operator|.
name|append
argument_list|(
literal|") for "
argument_list|)
operator|.
name|append
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|nl
argument_list|)
expr_stmt|;
block|}
return|return
name|sbld
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Be sure to call printTokensToString which is verified in unit test.
DECL|method|printTokens (final Configuration conf, final Path tokenFile, final boolean verbose)
specifier|static
name|void
name|printTokens
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Path
name|tokenFile
parameter_list|,
specifier|final
name|boolean
name|verbose
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|printTokensToString
argument_list|(
name|conf
argument_list|,
name|tokenFile
argument_list|,
name|verbose
argument_list|)
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
literal|"  --webservice<url>  Url to contact NN on (starts with "
operator|+
literal|"http:// or https://)"
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
literal|"  --renew             Renew the delegation token.  "
operator|+
literal|"Delegation "
operator|+
literal|"token must have been fetched using the --renewer"
operator|+
literal|"<name> option."
argument_list|)
expr_stmt|;
name|err
operator|.
name|println
argument_list|(
literal|"  --print [--verbose] Print the delegation token, when "
operator|+
literal|"--verbose is passed, print more information about the token"
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
name|ExitUtil
operator|.
name|terminate
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
block|}
end_class

end_unit

