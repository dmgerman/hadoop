begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
operator|.
name|hadoop
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
name|hadoop
operator|.
name|lib
operator|.
name|server
operator|.
name|BaseService
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
name|lib
operator|.
name|server
operator|.
name|ServiceException
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccess
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccessException
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
name|lib
operator|.
name|service
operator|.
name|Instrumentation
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
name|lib
operator|.
name|service
operator|.
name|Scheduler
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
name|lib
operator|.
name|util
operator|.
name|Check
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
name|lib
operator|.
name|util
operator|.
name|ConfigurationUtils
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
name|util
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
name|util
operator|.
name|VersionInfo
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
name|HashSet
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
name|Set
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
name|ConcurrentHashMap
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
name|TimeUnit
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
name|AtomicInteger
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileSystemAccessService
specifier|public
class|class
name|FileSystemAccessService
extends|extends
name|BaseService
implements|implements
name|FileSystemAccess
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
name|FileSystemAccessService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"hadoop"
decl_stmt|;
DECL|field|INSTRUMENTATION_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|INSTRUMENTATION_GROUP
init|=
literal|"hadoop"
decl_stmt|;
DECL|field|AUTHENTICATION_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|AUTHENTICATION_TYPE
init|=
literal|"authentication.type"
decl_stmt|;
DECL|field|KERBEROS_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_KEYTAB
init|=
literal|"authentication.kerberos.keytab"
decl_stmt|;
DECL|field|KERBEROS_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|KERBEROS_PRINCIPAL
init|=
literal|"authentication.kerberos.principal"
decl_stmt|;
DECL|field|FS_CACHE_PURGE_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|FS_CACHE_PURGE_FREQUENCY
init|=
literal|"filesystem.cache.purge.frequency"
decl_stmt|;
DECL|field|FS_CACHE_PURGE_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|FS_CACHE_PURGE_TIMEOUT
init|=
literal|"filesystem.cache.purge.timeout"
decl_stmt|;
DECL|field|NAME_NODE_WHITELIST
specifier|public
specifier|static
specifier|final
name|String
name|NAME_NODE_WHITELIST
init|=
literal|"name.node.whitelist"
decl_stmt|;
DECL|field|HADOOP_CONF_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_CONF_DIR
init|=
literal|"config.dir"
decl_stmt|;
DECL|field|HADOOP_CONF_FILES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|HADOOP_CONF_FILES
init|=
block|{
literal|"core-site.xml"
block|,
literal|"hdfs-site.xml"
block|}
decl_stmt|;
DECL|field|FILE_SYSTEM_SERVICE_CREATED
specifier|private
specifier|static
specifier|final
name|String
name|FILE_SYSTEM_SERVICE_CREATED
init|=
literal|"FileSystemAccessService.created"
decl_stmt|;
DECL|class|CachedFileSystem
specifier|private
specifier|static
class|class
name|CachedFileSystem
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|lastUse
specifier|private
name|long
name|lastUse
decl_stmt|;
DECL|field|timeout
specifier|private
name|long
name|timeout
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|CachedFileSystem (long timeout)
specifier|public
name|CachedFileSystem
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|lastUse
operator|=
operator|-
literal|1
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getFileSystem (Configuration conf)
specifier|synchronized
name|FileSystem
name|getFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|lastUse
operator|=
operator|-
literal|1
expr_stmt|;
name|count
operator|++
expr_stmt|;
return|return
name|fs
return|;
block|}
DECL|method|release ()
specifier|synchronized
name|void
name|release
parameter_list|()
throws|throws
name|IOException
block|{
name|count
operator|--
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|timeout
operator|==
literal|0
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
name|lastUse
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|lastUse
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// to avoid race conditions in the map cache adding removing entries
comment|// an entry in the cache remains forever, it just closes/opens filesystems
comment|// based on their utilization. Worse case scenario, the penalty we'll
comment|// pay is that the amount of entries in the cache will be the total
comment|// number of users in HDFS (which seems a resonable overhead).
DECL|method|purgeIfIdle ()
specifier|synchronized
name|boolean
name|purgeIfIdle
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|ret
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
operator|&&
name|lastUse
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastUse
operator|)
operator|>
name|timeout
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
name|lastUse
operator|=
operator|-
literal|1
expr_stmt|;
name|ret
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
DECL|method|FileSystemAccessService ()
specifier|public
name|FileSystemAccessService
parameter_list|()
block|{
name|super
argument_list|(
name|PREFIX
argument_list|)
expr_stmt|;
block|}
DECL|field|nameNodeWhitelist
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|nameNodeWhitelist
decl_stmt|;
DECL|field|serviceHadoopConf
name|Configuration
name|serviceHadoopConf
decl_stmt|;
DECL|field|unmanagedFileSystems
specifier|private
name|AtomicInteger
name|unmanagedFileSystems
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|fsCache
specifier|private
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|CachedFileSystem
argument_list|>
name|fsCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|CachedFileSystem
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|purgeTimeout
specifier|private
name|long
name|purgeTimeout
decl_stmt|;
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|ServiceException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using FileSystemAccess JARs version [{}]"
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|security
init|=
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|AUTHENTICATION_TYPE
argument_list|,
literal|"simple"
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|security
operator|.
name|equals
argument_list|(
literal|"kerberos"
argument_list|)
condition|)
block|{
name|String
name|defaultName
init|=
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|keytab
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
operator|+
literal|"/"
operator|+
name|defaultName
operator|+
literal|".keytab"
decl_stmt|;
name|keytab
operator|=
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|KERBEROS_KEYTAB
argument_list|,
name|keytab
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|keytab
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H01
argument_list|,
name|KERBEROS_KEYTAB
argument_list|)
throw|;
block|}
name|String
name|principal
init|=
name|defaultName
operator|+
literal|"/localhost@LOCALHOST"
decl_stmt|;
name|principal
operator|=
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|KERBEROS_PRINCIPAL
argument_list|,
name|principal
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|principal
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H01
argument_list|,
name|KERBEROS_PRINCIPAL
argument_list|)
throw|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
name|UserGroupInformation
operator|.
name|loginUserFromKeytab
argument_list|(
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H02
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using FileSystemAccess Kerberos authentication, principal [{}] keytab [{}]"
argument_list|,
name|principal
argument_list|,
name|keytab
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|security
operator|.
name|equals
argument_list|(
literal|"simple"
argument_list|)
condition|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using FileSystemAccess simple/pseudo authentication, principal [{}]"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H09
argument_list|,
name|security
argument_list|)
throw|;
block|}
name|String
name|hadoopConfDirProp
init|=
name|getServiceConfig
argument_list|()
operator|.
name|get
argument_list|(
name|HADOOP_CONF_DIR
argument_list|,
name|getServer
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|hadoopConfDir
init|=
operator|new
name|File
argument_list|(
name|hadoopConfDirProp
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hadoopConfDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|hadoopConfDir
operator|=
operator|new
name|File
argument_list|(
name|getServer
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hadoopConfDir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H10
argument_list|,
name|hadoopConfDir
argument_list|)
throw|;
block|}
try|try
block|{
name|serviceHadoopConf
operator|=
name|loadHadoopConf
argument_list|(
name|hadoopConfDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H11
argument_list|,
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"FileSystemAccess FileSystem configuration:"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|serviceHadoopConf
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"  {} = {}"
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
name|setRequiredServiceHadoopConf
argument_list|(
name|serviceHadoopConf
argument_list|)
expr_stmt|;
name|nameNodeWhitelist
operator|=
name|toLowerCase
argument_list|(
name|getServiceConfig
argument_list|()
operator|.
name|getTrimmedStringCollection
argument_list|(
name|NAME_NODE_WHITELIST
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|loadHadoopConf (File dir)
specifier|private
name|Configuration
name|loadHadoopConf
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|hadoopConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|HADOOP_CONF_FILES
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|hadoopConf
operator|.
name|addResource
argument_list|(
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|hadoopConf
return|;
block|}
annotation|@
name|Override
DECL|method|postInit ()
specifier|public
name|void
name|postInit
parameter_list|()
throws|throws
name|ServiceException
block|{
name|super
operator|.
name|postInit
argument_list|()
expr_stmt|;
name|Instrumentation
name|instrumentation
init|=
name|getServer
argument_list|()
operator|.
name|get
argument_list|(
name|Instrumentation
operator|.
name|class
argument_list|)
decl_stmt|;
name|instrumentation
operator|.
name|addVariable
argument_list|(
name|INSTRUMENTATION_GROUP
argument_list|,
literal|"unmanaged.fs"
argument_list|,
operator|new
name|Instrumentation
operator|.
name|Variable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|getValue
parameter_list|()
block|{
return|return
name|unmanagedFileSystems
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|instrumentation
operator|.
name|addSampler
argument_list|(
name|INSTRUMENTATION_GROUP
argument_list|,
literal|"unmanaged.fs"
argument_list|,
literal|60
argument_list|,
operator|new
name|Instrumentation
operator|.
name|Variable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
return|return
operator|(
name|long
operator|)
name|unmanagedFileSystems
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Scheduler
name|scheduler
init|=
name|getServer
argument_list|()
operator|.
name|get
argument_list|(
name|Scheduler
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|purgeInterval
init|=
name|getServiceConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|FS_CACHE_PURGE_FREQUENCY
argument_list|,
literal|60
argument_list|)
decl_stmt|;
name|purgeTimeout
operator|=
name|getServiceConfig
argument_list|()
operator|.
name|getLong
argument_list|(
name|FS_CACHE_PURGE_TIMEOUT
argument_list|,
literal|60
argument_list|)
expr_stmt|;
name|purgeTimeout
operator|=
operator|(
name|purgeTimeout
operator|>
literal|0
operator|)
condition|?
name|purgeTimeout
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|purgeTimeout
operator|>
literal|0
condition|)
block|{
name|scheduler
operator|.
name|schedule
argument_list|(
operator|new
name|FileSystemCachePurger
argument_list|()
argument_list|,
name|purgeInterval
argument_list|,
name|purgeInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FileSystemCachePurger
specifier|private
class|class
name|FileSystemCachePurger
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CachedFileSystem
name|cacheFs
range|:
name|fsCache
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|count
operator|+=
name|cacheFs
operator|.
name|purgeIfIdle
argument_list|()
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while purging filesystem, "
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Purged [{}} filesystem instances"
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toLowerCase (Collection<String> collection)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|toLowerCase
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|collection
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|collection
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|FileSystemAccess
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
block|{
return|return
operator|new
name|Class
index|[]
block|{
name|Instrumentation
operator|.
name|class
block|,
name|Scheduler
operator|.
name|class
block|}
return|;
block|}
DECL|method|getUGI (String user)
specifier|protected
name|UserGroupInformation
name|getUGI
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|user
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
return|;
block|}
DECL|method|setRequiredServiceHadoopConf (Configuration conf)
specifier|protected
name|void
name|setRequiredServiceHadoopConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|field|HTTPFS_FS_USER
specifier|private
specifier|static
specifier|final
name|String
name|HTTPFS_FS_USER
init|=
literal|"httpfs.fs.user"
decl_stmt|;
DECL|method|createFileSystem (Configuration namenodeConf)
specifier|protected
name|FileSystem
name|createFileSystem
parameter_list|(
name|Configuration
name|namenodeConf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|CachedFileSystem
name|newCachedFS
init|=
operator|new
name|CachedFileSystem
argument_list|(
name|purgeTimeout
argument_list|)
decl_stmt|;
name|CachedFileSystem
name|cachedFS
init|=
name|fsCache
operator|.
name|putIfAbsent
argument_list|(
name|user
argument_list|,
name|newCachedFS
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedFS
operator|==
literal|null
condition|)
block|{
name|cachedFS
operator|=
name|newCachedFS
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|namenodeConf
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HTTPFS_FS_USER
argument_list|,
name|user
argument_list|)
expr_stmt|;
return|return
name|cachedFS
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|closeFileSystem (FileSystem fs)
specifier|protected
name|void
name|closeFileSystem
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fsCache
operator|.
name|containsKey
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|HTTPFS_FS_USER
argument_list|)
argument_list|)
condition|)
block|{
name|fsCache
operator|.
name|get
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|HTTPFS_FS_USER
argument_list|)
argument_list|)
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|validateNamenode (String namenode)
specifier|protected
name|void
name|validateNamenode
parameter_list|(
name|String
name|namenode
parameter_list|)
throws|throws
name|FileSystemAccessException
block|{
if|if
condition|(
name|nameNodeWhitelist
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|nameNodeWhitelist
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|nameNodeWhitelist
operator|.
name|contains
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|namenode
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H05
argument_list|,
name|namenode
argument_list|,
literal|"not in whitelist"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|checkNameNodeHealth (FileSystem fileSystem)
specifier|protected
name|void
name|checkNameNodeHealth
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|FileSystemAccessException
block|{   }
annotation|@
name|Override
DECL|method|execute (String user, final Configuration conf, final FileSystemExecutor<T> executor)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|String
name|user
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|FileSystemExecutor
argument_list|<
name|T
argument_list|>
name|executor
parameter_list|)
throws|throws
name|FileSystemAccessException
block|{
name|Check
operator|.
name|notEmpty
argument_list|(
name|user
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notNull
argument_list|(
name|conf
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notNull
argument_list|(
name|executor
argument_list|,
literal|"executor"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|FILE_SYSTEM_SERVICE_CREATED
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H04
argument_list|)
throw|;
block|}
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
operator|==
literal|null
operator|||
name|conf
operator|.
name|getTrimmed
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H06
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
throw|;
block|}
try|try
block|{
name|validateNamenode
argument_list|(
operator|new
name|URI
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|getUGI
argument_list|(
name|user
argument_list|)
decl_stmt|;
return|return
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|createFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Instrumentation
name|instrumentation
init|=
name|getServer
argument_list|()
operator|.
name|get
argument_list|(
name|Instrumentation
operator|.
name|class
argument_list|)
decl_stmt|;
name|Instrumentation
operator|.
name|Cron
name|cron
init|=
name|instrumentation
operator|.
name|createCron
argument_list|()
decl_stmt|;
try|try
block|{
name|checkNameNodeHealth
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|cron
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|executor
operator|.
name|execute
argument_list|(
name|fs
argument_list|)
return|;
block|}
finally|finally
block|{
name|cron
operator|.
name|stop
argument_list|()
expr_stmt|;
name|instrumentation
operator|.
name|addCron
argument_list|(
name|INSTRUMENTATION_GROUP
argument_list|,
name|executor
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|cron
argument_list|)
expr_stmt|;
name|closeFileSystem
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|FileSystemAccessException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H03
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|createFileSystemInternal (String user, final Configuration conf)
specifier|public
name|FileSystem
name|createFileSystemInternal
parameter_list|(
name|String
name|user
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|FileSystemAccessException
block|{
name|Check
operator|.
name|notEmpty
argument_list|(
name|user
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notNull
argument_list|(
name|conf
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|conf
operator|.
name|getBoolean
argument_list|(
name|FILE_SYSTEM_SERVICE_CREATED
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H04
argument_list|)
throw|;
block|}
try|try
block|{
name|validateNamenode
argument_list|(
operator|new
name|URI
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|)
argument_list|)
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|getUGI
argument_list|(
name|user
argument_list|)
decl_stmt|;
return|return
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createFileSystem
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|FileSystemAccessException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|FileSystemAccessException
argument_list|(
name|FileSystemAccessException
operator|.
name|ERROR
operator|.
name|H08
argument_list|,
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
annotation|@
name|Override
DECL|method|createFileSystem (String user, final Configuration conf)
specifier|public
name|FileSystem
name|createFileSystem
parameter_list|(
name|String
name|user
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|FileSystemAccessException
block|{
name|unmanagedFileSystems
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|createFileSystemInternal
argument_list|(
name|user
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|releaseFileSystem (FileSystem fs)
specifier|public
name|void
name|releaseFileSystem
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|unmanagedFileSystems
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|closeFileSystem
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileSystemConfiguration ()
specifier|public
name|Configuration
name|getFileSystemConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|ConfigurationUtils
operator|.
name|copy
argument_list|(
name|serviceHadoopConf
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|FILE_SYSTEM_SERVICE_CREATED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Force-clear server-side umask to make HttpFS match WebHDFS behavior
name|conf
operator|.
name|set
argument_list|(
name|FsPermission
operator|.
name|UMASK_LABEL
argument_list|,
literal|"000"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

