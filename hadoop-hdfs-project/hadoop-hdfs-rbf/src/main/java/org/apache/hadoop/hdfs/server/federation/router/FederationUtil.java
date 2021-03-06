begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
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
name|federation
operator|.
name|router
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|util
operator|.
name|EnumSet
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
name|hdfs
operator|.
name|DFSUtil
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|federation
operator|.
name|resolver
operator|.
name|FileSubclusterResolver
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
name|federation
operator|.
name|store
operator|.
name|StateStoreService
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
name|URLConnectionFactory
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
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
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
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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

begin_comment
comment|/**  * Utilities for managing HDFS federation.  */
end_comment

begin_class
DECL|class|FederationUtil
specifier|public
specifier|final
class|class
name|FederationUtil
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
name|FederationUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FederationUtil ()
specifier|private
name|FederationUtil
parameter_list|()
block|{
comment|// Utility Class
block|}
comment|/**    * Get a JMX data from a web endpoint.    *    * @param beanQuery JMX bean.    * @param webAddress Web address of the JMX endpoint.    * @param connectionFactory to open http/https connection.    * @param scheme to use for URL connection.    * @return JSON with the JMX data    */
DECL|method|getJmx (String beanQuery, String webAddress, URLConnectionFactory connectionFactory, String scheme)
specifier|public
specifier|static
name|JSONArray
name|getJmx
parameter_list|(
name|String
name|beanQuery
parameter_list|,
name|String
name|webAddress
parameter_list|,
name|URLConnectionFactory
name|connectionFactory
parameter_list|,
name|String
name|scheme
parameter_list|)
block|{
name|JSONArray
name|ret
init|=
literal|null
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|host
init|=
name|webAddress
decl_stmt|;
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|webAddress
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|webAddressSplit
init|=
name|webAddress
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|host
operator|=
name|webAddressSplit
index|[
literal|0
index|]
expr_stmt|;
name|port
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|webAddressSplit
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|URL
name|jmxURL
init|=
operator|new
name|URL
argument_list|(
name|scheme
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
literal|"/jmx?qry="
operator|+
name|beanQuery
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"JMX URL: {}"
argument_list|,
name|jmxURL
argument_list|)
expr_stmt|;
comment|// Create a URL connection
name|URLConnection
name|conn
init|=
name|connectionFactory
operator|.
name|openConnection
argument_list|(
name|jmxURL
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
decl_stmt|;
name|conn
operator|.
name|setConnectTimeout
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setReadTimeout
argument_list|(
literal|5
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
name|isr
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|String
name|jmxOutput
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Parse JSON
name|JSONObject
name|json
init|=
operator|new
name|JSONObject
argument_list|(
name|jmxOutput
argument_list|)
decl_stmt|;
name|ret
operator|=
name|json
operator|.
name|getJSONArray
argument_list|(
literal|"beans"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot read JMX bean {} from server {}: {}"
argument_list|,
name|beanQuery
argument_list|,
name|webAddress
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSONException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot parse JMX output for {} from server {}: {}"
argument_list|,
name|beanQuery
argument_list|,
name|webAddress
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot parse JMX output for {} from server {}: {}"
argument_list|,
name|beanQuery
argument_list|,
name|webAddress
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem closing {}"
argument_list|,
name|webAddress
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Fetch the Hadoop version string for this jar.    *    * @return Hadoop version string, e.g., 3.0.1.    */
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**    * Fetch the build/compile information for this jar.    *    * @return String Compilation info.    */
DECL|method|getCompileInfo ()
specifier|public
specifier|static
name|String
name|getCompileInfo
parameter_list|()
block|{
return|return
name|VersionInfo
operator|.
name|getDate
argument_list|()
operator|+
literal|" by "
operator|+
name|VersionInfo
operator|.
name|getUser
argument_list|()
operator|+
literal|" from "
operator|+
name|VersionInfo
operator|.
name|getBranch
argument_list|()
return|;
block|}
comment|/**    * Create an instance of an interface with a constructor using a context.    *    * @param conf Configuration for the class names.    * @param context Context object to pass to the instance.    * @param contextClass Type of the context passed to the constructor.    * @param clazz Class of the object to return.    * @return New instance of the specified class that implements the desired    *         interface and a single parameter constructor containing a    *         StateStore reference.    */
DECL|method|newInstance (final Configuration conf, final R context, final Class<R> contextClass, final Class<T> clazz)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
name|T
name|newInstance
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|R
name|context
parameter_list|,
specifier|final
name|Class
argument_list|<
name|R
argument_list|>
name|contextClass
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|contextClass
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
comment|// Default constructor if no context
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|()
return|;
block|}
else|else
block|{
comment|// Constructor with configuration but no context
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// Constructor with context
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|contextClass
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not instantiate: {}"
argument_list|,
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Creates an instance of a FileSubclusterResolver from the configuration.    *    * @param conf Configuration that defines the file resolver class.    * @param router Router service.    * @return New file subcluster resolver.    */
DECL|method|newFileSubclusterResolver ( Configuration conf, Router router)
specifier|public
specifier|static
name|FileSubclusterResolver
name|newFileSubclusterResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Router
name|router
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|FileSubclusterResolver
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|RBFConfigKeys
operator|.
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS
argument_list|,
name|RBFConfigKeys
operator|.
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS_DEFAULT
argument_list|,
name|FileSubclusterResolver
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|newInstance
argument_list|(
name|conf
argument_list|,
name|router
argument_list|,
name|Router
operator|.
name|class
argument_list|,
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of an ActiveNamenodeResolver from the configuration.    *    * @param conf Configuration that defines the namenode resolver class.    * @param stateStore State store passed to class constructor.    * @return New active namenode resolver.    */
DECL|method|newActiveNamenodeResolver ( Configuration conf, StateStoreService stateStore)
specifier|public
specifier|static
name|ActiveNamenodeResolver
name|newActiveNamenodeResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateStoreService
name|stateStore
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ActiveNamenodeResolver
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|RBFConfigKeys
operator|.
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS
argument_list|,
name|RBFConfigKeys
operator|.
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS_DEFAULT
argument_list|,
name|ActiveNamenodeResolver
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|newInstance
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|,
name|StateStoreService
operator|.
name|class
argument_list|,
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of DelegationTokenSecretManager from the    * configuration.    *    * @param conf Configuration that defines the token manager class.    * @return New delegation token secret manager.    */
specifier|public
specifier|static
name|AbstractDelegationTokenSecretManager
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
DECL|method|newSecretManager (Configuration conf)
name|newSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|>
name|clazz
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_DELEGATION_TOKEN_DRIVER_CLASS
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_DELEGATION_TOKEN_DRIVER_CLASS_DEFAULT
argument_list|,
name|AbstractDelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|newInstance
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Add the the number of children for an existing HdfsFileStatus object.    * @param dirStatus HdfsfileStatus object.    * @param children number of children to be added.    * @return HdfsFileStatus with the number of children specified.    */
DECL|method|updateMountPointStatus (HdfsFileStatus dirStatus, int children)
specifier|public
specifier|static
name|HdfsFileStatus
name|updateMountPointStatus
parameter_list|(
name|HdfsFileStatus
name|dirStatus
parameter_list|,
name|int
name|children
parameter_list|)
block|{
comment|// Get flags to set in new FileStatus.
name|EnumSet
argument_list|<
name|HdfsFileStatus
operator|.
name|Flags
argument_list|>
name|flags
init|=
name|DFSUtil
operator|.
name|getFlags
argument_list|(
name|dirStatus
operator|.
name|isEncrypted
argument_list|()
argument_list|,
name|dirStatus
operator|.
name|isErasureCoded
argument_list|()
argument_list|,
name|dirStatus
operator|.
name|isSnapshotEnabled
argument_list|()
argument_list|,
name|dirStatus
operator|.
name|hasAcl
argument_list|()
argument_list|)
decl_stmt|;
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|HdfsFileStatus
operator|.
name|Flags
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
operator|new
name|HdfsFileStatus
operator|.
name|Builder
argument_list|()
operator|.
name|atime
argument_list|(
name|dirStatus
operator|.
name|getAccessTime
argument_list|()
argument_list|)
operator|.
name|blocksize
argument_list|(
name|dirStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
operator|.
name|children
argument_list|(
name|children
argument_list|)
operator|.
name|ecPolicy
argument_list|(
name|dirStatus
operator|.
name|getErasureCodingPolicy
argument_list|()
argument_list|)
operator|.
name|feInfo
argument_list|(
name|dirStatus
operator|.
name|getFileEncryptionInfo
argument_list|()
argument_list|)
operator|.
name|fileId
argument_list|(
name|dirStatus
operator|.
name|getFileId
argument_list|()
argument_list|)
operator|.
name|group
argument_list|(
name|dirStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
operator|.
name|isdir
argument_list|(
name|dirStatus
operator|.
name|isDir
argument_list|()
argument_list|)
operator|.
name|length
argument_list|(
name|dirStatus
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|mtime
argument_list|(
name|dirStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|owner
argument_list|(
name|dirStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|path
argument_list|(
name|dirStatus
operator|.
name|getLocalNameInBytes
argument_list|()
argument_list|)
operator|.
name|perm
argument_list|(
name|dirStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
operator|.
name|replication
argument_list|(
name|dirStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|storagePolicy
argument_list|(
name|dirStatus
operator|.
name|getStoragePolicy
argument_list|()
argument_list|)
operator|.
name|symlink
argument_list|(
name|dirStatus
operator|.
name|getSymlinkInBytes
argument_list|()
argument_list|)
operator|.
name|flags
argument_list|(
name|flags
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

