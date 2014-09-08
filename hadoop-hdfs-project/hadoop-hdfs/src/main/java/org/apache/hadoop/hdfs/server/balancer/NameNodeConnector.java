begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.balancer
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
name|balancer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|OutputStream
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|FsServerDefaults
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
name|NameNodeProxies
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
name|AlreadyBeingCreatedException
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
name|ClientProtocol
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
name|DatanodeInfo
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
name|DatanodeReportType
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
name|protocol
operator|.
name|BlocksWithLocations
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
name|protocol
operator|.
name|DatanodeStorageReport
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
name|protocol
operator|.
name|NamenodeProtocol
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
name|protocol
operator|.
name|NamespaceInfo
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
name|ipc
operator|.
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * The class provides utilities for accessing a NameNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeConnector
specifier|public
class|class
name|NameNodeConnector
implements|implements
name|Closeable
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
name|NameNodeConnector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_NOT_CHANGED_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_NOT_CHANGED_ITERATIONS
init|=
literal|5
decl_stmt|;
comment|/** Create {@link NameNodeConnector} for the given namenodes. */
DECL|method|newNameNodeConnectors ( Collection<URI> namenodes, String name, Path idPath, Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|newNameNodeConnectors
parameter_list|(
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
parameter_list|,
name|String
name|name
parameter_list|,
name|Path
name|idPath
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|connectors
init|=
operator|new
name|ArrayList
argument_list|<
name|NameNodeConnector
argument_list|>
argument_list|(
name|namenodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|namenodes
control|)
block|{
name|NameNodeConnector
name|nnc
init|=
operator|new
name|NameNodeConnector
argument_list|(
name|name
argument_list|,
name|uri
argument_list|,
name|idPath
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|nnc
operator|.
name|getKeyManager
argument_list|()
operator|.
name|startBlockKeyUpdater
argument_list|()
expr_stmt|;
name|connectors
operator|.
name|add
argument_list|(
name|nnc
argument_list|)
expr_stmt|;
block|}
return|return
name|connectors
return|;
block|}
DECL|method|newNameNodeConnectors ( Map<URI, List<Path>> namenodes, String name, Path idPath, Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|newNameNodeConnectors
parameter_list|(
name|Map
argument_list|<
name|URI
argument_list|,
name|List
argument_list|<
name|Path
argument_list|>
argument_list|>
name|namenodes
parameter_list|,
name|String
name|name
parameter_list|,
name|Path
name|idPath
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|connectors
init|=
operator|new
name|ArrayList
argument_list|<
name|NameNodeConnector
argument_list|>
argument_list|(
name|namenodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|URI
argument_list|,
name|List
argument_list|<
name|Path
argument_list|>
argument_list|>
name|entry
range|:
name|namenodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NameNodeConnector
name|nnc
init|=
operator|new
name|NameNodeConnector
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|idPath
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|nnc
operator|.
name|getKeyManager
argument_list|()
operator|.
name|startBlockKeyUpdater
argument_list|()
expr_stmt|;
name|connectors
operator|.
name|add
argument_list|(
name|nnc
argument_list|)
expr_stmt|;
block|}
return|return
name|connectors
return|;
block|}
DECL|field|nameNodeUri
specifier|private
specifier|final
name|URI
name|nameNodeUri
decl_stmt|;
DECL|field|blockpoolID
specifier|private
specifier|final
name|String
name|blockpoolID
decl_stmt|;
DECL|field|namenode
specifier|private
specifier|final
name|NamenodeProtocol
name|namenode
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|ClientProtocol
name|client
decl_stmt|;
DECL|field|keyManager
specifier|private
specifier|final
name|KeyManager
name|keyManager
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|idPath
specifier|private
specifier|final
name|Path
name|idPath
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|OutputStream
name|out
decl_stmt|;
DECL|field|targetPaths
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|targetPaths
decl_stmt|;
DECL|field|notChangedIterations
specifier|private
name|int
name|notChangedIterations
init|=
literal|0
decl_stmt|;
DECL|method|NameNodeConnector (String name, URI nameNodeUri, Path idPath, List<Path> targetPaths, Configuration conf)
specifier|public
name|NameNodeConnector
parameter_list|(
name|String
name|name
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|,
name|Path
name|idPath
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|targetPaths
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nameNodeUri
operator|=
name|nameNodeUri
expr_stmt|;
name|this
operator|.
name|idPath
operator|=
name|idPath
expr_stmt|;
name|this
operator|.
name|targetPaths
operator|=
name|targetPaths
operator|==
literal|null
operator|||
name|targetPaths
operator|.
name|isEmpty
argument_list|()
condition|?
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
else|:
name|targetPaths
expr_stmt|;
name|this
operator|.
name|namenode
operator|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|this
operator|.
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|nameNodeUri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|NamespaceInfo
name|namespaceinfo
init|=
name|namenode
operator|.
name|versionRequest
argument_list|()
decl_stmt|;
name|this
operator|.
name|blockpoolID
operator|=
name|namespaceinfo
operator|.
name|getBlockPoolID
argument_list|()
expr_stmt|;
specifier|final
name|FsServerDefaults
name|defaults
init|=
name|fs
operator|.
name|getServerDefaults
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|keyManager
operator|=
operator|new
name|KeyManager
argument_list|(
name|blockpoolID
argument_list|,
name|namenode
argument_list|,
name|defaults
operator|.
name|getEncryptDataTransfer
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Exit if there is another one running.
name|out
operator|=
name|checkAndMarkRunning
argument_list|()
expr_stmt|;
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Another "
operator|+
name|name
operator|+
literal|" is running."
argument_list|)
throw|;
block|}
block|}
DECL|method|getDistributedFileSystem ()
specifier|public
name|DistributedFileSystem
name|getDistributedFileSystem
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
comment|/** @return the block pool ID */
DECL|method|getBlockpoolID ()
specifier|public
name|String
name|getBlockpoolID
parameter_list|()
block|{
return|return
name|blockpoolID
return|;
block|}
comment|/** @return blocks with locations. */
DECL|method|getBlocks (DatanodeInfo datanode, long size)
specifier|public
name|BlocksWithLocations
name|getBlocks
parameter_list|(
name|DatanodeInfo
name|datanode
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|namenode
operator|.
name|getBlocks
argument_list|(
name|datanode
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/** @return live datanode storage reports. */
DECL|method|getLiveDatanodeStorageReport ()
specifier|public
name|DatanodeStorageReport
index|[]
name|getLiveDatanodeStorageReport
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|client
operator|.
name|getDatanodeStorageReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
return|;
block|}
comment|/** @return the key manager */
DECL|method|getKeyManager ()
specifier|public
name|KeyManager
name|getKeyManager
parameter_list|()
block|{
return|return
name|keyManager
return|;
block|}
comment|/** @return the list of paths to scan/migrate */
DECL|method|getTargetPaths ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getTargetPaths
parameter_list|()
block|{
return|return
name|targetPaths
return|;
block|}
comment|/** Should the instance continue running? */
DECL|method|shouldContinue (long dispatchBlockMoveBytes)
specifier|public
name|boolean
name|shouldContinue
parameter_list|(
name|long
name|dispatchBlockMoveBytes
parameter_list|)
block|{
if|if
condition|(
name|dispatchBlockMoveBytes
operator|>
literal|0
condition|)
block|{
name|notChangedIterations
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|notChangedIterations
operator|++
expr_stmt|;
if|if
condition|(
name|notChangedIterations
operator|>=
name|MAX_NOT_CHANGED_ITERATIONS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No block has been moved for "
operator|+
name|notChangedIterations
operator|+
literal|" iterations. Exiting..."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * The idea for making sure that there is no more than one instance    * running in an HDFS is to create a file in the HDFS, writes the hostname    * of the machine on which the instance is running to the file, but did not    * close the file until it exits.     *     * This prevents the second instance from running because it can not    * creates the file while the first one is running.    *     * This method checks if there is any running instance. If no, mark yes.    * Note that this is an atomic operation.    *     * @return null if there is a running instance;    *         otherwise, the output stream to the newly created file.    */
DECL|method|checkAndMarkRunning ()
specifier|private
name|OutputStream
name|checkAndMarkRunning
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|idPath
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|out
return|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
if|if
condition|(
name|AlreadyBeingCreatedException
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getClassName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|keyManager
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close the output file
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|idPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete "
operator|+
name|idPath
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"[namenodeUri="
operator|+
name|nameNodeUri
operator|+
literal|", bpid="
operator|+
name|blockpoolID
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

