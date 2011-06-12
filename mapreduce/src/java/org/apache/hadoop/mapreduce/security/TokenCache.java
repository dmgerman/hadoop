begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|security
package|;
end_package

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
name|URI
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
name|classification
operator|.
name|InterfaceStability
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
name|DistributedFileSystem
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|mapred
operator|.
name|JobConf
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|KerberosName
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_comment
comment|/**  * This class provides user facing APIs for transferring secrets from  * the job client to the tasks.  * The secrets can be stored just before submission of jobs and read during  * the task execution.    */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TokenCache
specifier|public
class|class
name|TokenCache
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
name|TokenCache
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * auxiliary method to get user's secret keys..    * @param alias    * @return secret key from the storage    */
DECL|method|getSecretKey (Credentials credentials, Text alias)
specifier|public
specifier|static
name|byte
index|[]
name|getSecretKey
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|Text
name|alias
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|credentials
operator|.
name|getSecretKey
argument_list|(
name|alias
argument_list|)
return|;
block|}
comment|/**    * Convenience method to obtain delegation tokens from namenodes     * corresponding to the paths passed.    * @param credentials    * @param ps array of paths    * @param conf configuration    * @throws IOException    */
DECL|method|obtainTokensForNamenodes (Credentials credentials, Path[] ps, Configuration conf)
specifier|public
specifier|static
name|void
name|obtainTokensForNamenodes
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|Path
index|[]
name|ps
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|obtainTokensForNamenodesInternal
argument_list|(
name|credentials
argument_list|,
name|ps
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|obtainTokensForNamenodesInternal (Credentials credentials, Path[] ps, Configuration conf)
specifier|static
name|void
name|obtainTokensForNamenodesInternal
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|Path
index|[]
name|ps
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Path
name|p
range|:
name|ps
control|)
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|obtainTokensForNamenodesInternal
argument_list|(
name|fs
argument_list|,
name|credentials
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * get delegation token for a specific FS    * @param fs    * @param credentials    * @param p    * @param conf    * @throws IOException    */
DECL|method|obtainTokensForNamenodesInternal (FileSystem fs, Credentials credentials, Configuration conf)
specifier|static
name|void
name|obtainTokensForNamenodesInternal
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get jobtracker principal id (for the renewer)
name|KerberosName
name|jtKrbName
init|=
operator|new
name|KerberosName
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JTConfig
operator|.
name|JT_USER_NAME
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|delegTokenRenewer
init|=
name|jtKrbName
operator|.
name|getShortName
argument_list|()
decl_stmt|;
name|boolean
name|readFile
init|=
literal|true
decl_stmt|;
name|String
name|fsName
init|=
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
decl_stmt|;
if|if
condition|(
name|TokenCache
operator|.
name|getDelegationToken
argument_list|(
name|credentials
argument_list|,
name|fsName
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|//TODO: Need to come up with a better place to put
comment|//this block of code to do with reading the file
if|if
condition|(
name|readFile
condition|)
block|{
name|readFile
operator|=
literal|false
expr_stmt|;
name|String
name|binaryTokenFilename
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"mapreduce.job.credentials.binary"
argument_list|)
decl_stmt|;
if|if
condition|(
name|binaryTokenFilename
operator|!=
literal|null
condition|)
block|{
name|Credentials
name|binary
decl_stmt|;
try|try
block|{
name|binary
operator|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///"
operator|+
name|binaryTokenFilename
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|credentials
operator|.
name|addAll
argument_list|(
name|binary
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|TokenCache
operator|.
name|getDelegationToken
argument_list|(
name|credentials
argument_list|,
name|fsName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"DT for "
operator|+
name|fsName
operator|+
literal|" is already present"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
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
name|delegTokenRenewer
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|Text
name|fsNameText
init|=
operator|new
name|Text
argument_list|(
name|fsName
argument_list|)
decl_stmt|;
name|token
operator|.
name|setService
argument_list|(
name|fsNameText
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|addToken
argument_list|(
name|fsNameText
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got dt for "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
operator|+
literal|";uri="
operator|+
name|fsName
operator|+
literal|";t.service="
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
comment|/**    * file name used on HDFS for generated job token    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|JOB_TOKEN_HDFS_FILE
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TOKEN_HDFS_FILE
init|=
literal|"jobToken"
decl_stmt|;
comment|/**    * conf setting for job tokens cache file name    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|JOB_TOKENS_FILENAME
specifier|public
specifier|static
specifier|final
name|String
name|JOB_TOKENS_FILENAME
init|=
literal|"mapreduce.job.jobTokenFile"
decl_stmt|;
DECL|field|JOB_TOKEN
specifier|private
specifier|static
specifier|final
name|Text
name|JOB_TOKEN
init|=
operator|new
name|Text
argument_list|(
literal|"ShuffleAndJobToken"
argument_list|)
decl_stmt|;
comment|/**    *     * @param namenode    * @return delegation token    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getDelegationToken ( Credentials credentials, String namenode)
specifier|public
specifier|static
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|namenode
parameter_list|)
block|{
return|return
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|credentials
operator|.
name|getToken
argument_list|(
operator|new
name|Text
argument_list|(
name|namenode
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * load job token from a file    * @param conf    * @throws IOException    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|loadTokens (String jobTokenFile, JobConf conf)
specifier|public
specifier|static
name|Credentials
name|loadTokens
parameter_list|(
name|String
name|jobTokenFile
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|localJobTokenFile
init|=
operator|new
name|Path
argument_list|(
literal|"file:///"
operator|+
name|jobTokenFile
argument_list|)
decl_stmt|;
name|Credentials
name|ts
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|localJobTokenFile
argument_list|,
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
literal|"Task: Loaded jobTokenFile from: "
operator|+
name|localJobTokenFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|"; num of sec keys  = "
operator|+
name|ts
operator|.
name|numberOfSecretKeys
argument_list|()
operator|+
literal|" Number of tokens "
operator|+
name|ts
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
comment|/**    * store job token    * @param t    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setJobToken (Token<? extends TokenIdentifier> t, Credentials credentials)
specifier|public
specifier|static
name|void
name|setJobToken
parameter_list|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|t
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
block|{
name|credentials
operator|.
name|addToken
argument_list|(
name|JOB_TOKEN
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @return job token    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|getJobToken (Credentials credentials)
specifier|public
specifier|static
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|getJobToken
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
return|return
operator|(
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
operator|)
name|credentials
operator|.
name|getToken
argument_list|(
name|JOB_TOKEN
argument_list|)
return|;
block|}
block|}
end_class

end_unit

