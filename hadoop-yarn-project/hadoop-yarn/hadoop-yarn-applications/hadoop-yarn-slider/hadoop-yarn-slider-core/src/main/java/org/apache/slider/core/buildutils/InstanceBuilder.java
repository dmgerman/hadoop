begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.buildutils
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|buildutils
package|;
end_package

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
name|CommonConfigurationKeysPublic
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|InternalKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|StatusKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|SliderXmlConfKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|CoreFileSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|AggregateConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|ConfTreeOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|MapOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
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
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|BadConfigException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|ErrorStrings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|ConfPersister
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|InstancePaths
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|LockAcquireFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|LockHeldAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|zk
operator|.
name|ZKPathBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|zk
operator|.
name|ZookeeperUtils
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
name|IOException
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
name|slider
operator|.
name|api
operator|.
name|InternalKeys
operator|.
name|INTERNAL_ADDONS_DIR_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|InternalKeys
operator|.
name|INTERNAL_APPDEF_DIR_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|InternalKeys
operator|.
name|INTERNAL_QUEUE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_AM_TMP_DIR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_TMP_DIR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_APPLICATION_HOME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_APPLICATION_IMAGE_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_DATA_DIR_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_GENERATED_CONF_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|INTERNAL_SNAPSHOT_CONF_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|ZOOKEEPER_HOSTS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|ZOOKEEPER_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|OptionKeys
operator|.
name|ZOOKEEPER_QUORUM
import|;
end_import

begin_comment
comment|/**  * Build up the instance of a cluster.  */
end_comment

