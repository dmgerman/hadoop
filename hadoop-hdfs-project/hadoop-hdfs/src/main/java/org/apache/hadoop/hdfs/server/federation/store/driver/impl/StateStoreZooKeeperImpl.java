begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver.impl
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
name|store
operator|.
name|driver
operator|.
name|impl
package|;
end_package

begin_import
import|import static
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
name|StateStoreUtils
operator|.
name|filterMultiple
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreUtils
operator|.
name|getRecordName
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
name|util
operator|.
name|curator
operator|.
name|ZKCuratorManager
operator|.
name|getNodePath
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
name|util
operator|.
name|Time
operator|.
name|monotonicNow
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|CuratorFramework
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|imps
operator|.
name|CuratorFrameworkState
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
name|DFSConfigKeys
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
name|driver
operator|.
name|StateStoreDriver
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
name|records
operator|.
name|BaseRecord
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
name|records
operator|.
name|Query
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
name|records
operator|.
name|QueryResult
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
name|curator
operator|.
name|ZKCuratorManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
comment|/**  * {@link StateStoreDriver} driver implementation that uses ZooKeeper as a  * backend.  *<p>  * The structure of the znodes in the ensemble is:  * PARENT_PATH  * |--- MOUNT  * |--- MEMBERSHIP  * |--- REBALANCER  * |--- ROUTERS  */
end_comment

