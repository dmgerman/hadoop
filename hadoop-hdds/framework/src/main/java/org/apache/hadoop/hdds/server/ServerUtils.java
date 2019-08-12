begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|ipc
operator|.
name|RPC
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpRequestBase
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
name|net
operator|.
name|InetSocketAddress
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

begin_comment
comment|/**  * Generic utilities for all HDDS/Ozone servers.  */
end_comment

begin_class
DECL|class|ServerUtils
specifier|public
specifier|final
class|class
name|ServerUtils
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
name|ServerUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ServerUtils ()
specifier|private
name|ServerUtils
parameter_list|()
block|{   }
comment|/**    * Checks that a given value is with a range.    *    * For example, sanitizeUserArgs(17, 3, 5, 10)    * ensures that 17 is greater/equal than 3 * 5 and less/equal to 3 * 10.    *    * @param key           - config key of the value    * @param valueTocheck  - value to check    * @param baseKey       - config key of the baseValue    * @param baseValue     - the base value that is being used.    * @param minFactor     - range min - a 2 here makes us ensure that value    *                        valueTocheck is at least twice the baseValue.    * @param maxFactor     - range max    * @return long    */
DECL|method|sanitizeUserArgs (String key, long valueTocheck, String baseKey, long baseValue, long minFactor, long maxFactor)
specifier|public
specifier|static
name|long
name|sanitizeUserArgs
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|valueTocheck
parameter_list|,
name|String
name|baseKey
parameter_list|,
name|long
name|baseValue
parameter_list|,
name|long
name|minFactor
parameter_list|,
name|long
name|maxFactor
parameter_list|)
block|{
name|long
name|minLimit
init|=
name|baseValue
operator|*
name|minFactor
decl_stmt|;
name|long
name|maxLimit
init|=
name|baseValue
operator|*
name|maxFactor
decl_stmt|;
if|if
condition|(
name|valueTocheck
operator|<
name|minLimit
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} value = {} is smaller than min = {} based on"
operator|+
literal|" the key value of {}, reset to the min value {}."
argument_list|,
name|key
argument_list|,
name|valueTocheck
argument_list|,
name|minLimit
argument_list|,
name|baseKey
argument_list|,
name|minLimit
argument_list|)
expr_stmt|;
name|valueTocheck
operator|=
name|minLimit
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueTocheck
operator|>
name|maxLimit
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} value = {} is larger than max = {} based on"
operator|+
literal|" the key value of {}, reset to the max value {}."
argument_list|,
name|key
argument_list|,
name|valueTocheck
argument_list|,
name|maxLimit
argument_list|,
name|baseKey
argument_list|,
name|maxLimit
argument_list|)
expr_stmt|;
name|valueTocheck
operator|=
name|maxLimit
expr_stmt|;
block|}
return|return
name|valueTocheck
return|;
block|}
comment|/**    * After starting an RPC server, updates configuration with the actual    * listening address of that server. The listening address may be different    * from the configured address if, for example, the configured address uses    * port 0 to request use of an ephemeral port.    *    * @param conf configuration to update    * @param rpcAddressKey configuration key for RPC server address    * @param addr configured address    * @param rpcServer started RPC server.    */
DECL|method|updateRPCListenAddress ( OzoneConfiguration conf, String rpcAddressKey, InetSocketAddress addr, RPC.Server rpcServer)
specifier|public
specifier|static
name|InetSocketAddress
name|updateRPCListenAddress
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|rpcAddressKey
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|RPC
operator|.
name|Server
name|rpcServer
parameter_list|)
block|{
return|return
name|updateListenAddress
argument_list|(
name|conf
argument_list|,
name|rpcAddressKey
argument_list|,
name|addr
argument_list|,
name|rpcServer
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
return|;
block|}
DECL|method|updateRPCListenPort ( OzoneConfiguration conf, String rpcAddressKey, InetSocketAddress listenerAddress)
specifier|public
specifier|static
name|InetSocketAddress
name|updateRPCListenPort
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|rpcAddressKey
parameter_list|,
name|InetSocketAddress
name|listenerAddress
parameter_list|)
block|{
name|String
name|originalValue
init|=
name|conf
operator|.
name|get
argument_list|(
name|rpcAddressKey
argument_list|)
decl_stmt|;
comment|//remove existing port
name|originalValue
operator|=
name|originalValue
operator|.
name|replaceAll
argument_list|(
literal|":.*"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|rpcAddressKey
argument_list|,
name|originalValue
operator|+
literal|":"
operator|+
name|listenerAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|originalValue
argument_list|,
name|listenerAddress
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * After starting an server, updates configuration with the actual    * listening address of that server. The listening address may be different    * from the configured address if, for example, the configured address uses    * port 0 to request use of an ephemeral port.    *    * @param conf       configuration to update    * @param addressKey configuration key for RPC server address    * @param addr       configured address    * @param listenAddr the real listening address.    */
DECL|method|updateListenAddress (OzoneConfiguration conf, String addressKey, InetSocketAddress addr, InetSocketAddress listenAddr)
specifier|public
specifier|static
name|InetSocketAddress
name|updateListenAddress
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|addressKey
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|InetSocketAddress
name|listenAddr
parameter_list|)
block|{
name|InetSocketAddress
name|updatedAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|addr
operator|.
name|getHostString
argument_list|()
argument_list|,
name|listenAddr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|addressKey
argument_list|,
name|addr
operator|.
name|getHostString
argument_list|()
operator|+
literal|":"
operator|+
name|listenAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|updatedAddr
return|;
block|}
comment|/**    * Releases a http connection if the request is not null.    * @param request    */
DECL|method|releaseConnection (HttpRequestBase request)
specifier|public
specifier|static
name|void
name|releaseConnection
parameter_list|(
name|HttpRequestBase
name|request
parameter_list|)
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the location where SCM should store its metadata directories.    * Fall back to OZONE_METADATA_DIRS if not defined.    *    * @param conf    * @return    */
DECL|method|getScmDbDir (Configuration conf)
specifier|public
specifier|static
name|File
name|getScmDbDir
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|File
name|metadataDir
init|=
name|getDirectoryFromConfig
argument_list|(
name|conf
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_DIRS
argument_list|,
literal|"SCM"
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadataDir
operator|!=
literal|null
condition|)
block|{
return|return
name|metadataDir
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} is not configured. We recommend adding this setting. "
operator|+
literal|"Falling back to {} instead."
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_DIRS
argument_list|,
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
expr_stmt|;
return|return
name|getOzoneMetaDirPath
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Utility method to get value of a given key that corresponds to a DB    * directory.    * @param conf configuration bag    * @param key Key to test    * @param componentName Which component's key is this    * @return File created from the value of the key in conf.    */
DECL|method|getDirectoryFromConfig (Configuration conf, String key, String componentName)
specifier|public
specifier|static
name|File
name|getDirectoryFromConfig
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|componentName
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|metadirs
init|=
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadirs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bad config setting "
operator|+
name|key
operator|+
literal|". "
operator|+
name|componentName
operator|+
literal|" does not support multiple metadata dirs currently"
argument_list|)
throw|;
block|}
if|if
condition|(
name|metadirs
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|File
name|dbDirPath
init|=
operator|new
name|File
argument_list|(
name|metadirs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dbDirPath
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|dbDirPath
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to create directory "
operator|+
name|dbDirPath
operator|+
literal|" specified in configuration setting "
operator|+
name|key
argument_list|)
throw|;
block|}
return|return
name|dbDirPath
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Checks and creates Ozone Metadir Path if it does not exist.    *    * @param conf - Configuration    * @return File MetaDir    * @throws IllegalArgumentException if the configuration setting is not set    */
DECL|method|getOzoneMetaDirPath (Configuration conf)
specifier|public
specifier|static
name|File
name|getOzoneMetaDirPath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|File
name|dirPath
init|=
name|getDirectoryFromConfig
argument_list|(
name|conf
argument_list|,
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
literal|"Ozone"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dirPath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
operator|+
literal|" must be defined."
argument_list|)
throw|;
block|}
return|return
name|dirPath
return|;
block|}
DECL|method|setOzoneMetaDirPath (OzoneConfiguration conf, String path)
specifier|public
specifier|static
name|void
name|setOzoneMetaDirPath
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns with the service specific metadata directory.    *<p>    * If the directory is missing the method tries to create it.    *    * @param conf The ozone configuration object    * @param key The configuration key which specify the directory.    * @return The path of the directory.    */
DECL|method|getDBPath (Configuration conf, String key)
specifier|public
specifier|static
name|File
name|getDBPath
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|key
parameter_list|)
block|{
specifier|final
name|File
name|dbDirPath
init|=
name|getDirectoryFromConfig
argument_list|(
name|conf
argument_list|,
name|key
argument_list|,
literal|"OM"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dbDirPath
operator|!=
literal|null
condition|)
block|{
return|return
name|dbDirPath
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} is not configured. We recommend adding this setting. "
operator|+
literal|"Falling back to {} instead."
argument_list|,
name|key
argument_list|,
name|HddsConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
expr_stmt|;
return|return
name|ServerUtils
operator|.
name|getOzoneMetaDirPath
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