begin_class
DECL|class|InstanceBuilder
specifier|public
class|class
name|InstanceBuilder
block|{
DECL|field|clustername
specifier|private
specifier|final
name|String
name|clustername
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|coreFS
specifier|private
specifier|final
name|CoreFileSystem
name|coreFS
decl_stmt|;
DECL|field|instancePaths
specifier|private
specifier|final
name|InstancePaths
name|instancePaths
decl_stmt|;
DECL|field|instanceDescription
specifier|private
name|AggregateConf
name|instanceDescription
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InstanceBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|InstanceBuilder (CoreFileSystem coreFileSystem, Configuration conf, String clustername)
specifier|public
name|InstanceBuilder
parameter_list|(
name|CoreFileSystem
name|coreFileSystem
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|clustername
parameter_list|)
block|{
name|this
operator|.
name|clustername
operator|=
name|clustername
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|coreFS
operator|=
name|coreFileSystem
expr_stmt|;
name|Path
name|instanceDir
init|=
name|coreFileSystem
operator|.
name|buildClusterDirPath
argument_list|(
name|clustername
argument_list|)
decl_stmt|;
name|instancePaths
operator|=
operator|new
name|InstancePaths
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
block|}
DECL|method|getInstanceDescription ()
specifier|public
name|AggregateConf
name|getInstanceDescription
parameter_list|()
block|{
return|return
name|instanceDescription
return|;
block|}
DECL|method|getInstancePaths ()
specifier|public
name|InstancePaths
name|getInstancePaths
parameter_list|()
block|{
return|return
name|instancePaths
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Builder working with "
operator|+
name|clustername
operator|+
literal|" at "
operator|+
name|getInstanceDir
argument_list|()
return|;
block|}
DECL|method|getInstanceDir ()
specifier|private
name|Path
name|getInstanceDir
parameter_list|()
block|{
return|return
name|instancePaths
operator|.
name|instanceDir
return|;
block|}
comment|/**    * Initial part of the build process    * @param instanceConf    * @param provider    */
DECL|method|init ( String provider, AggregateConf instanceConf)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|provider
parameter_list|,
name|AggregateConf
name|instanceConf
parameter_list|)
block|{
name|this
operator|.
name|instanceDescription
operator|=
name|instanceConf
expr_stmt|;
comment|//internal is extended
name|ConfTreeOperations
name|internalOps
init|=
name|instanceConf
operator|.
name|getInternalOperations
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|md
init|=
name|internalOps
operator|.
name|getConfTree
argument_list|()
operator|.
name|metadata
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|md
operator|.
name|put
argument_list|(
name|StatusKeys
operator|.
name|INFO_CREATE_TIME_HUMAN
argument_list|,
name|SliderUtils
operator|.
name|toGMTString
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|md
operator|.
name|put
argument_list|(
name|StatusKeys
operator|.
name|INFO_CREATE_TIME_MILLIS
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|time
argument_list|)
argument_list|)
expr_stmt|;
name|MapOperations
name|globalOptions
init|=
name|internalOps
operator|.
name|getGlobalOptions
argument_list|()
decl_stmt|;
name|BuildHelper
operator|.
name|addBuildMetadata
argument_list|(
name|md
argument_list|,
literal|"create"
argument_list|)
expr_stmt|;
name|SliderUtils
operator|.
name|setInfoTime
argument_list|(
name|md
argument_list|,
name|StatusKeys
operator|.
name|INFO_CREATE_TIME_HUMAN
argument_list|,
name|StatusKeys
operator|.
name|INFO_CREATE_TIME_MILLIS
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_AM_TMP_DIR
argument_list|,
name|instancePaths
operator|.
name|tmpPathAM
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_TMP_DIR
argument_list|,
name|instancePaths
operator|.
name|tmpPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_SNAPSHOT_CONF_PATH
argument_list|,
name|instancePaths
operator|.
name|snapshotConfPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_GENERATED_CONF_PATH
argument_list|,
name|instancePaths
operator|.
name|generatedConfPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_DATA_DIR_PATH
argument_list|,
name|instancePaths
operator|.
name|dataPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_APPDEF_DIR_PATH
argument_list|,
name|instancePaths
operator|.
name|appDefPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|INTERNAL_ADDONS_DIR_PATH
argument_list|,
name|instancePaths
operator|.
name|addonsPath
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|InternalKeys
operator|.
name|INTERNAL_PROVIDER_NAME
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|internalOps
operator|.
name|set
argument_list|(
name|OptionKeys
operator|.
name|APPLICATION_NAME
argument_list|,
name|clustername
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the queue used to start the application    * @param queue    * @throws BadConfigException    */
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
throws|throws
name|BadConfigException
block|{
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|SliderUtils
operator|.
name|isUnset
argument_list|(
name|queue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadConfigException
argument_list|(
literal|"Queue value cannot be empty."
argument_list|)
throw|;
block|}
name|instanceDescription
operator|.
name|getInternalOperations
argument_list|()
operator|.
name|set
argument_list|(
name|INTERNAL_QUEUE
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set up the image/app home path    * @param appImage   path in the DFS to the tar file    * @param appHomeDir other strategy: home dir    * @throws BadConfigException if both are found    */
DECL|method|setImageDetailsIfAvailable ( Path appImage, String appHomeDir)
specifier|public
name|void
name|setImageDetailsIfAvailable
parameter_list|(
name|Path
name|appImage
parameter_list|,
name|String
name|appHomeDir
parameter_list|)
throws|throws
name|BadConfigException
block|{
name|boolean
name|appHomeUnset
init|=
name|SliderUtils
operator|.
name|isUnset
argument_list|(
name|appHomeDir
argument_list|)
decl_stmt|;
comment|// App home or image
if|if
condition|(
name|appImage
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|appHomeUnset
condition|)
block|{
comment|// both args have been set
throw|throw
operator|new
name|BadConfigException
argument_list|(
name|ErrorStrings
operator|.
name|E_BOTH_IMAGE_AND_HOME_DIR_SPECIFIED
argument_list|)
throw|;
block|}
name|instanceDescription
operator|.
name|getInternalOperations
argument_list|()
operator|.
name|set
argument_list|(
name|INTERNAL_APPLICATION_IMAGE_PATH
argument_list|,
name|appImage
operator|.
name|toUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the alternative is app home, which now MUST be set
if|if
condition|(
operator|!
name|appHomeUnset
condition|)
block|{
name|instanceDescription
operator|.
name|getInternalOperations
argument_list|()
operator|.
name|set
argument_list|(
name|INTERNAL_APPLICATION_HOME
argument_list|,
name|appHomeDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Propagate any critical principals from the current site config down to the HBase one.    */
DECL|method|propagatePrincipals ()
specifier|public
name|void
name|propagatePrincipals
parameter_list|()
block|{
name|String
name|dfsPrincipal
init|=
name|conf
operator|.
name|get
argument_list|(
name|SliderXmlConfKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|dfsPrincipal
operator|!=
literal|null
condition|)
block|{
name|String
name|siteDfsPrincipal
init|=
name|OptionKeys
operator|.
name|SITE_XML_PREFIX
operator|+
name|SliderXmlConfKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
decl_stmt|;
name|instanceDescription
operator|.
name|getAppConfOperations
argument_list|()
operator|.
name|set
argument_list|(
name|siteDfsPrincipal
argument_list|,
name|dfsPrincipal
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|propagateFilename ()
specifier|public
name|void
name|propagateFilename
parameter_list|()
block|{
name|String
name|fsDefaultName
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
decl_stmt|;
name|instanceDescription
operator|.
name|getAppConfOperations
argument_list|()
operator|.
name|set
argument_list|(
name|OptionKeys
operator|.
name|SITE_XML_PREFIX
operator|+
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|fsDefaultName
argument_list|)
expr_stmt|;
name|instanceDescription
operator|.
name|getAppConfOperations
argument_list|()
operator|.
name|set
argument_list|(
name|OptionKeys
operator|.
name|SITE_XML_PREFIX
operator|+
name|SliderXmlConfKeys
operator|.
name|FS_DEFAULT_NAME_CLASSIC
argument_list|,
name|fsDefaultName
argument_list|)
expr_stmt|;
block|}
DECL|method|takeSnapshotOfConfDir (Path appconfdir)
specifier|public
name|void
name|takeSnapshotOfConfDir
parameter_list|(
name|Path
name|appconfdir
parameter_list|)
throws|throws
name|IOException
throws|,
name|BadConfigException
throws|,
name|BadClusterStateException
block|{
name|FileSystem
name|srcFS
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|appconfdir
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|srcFS
operator|.
name|isDirectory
argument_list|(
name|appconfdir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadConfigException
argument_list|(
literal|"Source Configuration directory is not valid: %s"
argument_list|,
name|appconfdir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
comment|// bulk copy
name|FsPermission
name|clusterPerms
init|=
name|coreFS
operator|.
name|getInstanceDirectoryPermissions
argument_list|()
decl_stmt|;
comment|// first the original from wherever to the DFS
name|SliderUtils
operator|.
name|copyDirectory
argument_list|(
name|conf
argument_list|,
name|appconfdir
argument_list|,
name|instancePaths
operator|.
name|snapshotConfPath
argument_list|,
name|clusterPerms
argument_list|)
expr_stmt|;
block|}
comment|/**    * Persist this    * @param appconfdir conf dir    * @param overwrite if true, we don't need to create cluster dir    * @throws IOException    * @throws SliderException    * @throws LockAcquireFailedException    */
DECL|method|persist (Path appconfdir, boolean overwrite)
specifier|public
name|void
name|persist
parameter_list|(
name|Path
name|appconfdir
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
throws|,
name|SliderException
throws|,
name|LockAcquireFailedException
block|{
if|if
condition|(
operator|!
name|overwrite
condition|)
block|{
name|coreFS
operator|.
name|createClusterDirectories
argument_list|(
name|instancePaths
argument_list|)
expr_stmt|;
block|}
name|ConfPersister
name|persister
init|=
operator|new
name|ConfPersister
argument_list|(
name|coreFS
argument_list|,
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
name|ConfDirSnapshotAction
name|action
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|appconfdir
operator|!=
literal|null
condition|)
block|{
name|action
operator|=
operator|new
name|ConfDirSnapshotAction
argument_list|(
name|appconfdir
argument_list|)
expr_stmt|;
block|}
name|persister
operator|.
name|save
argument_list|(
name|instanceDescription
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the ZK paths to the application options.     *     * @param zkBinding ZK binding    */
DECL|method|addZKBinding (ZKPathBuilder zkBinding)
specifier|public
name|void
name|addZKBinding
parameter_list|(
name|ZKPathBuilder
name|zkBinding
parameter_list|)
throws|throws
name|BadConfigException
block|{
name|String
name|quorum
init|=
name|zkBinding
operator|.
name|getAppQuorum
argument_list|()
decl_stmt|;
if|if
condition|(
name|SliderUtils
operator|.
name|isSet
argument_list|(
name|quorum
argument_list|)
condition|)
block|{
name|MapOperations
name|globalAppOptions
init|=
name|instanceDescription
operator|.
name|getAppConfOperations
argument_list|()
operator|.
name|getGlobalOptions
argument_list|()
decl_stmt|;
name|globalAppOptions
operator|.
name|put
argument_list|(
name|ZOOKEEPER_PATH
argument_list|,
name|zkBinding
operator|.
name|getAppPath
argument_list|()
argument_list|)
expr_stmt|;
name|globalAppOptions
operator|.
name|put
argument_list|(
name|ZOOKEEPER_QUORUM
argument_list|,
name|quorum
argument_list|)
expr_stmt|;
name|globalAppOptions
operator|.
name|put
argument_list|(
name|ZOOKEEPER_HOSTS
argument_list|,
name|ZookeeperUtils
operator|.
name|convertToHostsOnlyList
argument_list|(
name|quorum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Class to execute the snapshotting of the configuration directory    * while the persistence lock is held.     *     * This guarantees that there won't be an attempt to launch a cluster    * until the snapshot is complete -as the write lock won't be released    * until afterwards.    */
DECL|class|ConfDirSnapshotAction
specifier|private
class|class
name|ConfDirSnapshotAction
implements|implements
name|LockHeldAction
block|{
DECL|field|appconfdir
specifier|private
specifier|final
name|Path
name|appconfdir
decl_stmt|;
DECL|method|ConfDirSnapshotAction (Path appconfdir)
specifier|private
name|ConfDirSnapshotAction
parameter_list|(
name|Path
name|appconfdir
parameter_list|)
block|{
name|this
operator|.
name|appconfdir
operator|=
name|appconfdir
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
block|{
name|takeSnapshotOfConfDir
argument_list|(
name|appconfdir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