begin_class
DECL|class|StateStoreZooKeeperImpl
specifier|public
class|class
name|StateStoreZooKeeperImpl
extends|extends
name|StateStoreSerializableImpl
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
name|StateStoreZooKeeperImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Configuration keys. */
DECL|field|FEDERATION_STORE_ZK_DRIVER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_ZK_DRIVER_PREFIX
init|=
name|DFSConfigKeys
operator|.
name|FEDERATION_STORE_PREFIX
operator|+
literal|"driver.zk."
decl_stmt|;
DECL|field|FEDERATION_STORE_ZK_PARENT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_ZK_PARENT_PATH
init|=
name|FEDERATION_STORE_ZK_DRIVER_PREFIX
operator|+
literal|"parent-path"
decl_stmt|;
DECL|field|FEDERATION_STORE_ZK_PARENT_PATH_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_STORE_ZK_PARENT_PATH_DEFAULT
init|=
literal|"/hdfs-federation"
decl_stmt|;
comment|/** Directory to store the state store data. */
DECL|field|baseZNode
specifier|private
name|String
name|baseZNode
decl_stmt|;
comment|/** Interface to ZooKeeper. */
DECL|field|zkManager
specifier|private
name|ZKCuratorManager
name|zkManager
decl_stmt|;
annotation|@
name|Override
DECL|method|initDriver ()
specifier|public
name|boolean
name|initDriver
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing ZooKeeper connection"
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|baseZNode
operator|=
name|conf
operator|.
name|get
argument_list|(
name|FEDERATION_STORE_ZK_PARENT_PATH
argument_list|,
name|FEDERATION_STORE_ZK_PARENT_PATH_DEFAULT
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|zkManager
operator|=
operator|new
name|ZKCuratorManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkManager
operator|.
name|start
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
literal|"Cannot initialize the ZK connection"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|initRecordStorage ( String className, Class<T> clazz)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|boolean
name|initRecordStorage
parameter_list|(
name|String
name|className
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
try|try
block|{
name|String
name|checkPath
init|=
name|getNodePath
argument_list|(
name|baseZNode
argument_list|,
name|className
argument_list|)
decl_stmt|;
name|zkManager
operator|.
name|createRootDirRecursively
argument_list|(
name|checkPath
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
literal|"Cannot initialize ZK node for {}: {}"
argument_list|,
name|className
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|zkManager
operator|!=
literal|null
condition|)
block|{
name|zkManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isDriverReady ()
specifier|public
name|boolean
name|isDriverReady
parameter_list|()
block|{
if|if
condition|(
name|zkManager
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CuratorFramework
name|curator
init|=
name|zkManager
operator|.
name|getCurator
argument_list|()
decl_stmt|;
if|if
condition|(
name|curator
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|curator
operator|.
name|getState
argument_list|()
operator|==
name|CuratorFrameworkState
operator|.
name|STARTED
return|;
block|}
annotation|@
name|Override
DECL|method|get (Class<T> clazz)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|QueryResult
argument_list|<
name|T
argument_list|>
name|get
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDriverReady
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|znode
init|=
name|getZNodeForClass
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zkManager
operator|.
name|getChildren
argument_list|(
name|znode
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
try|try
block|{
name|String
name|path
init|=
name|getNodePath
argument_list|(
name|znode
argument_list|,
name|child
argument_list|)
decl_stmt|;
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|String
name|data
init|=
name|zkManager
operator|.
name|getStringData
argument_list|(
name|path
argument_list|,
name|stat
argument_list|)
decl_stmt|;
name|boolean
name|corrupted
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
operator|||
name|data
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
comment|// All records should have data, otherwise this is corrupted
name|corrupted
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|T
name|record
init|=
name|createRecord
argument_list|(
name|data
argument_list|,
name|stat
argument_list|,
name|clazz
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|record
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
literal|"Cannot create record type \"{}\" from \"{}\": {}"
argument_list|,
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|data
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|corrupted
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|corrupted
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get data for {} at {}, cleaning corrupted data"
argument_list|,
name|child
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|zkManager
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
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
literal|"Cannot get data for {}: {}"
argument_list|,
name|child
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getMetrics
argument_list|()
operator|.
name|addFailure
argument_list|(
name|monotonicNow
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
literal|"Cannot get children for \""
operator|+
name|znode
operator|+
literal|"\": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|long
name|end
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|getMetrics
argument_list|()
operator|.
name|addRead
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
return|return
operator|new
name|QueryResult
argument_list|<
name|T
argument_list|>
argument_list|(
name|ret
argument_list|,
name|getTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|putAll ( List<T> records, boolean update, boolean error)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|boolean
name|putAll
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|records
parameter_list|,
name|boolean
name|update
parameter_list|,
name|boolean
name|error
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDriverReady
argument_list|()
expr_stmt|;
if|if
condition|(
name|records
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// All records should be the same
name|T
name|record0
init|=
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
name|recordClass
init|=
name|record0
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|String
name|znode
init|=
name|getZNodeForClass
argument_list|(
name|recordClass
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|boolean
name|status
init|=
literal|true
decl_stmt|;
for|for
control|(
name|T
name|record
range|:
name|records
control|)
block|{
name|String
name|primaryKey
init|=
name|getPrimaryKey
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|String
name|recordZNode
init|=
name|getNodePath
argument_list|(
name|znode
argument_list|,
name|primaryKey
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|serialize
argument_list|(
name|record
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeNode
argument_list|(
name|recordZNode
argument_list|,
name|data
argument_list|,
name|update
argument_list|,
name|error
argument_list|)
condition|)
block|{
name|status
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
condition|)
block|{
name|getMetrics
argument_list|()
operator|.
name|addWrite
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getMetrics
argument_list|()
operator|.
name|addFailure
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|remove ( Class<T> clazz, Query<T> query)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|int
name|remove
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|Query
argument_list|<
name|T
argument_list|>
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDriverReady
argument_list|()
expr_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// Read the current data
name|long
name|start
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|records
init|=
literal|null
decl_stmt|;
try|try
block|{
name|QueryResult
argument_list|<
name|T
argument_list|>
name|result
init|=
name|get
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|records
operator|=
name|result
operator|.
name|getRecords
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get existing records"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|getMetrics
argument_list|()
operator|.
name|addFailure
argument_list|(
name|monotonicNow
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|// Check the records to remove
name|String
name|znode
init|=
name|getZNodeForClass
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|recordsToRemove
init|=
name|filterMultiple
argument_list|(
name|query
argument_list|,
name|records
argument_list|)
decl_stmt|;
comment|// Remove the records
name|int
name|removed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|existingRecord
range|:
name|recordsToRemove
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing \"{}\""
argument_list|,
name|existingRecord
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|primaryKey
init|=
name|getPrimaryKey
argument_list|(
name|existingRecord
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|getNodePath
argument_list|(
name|znode
argument_list|,
name|primaryKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkManager
operator|.
name|delete
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|removed
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Did not remove \"{}\""
argument_list|,
name|existingRecord
argument_list|)
expr_stmt|;
block|}
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
literal|"Cannot remove \"{}\""
argument_list|,
name|existingRecord
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|getMetrics
argument_list|()
operator|.
name|addFailure
argument_list|(
name|monotonicNow
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|removed
operator|>
literal|0
condition|)
block|{
name|getMetrics
argument_list|()
operator|.
name|addRemove
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
return|return
name|removed
return|;
block|}
annotation|@
name|Override
DECL|method|removeAll (Class<T> clazz)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|boolean
name|removeAll
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
name|boolean
name|status
init|=
literal|true
decl_stmt|;
name|String
name|znode
init|=
name|getZNodeForClass
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting all children under {}"
argument_list|,
name|znode
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zkManager
operator|.
name|getChildren
argument_list|(
name|znode
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
name|String
name|path
init|=
name|getNodePath
argument_list|(
name|znode
argument_list|,
name|child
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|zkManager
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
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
literal|"Cannot remove {}: {}"
argument_list|,
name|znode
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
literal|false
expr_stmt|;
block|}
name|long
name|time
init|=
name|monotonicNow
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|status
condition|)
block|{
name|getMetrics
argument_list|()
operator|.
name|addRemove
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getMetrics
argument_list|()
operator|.
name|addFailure
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
return|return
name|status
return|;
block|}
DECL|method|writeNode ( String znode, byte[] bytes, boolean update, boolean error)
specifier|private
name|boolean
name|writeNode
parameter_list|(
name|String
name|znode
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|boolean
name|update
parameter_list|,
name|boolean
name|error
parameter_list|)
block|{
try|try
block|{
name|boolean
name|created
init|=
name|zkManager
operator|.
name|create
argument_list|(
name|znode
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|update
operator|&&
operator|!
name|created
operator|&&
name|error
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot write record \"{}\", it already exists"
argument_list|,
name|znode
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Write data
name|zkManager
operator|.
name|setData
argument_list|(
name|znode
argument_list|,
name|bytes
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
literal|"Cannot write record \"{}\": {}"
argument_list|,
name|znode
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Get the ZNode for a class.    *    * @param clazz Record class to evaluate.    * @return The ZNode for the class.    */
DECL|method|getZNodeForClass (Class<T> clazz)
specifier|private
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|String
name|getZNodeForClass
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|className
init|=
name|getRecordName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
return|return
name|getNodePath
argument_list|(
name|baseZNode
argument_list|,
name|className
argument_list|)
return|;
block|}
comment|/**    * Creates a record from a string returned by ZooKeeper.    *    * @param data The data to write.    * @param stat Stat of the data record to create.    * @param clazz The data record type to create.    * @return The created record.    * @throws IOException    */
DECL|method|createRecord ( String data, Stat stat, Class<T> clazz)
specifier|private
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|T
name|createRecord
parameter_list|(
name|String
name|data
parameter_list|,
name|Stat
name|stat
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|T
name|record
init|=
name|newRecord
argument_list|(
name|data
argument_list|,
name|clazz
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|record
operator|.
name|setDateCreated
argument_list|(
name|stat
operator|.
name|getCtime
argument_list|()
argument_list|)
expr_stmt|;
name|record
operator|.
name|setDateModified
argument_list|(
name|stat
operator|.
name|getMtime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
block|}
end_class

end_unit

