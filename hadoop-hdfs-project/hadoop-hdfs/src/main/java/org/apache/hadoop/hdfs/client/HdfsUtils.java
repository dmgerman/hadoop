begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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
name|protocol
operator|.
name|HdfsConstants
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
name|HdfsConstants
operator|.
name|SafeModeAction
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

begin_comment
comment|/**  * The public utility API for HDFS.  */
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
DECL|class|HdfsUtils
specifier|public
class|class
name|HdfsUtils
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
name|HdfsUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Is the HDFS healthy?    * HDFS is considered as healthy if it is up and not in safemode.    *    * @param uri the HDFS URI.  Note that the URI path is ignored.    * @return true if HDFS is healthy; false, otherwise.    */
DECL|method|isHealthy (URI uri)
specifier|public
specifier|static
name|boolean
name|isHealthy
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
comment|//check scheme
specifier|final
name|String
name|scheme
init|=
name|uri
operator|.
name|getScheme
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
operator|.
name|equalsIgnoreCase
argument_list|(
name|scheme
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The scheme is not "
operator|+
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
operator|+
literal|", uri="
operator|+
name|uri
argument_list|)
throw|;
block|}
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//disable FileSystem cache
name|conf
operator|.
name|setBoolean
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"fs.%s.impl.disable.cache"
argument_list|,
name|scheme
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//disable client retry for rpc connection and rpc calls
name|conf
operator|.
name|setBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|POLICY_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|safemode
init|=
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_GET
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
literal|"Is namenode in safemode? "
operator|+
name|safemode
operator|+
literal|"; uri="
operator|+
name|uri
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
return|return
operator|!
name|safemode
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Got an exception for uri="
operator|+
name|uri
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

