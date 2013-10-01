begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
package|;
end_package

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
name|InetAddress
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
name|UnknownHostException
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
name|CommonConfigurationKeys
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
name|FileContext
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
name|LocalFileSystem
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
name|http
operator|.
name|HttpConfig
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
name|LocalContainerLauncher
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
name|ShuffleHandler
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
name|MRConfig
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
name|MRJobConfig
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
name|v2
operator|.
name|hs
operator|.
name|JobHistoryServer
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
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|v2
operator|.
name|jobhistory
operator|.
name|JobHistoryUtils
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
name|v2
operator|.
name|util
operator|.
name|MRWebAppUtil
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
name|service
operator|.
name|AbstractService
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
name|Service
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
name|JarFinder
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
name|YarnRuntimeException
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
name|server
operator|.
name|MiniYARNCluster
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
name|server
operator|.
name|nodemanager
operator|.
name|ContainerExecutor
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
name|server
operator|.
name|nodemanager
operator|.
name|DefaultContainerExecutor
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
import|;
end_import

begin_comment
comment|/**  * Configures and starts the MR-specific components in the YARN cluster.  *  */
end_comment

begin_class
DECL|class|MiniMRYarnCluster
specifier|public
class|class
name|MiniMRYarnCluster
extends|extends
name|MiniYARNCluster
block|{
DECL|field|APPJAR
specifier|public
specifier|static
specifier|final
name|String
name|APPJAR
init|=
name|JarFinder
operator|.
name|getJar
argument_list|(
name|LocalContainerLauncher
operator|.
name|class
argument_list|)
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
name|MiniMRYarnCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|historyServer
specifier|private
name|JobHistoryServer
name|historyServer
decl_stmt|;
DECL|field|historyServerWrapper
specifier|private
name|JobHistoryServerWrapper
name|historyServerWrapper
decl_stmt|;
DECL|method|MiniMRYarnCluster (String testName)
specifier|public
name|MiniMRYarnCluster
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|this
argument_list|(
name|testName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|MiniMRYarnCluster (String testName, int noOfNMs)
specifier|public
name|MiniMRYarnCluster
parameter_list|(
name|String
name|testName
parameter_list|,
name|int
name|noOfNMs
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|,
name|noOfNMs
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|//TODO: add the history server
name|historyServerWrapper
operator|=
operator|new
name|JobHistoryServerWrapper
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|historyServerWrapper
argument_list|)
expr_stmt|;
block|}
DECL|method|getResolvedMRHistoryWebAppURLWithoutScheme ( Configuration conf, boolean isSSLEnabled)
specifier|public
specifier|static
name|String
name|getResolvedMRHistoryWebAppURLWithoutScheme
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|boolean
name|isSSLEnabled
parameter_list|)
block|{
name|InetSocketAddress
name|address
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isSSLEnabled
condition|)
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_HTTPS_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_HTTPS_PORT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|address
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_WEBAPP_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_WEBAPP_PORT
argument_list|)
expr_stmt|;
block|}
name|address
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|InetAddress
name|resolved
init|=
name|address
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|resolved
operator|==
literal|null
operator|||
name|resolved
operator|.
name|isAnyLocalAddress
argument_list|()
operator|||
name|resolved
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
name|String
name|lh
init|=
name|address
operator|.
name|getHostName
argument_list|()
decl_stmt|;
try|try
block|{
name|lh
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|//Ignore and fallback.
block|}
name|sb
operator|.
name|append
argument_list|(
name|lh
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
operator|.
name|append
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|YARN_FRAMEWORK_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
operator|new
name|File
argument_list|(
name|getTestWorkDir
argument_list|()
argument_list|,
literal|"apps_staging_dir/"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// By default, VMEM monitoring disabled, PMEM monitoring enabled.
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
argument_list|,
name|MRConfig
operator|.
name|DEFAULT_MAPREDUCE_MINICLUSTER_CONTROL_RESOURCE_MONITORING
argument_list|)
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_PERMISSIONS_UMASK_KEY
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
try|try
block|{
name|Path
name|stagingPath
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/*        * Re-configure the staging path on Windows if the file system is localFs.        * We need to use a absolute path that contains the drive letter. The unit        * test could run on a different drive than the AM. We can run into the        * issue that job files are localized to the drive where the test runs on,        * while the AM starts on a different drive and fails to find the job        * metafiles. Using absolute path can avoid this ambiguity.        */
if|if
condition|(
name|Path
operator|.
name|WINDOWS
condition|)
block|{
if|if
condition|(
name|LocalFileSystem
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|stagingPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|,
operator|new
name|File
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_STAGING_DIR
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|FileContext
name|fc
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|stagingPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|fc
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
name|stagingPath
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|stagingPath
operator|+
literal|" exists! deleting..."
argument_list|)
expr_stmt|;
name|fc
operator|.
name|delete
argument_list|(
name|stagingPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"mkdir: "
operator|+
name|stagingPath
argument_list|)
expr_stmt|;
comment|//mkdir the staging directory so that right permissions are set while running as proxy user
name|fc
operator|.
name|mkdir
argument_list|(
name|stagingPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//mkdir done directory as well
name|String
name|doneDir
init|=
name|JobHistoryUtils
operator|.
name|getConfiguredHistoryServerDoneDirPrefix
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|doneDirPath
init|=
name|fc
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|doneDir
argument_list|)
argument_list|)
decl_stmt|;
name|fc
operator|.
name|mkdir
argument_list|(
name|doneDirPath
argument_list|,
literal|null
argument_list|,
literal|true
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
name|YarnRuntimeException
argument_list|(
literal|"Could not create staging directory. "
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
comment|// The default is local because of
comment|// which shuffle doesn't happen
comment|//configure the shuffle service in NM
name|conf
operator|.
name|setStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICES
argument_list|,
operator|new
name|String
index|[]
block|{
name|ShuffleHandler
operator|.
name|MAPREDUCE_SHUFFLE_SERVICEID
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICE_FMT
argument_list|,
name|ShuffleHandler
operator|.
name|MAPREDUCE_SHUFFLE_SERVICEID
argument_list|)
argument_list|,
name|ShuffleHandler
operator|.
name|class
argument_list|,
name|Service
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Non-standard shuffle port
name|conf
operator|.
name|setInt
argument_list|(
name|ShuffleHandler
operator|.
name|SHUFFLE_PORT_CONFIG_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_EXECUTOR
argument_list|,
name|DefaultContainerExecutor
operator|.
name|class
argument_list|,
name|ContainerExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// TestMRJobs is for testing non-uberized operation only; see TestUberAM
comment|// for corresponding uberized tests.
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|false
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
DECL|class|JobHistoryServerWrapper
specifier|private
class|class
name|JobHistoryServerWrapper
extends|extends
name|AbstractService
block|{
DECL|method|JobHistoryServerWrapper ()
specifier|public
name|JobHistoryServerWrapper
parameter_list|()
block|{
name|super
argument_list|(
name|JobHistoryServerWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
specifier|synchronized
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
operator|!
name|getConfig
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_MINICLUSTER_FIXED_PORTS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_MINICLUSTER_FIXED_PORTS
argument_list|)
condition|)
block|{
name|String
name|hostname
init|=
name|MiniYARNCluster
operator|.
name|getHostname
argument_list|()
decl_stmt|;
comment|// pick free random ports.
name|getConfig
argument_list|()
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|,
name|hostname
operator|+
literal|":0"
argument_list|)
expr_stmt|;
name|MRWebAppUtil
operator|.
name|setJHSWebappURLWithoutScheme
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|hostname
operator|+
literal|":0"
argument_list|)
expr_stmt|;
name|getConfig
argument_list|()
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|JHS_ADMIN_ADDRESS
argument_list|,
name|hostname
operator|+
literal|":0"
argument_list|)
expr_stmt|;
block|}
name|historyServer
operator|=
operator|new
name|JobHistoryServer
argument_list|()
expr_stmt|;
name|historyServer
operator|.
name|init
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|historyServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|historyServer
operator|.
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|INITED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for HistoryServer to start..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
comment|//TODO Add a timeout. State.STOPPED check ?
if|if
condition|(
name|historyServer
operator|.
name|getServiceState
argument_list|()
operator|!=
name|STATE
operator|.
name|STARTED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"HistoryServer failed to start"
argument_list|)
throw|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
comment|//need to do this because historyServer.init creates a new Configuration
name|getConfig
argument_list|()
operator|.
name|set
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|,
name|historyServer
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|MRWebAppUtil
operator|.
name|setJHSWebappURLWithoutScheme
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|MRWebAppUtil
operator|.
name|getJHSWebappURLWithoutScheme
argument_list|(
name|historyServer
operator|.
name|getConfig
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniMRYARN ResourceManager address: "
operator|+
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniMRYARN ResourceManager web address: "
operator|+
name|WebAppUtils
operator|.
name|getRMWebAppURLWithoutScheme
argument_list|(
name|getConfig
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniMRYARN HistoryServer address: "
operator|+
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniMRYARN HistoryServer web address: "
operator|+
name|getResolvedMRHistoryWebAppURLWithoutScheme
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|HttpConfig
operator|.
name|isSecure
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
specifier|synchronized
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|historyServer
operator|!=
literal|null
condition|)
block|{
name|historyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getHistoryServer ()
specifier|public
name|JobHistoryServer
name|getHistoryServer
parameter_list|()
block|{
return|return
name|this
operator|.
name|historyServer
return|;
block|}
block|}
end_class

end_unit

