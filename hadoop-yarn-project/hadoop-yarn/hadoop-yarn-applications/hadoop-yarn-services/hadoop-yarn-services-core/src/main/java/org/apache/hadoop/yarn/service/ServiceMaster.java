begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|annotations
operator|.
name|VisibleForTesting
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
name|lang3
operator|.
name|StringUtils
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
name|io
operator|.
name|DataOutputBuffer
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|service
operator|.
name|CompositeService
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ShutdownHookManager
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
name|yarn
operator|.
name|YarnUncaughtExceptionHandler
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|security
operator|.
name|AMRMTokenIdentifier
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
name|yarn
operator|.
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenSecretManager
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceState
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
name|yarn
operator|.
name|service
operator|.
name|exceptions
operator|.
name|BadClusterStateException
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
name|yarn
operator|.
name|service
operator|.
name|monitor
operator|.
name|ServiceMonitor
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
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
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
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|ServiceUtils
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
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|SliderFileSystem
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
name|nio
operator|.
name|ByteBuffer
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
name|Iterator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConstants
operator|.
name|KEYTAB_LOCATION
import|;
end_import

begin_class
DECL|class|ServiceMaster
specifier|public
class|class
name|ServiceMaster
extends|extends
name|CompositeService
block|{
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
name|ServiceMaster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|YARNFILE_OPTION
specifier|public
specifier|static
specifier|final
name|String
name|YARNFILE_OPTION
init|=
literal|"yarnfile"
decl_stmt|;
DECL|field|serviceDefPath
specifier|private
specifier|static
name|String
name|serviceDefPath
decl_stmt|;
DECL|field|context
specifier|protected
name|ServiceContext
name|context
decl_stmt|;
DECL|method|ServiceMaster (String name)
specifier|public
name|ServiceMaster
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|printSystemEnv
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|ServiceContext
argument_list|()
expr_stmt|;
name|Path
name|appDir
init|=
name|getAppDir
argument_list|()
decl_stmt|;
name|context
operator|.
name|serviceHdfsDir
operator|=
name|appDir
operator|.
name|toString
argument_list|()
expr_stmt|;
name|SliderFileSystem
name|fs
init|=
operator|new
name|SliderFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|context
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|fs
operator|.
name|setAppDir
argument_list|(
name|appDir
argument_list|)
expr_stmt|;
name|loadApplicationJson
argument_list|(
name|context
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|context
operator|.
name|tokens
operator|=
name|recordTokensForContainers
argument_list|()
expr_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|doSecureLogin
argument_list|()
expr_stmt|;
block|}
comment|// Take yarn config from YarnFile and merge them into YarnConfiguration
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|context
operator|.
name|service
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
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
name|ContainerId
name|amContainerId
init|=
name|getAMContainerId
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|amContainerId
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Service AppAttemptId: "
operator|+
name|attemptId
argument_list|)
expr_stmt|;
name|context
operator|.
name|attemptId
operator|=
name|attemptId
expr_stmt|;
comment|// configure AM to wait forever for RM
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCEMANAGER_CONNECT_MAX_WAIT_MS
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_MAX_ATTEMPTS
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"ServiceAppMaster"
argument_list|)
expr_stmt|;
name|context
operator|.
name|secretManager
operator|=
operator|new
name|ClientToAMTokenSecretManager
argument_list|(
name|attemptId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ClientAMService
name|clientAMService
init|=
operator|new
name|ClientAMService
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|clientAMService
operator|=
name|clientAMService
expr_stmt|;
name|addService
argument_list|(
name|clientAMService
argument_list|)
expr_stmt|;
name|ServiceScheduler
name|scheduler
init|=
name|createServiceScheduler
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|context
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|ServiceMonitor
name|monitor
init|=
operator|new
name|ServiceMonitor
argument_list|(
literal|"Service Monitor"
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|monitor
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// Record the tokens and use them for launching containers.
comment|// e.g. localization requires the hdfs delegation tokens
annotation|@
name|VisibleForTesting
DECL|method|recordTokensForContainers ()
specifier|protected
name|ByteBuffer
name|recordTokensForContainers
parameter_list|()
throws|throws
name|IOException
block|{
name|Credentials
name|copy
init|=
operator|new
name|Credentials
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
comment|// Now remove the AM->RM token so that task containers cannot access it.
name|Iterator
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|iter
init|=
name|copy
operator|.
name|getAllTokens
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|AMRMTokenIdentifier
operator|.
name|KIND_NAME
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
try|try
block|{
name|copy
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dob
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
return|;
block|}
comment|// 1. First try to use user specified keytabs
comment|// 2. If not specified, then try to use pre-installed keytab at localhost
comment|// 3. strip off hdfs delegation tokens to ensure use keytab to talk to hdfs
DECL|method|doSecureLogin ()
specifier|private
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
comment|// read the localized keytab specified by user
name|File
name|keytab
init|=
operator|new
name|File
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|KEYTAB_LOCATION
argument_list|,
name|context
operator|.
name|service
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keytab
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No keytab localized at "
operator|+
name|keytab
argument_list|)
expr_stmt|;
comment|// Check if there exists a pre-installed keytab at host
name|String
name|preInstalledKeytab
init|=
name|context
operator|.
name|service
operator|.
name|getKerberosPrincipal
argument_list|()
operator|.
name|getKeytab
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|preInstalledKeytab
argument_list|)
condition|)
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|preInstalledKeytab
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|keytab
operator|=
operator|new
name|File
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using pre-installed keytab from localhost: "
operator|+
name|preInstalledKeytab
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|keytab
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No keytab exists: "
operator|+
name|keytab
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|principal
init|=
name|context
operator|.
name|service
operator|.
name|getKerberosPrincipal
argument_list|()
operator|.
name|getPrincipalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
operator|(
name|principal
operator|)
argument_list|)
condition|)
block|{
name|principal
operator|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"No principal name specified.  Will use AM "
operator|+
literal|"login identity {} to attempt keytab-based login"
argument_list|,
name|principal
argument_list|)
expr_stmt|;
block|}
name|Credentials
name|credentials
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"User before logged in is: "
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|principalName
init|=
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|principal
argument_list|,
name|ServiceUtils
operator|.
name|getLocalHostName
argument_list|(
name|getConfig
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principalName
argument_list|,
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// add back the credentials
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"User after logged in is: "
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|principal
operator|=
name|principalName
expr_stmt|;
name|context
operator|.
name|keytab
operator|=
name|keytab
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|removeHdfsDelegationToken
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Remove HDFS delegation token from login user and ensure AM to use keytab
comment|// to talk to hdfs
DECL|method|removeHdfsDelegationToken (UserGroupInformation user)
specifier|private
specifier|static
name|void
name|removeHdfsDelegationToken
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|isFromKeytab
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"AM is not holding on a keytab in a secure deployment:"
operator|+
literal|" service will fail when tokens expire"
argument_list|)
expr_stmt|;
block|}
name|Credentials
name|credentials
init|=
name|user
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|iter
init|=
name|credentials
operator|.
name|getAllTokens
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|DelegationTokenIdentifier
operator|.
name|HDFS_DELEGATION_KIND
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Remove HDFS delegation token {}."
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getAMContainerId ()
specifier|protected
name|ContainerId
name|getAMContainerId
parameter_list|()
throws|throws
name|BadClusterStateException
block|{
return|return
name|ContainerId
operator|.
name|fromString
argument_list|(
name|ServiceUtils
operator|.
name|mandatoryEnvVariable
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|CONTAINER_ID
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAppDir ()
specifier|protected
name|Path
name|getAppDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|serviceDefPath
argument_list|)
operator|.
name|getParent
argument_list|()
return|;
block|}
DECL|method|createServiceScheduler (ServiceContext context)
specifier|protected
name|ServiceScheduler
name|createServiceScheduler
parameter_list|(
name|ServiceContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
return|return
operator|new
name|ServiceScheduler
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|method|loadApplicationJson (ServiceContext context, SliderFileSystem fs)
specifier|protected
name|void
name|loadApplicationJson
parameter_list|(
name|ServiceContext
name|context
parameter_list|,
name|SliderFileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|service
operator|=
name|ServiceApiUtil
operator|.
name|loadServiceFrom
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|serviceDefPath
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|service
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|context
operator|.
name|service
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting service as user "
operator|+
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|doAs
argument_list|(
call|(
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
call|)
argument_list|()
operator|->
block|{
name|super
operator|.
name|serviceStart
argument_list|()
block|;
return|return
literal|null
return|;
block|}
block|)
function|;
block|}
end_class

begin_function
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping app master"
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
end_function

begin_comment
comment|// This method should be called whenever there is an increment or decrement
end_comment

begin_comment
comment|// of a READY state component of a service
end_comment

begin_function
DECL|method|checkAndUpdateServiceState ( ServiceScheduler scheduler)
specifier|public
specifier|static
specifier|synchronized
name|void
name|checkAndUpdateServiceState
parameter_list|(
name|ServiceScheduler
name|scheduler
parameter_list|)
block|{
name|ServiceState
name|curState
init|=
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
comment|// Check the state of all components
name|boolean
name|isStable
init|=
literal|true
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
name|comp
range|:
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|getComponents
argument_list|()
control|)
block|{
if|if
condition|(
name|comp
operator|.
name|getState
argument_list|()
operator|!=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ComponentState
operator|.
name|STABLE
condition|)
block|{
name|isStable
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|isStable
condition|)
block|{
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|STABLE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// mark new state as started only if current state is stable, otherwise
comment|// leave it as is
if|if
condition|(
name|curState
operator|==
name|ServiceState
operator|.
name|STABLE
condition|)
block|{
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|STARTED
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|curState
operator|!=
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|getState
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Service state changed from {} -> {}"
argument_list|,
name|curState
argument_list|,
name|scheduler
operator|.
name|getApp
argument_list|()
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|printSystemEnv ()
specifier|private
name|void
name|printSystemEnv
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envs
range|:
name|System
operator|.
name|getenv
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"{} = {}"
argument_list|,
name|envs
operator|.
name|getKey
argument_list|()
argument_list|,
name|envs
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
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
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|ServiceMaster
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
try|try
block|{
name|ServiceMaster
name|serviceMaster
init|=
operator|new
name|ServiceMaster
argument_list|(
literal|"Service Master"
argument_list|)
decl_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|CompositeServiceShutdownHook
argument_list|(
name|serviceMaster
argument_list|)
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|YARNFILE_OPTION
argument_list|,
literal|true
argument_list|,
literal|"HDFS path to JSON service "
operator|+
literal|"specification"
argument_list|)
expr_stmt|;
name|opts
operator|.
name|getOption
argument_list|(
name|YARNFILE_OPTION
argument_list|)
operator|.
name|setRequired
argument_list|(
literal|true
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
name|opts
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|CommandLine
name|cmdLine
init|=
name|parser
operator|.
name|getCommandLine
argument_list|()
decl_stmt|;
name|serviceMaster
operator|.
name|serviceDefPath
operator|=
name|cmdLine
operator|.
name|getOptionValue
argument_list|(
name|YARNFILE_OPTION
argument_list|)
expr_stmt|;
name|serviceMaster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|serviceMaster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error starting service master"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
literal|"Error starting service master"
argument_list|)
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

