begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
name|namenode
operator|.
name|sps
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
name|StorageType
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
name|namenode
operator|.
name|sps
operator|.
name|StoragePolicySatisfier
operator|.
name|DatanodeMap
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
name|StorageReport
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
name|NetworkTopology
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
name|Time
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
comment|/**  * The Datanode cache Manager handles caching of {@link DatanodeStorageReport}.  *  * This class is instantiated by StoragePolicySatisifer. It maintains the array  * of datanode storage reports. It has a configurable refresh interval and  * periodically refresh the datanode cache by fetching latest  * {@link Context#getLiveDatanodeStorageReport()} once it reaches refresh  * interval.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DatanodeCacheManager
specifier|public
class|class
name|DatanodeCacheManager
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
name|DatanodeCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|datanodeMap
specifier|private
specifier|final
name|DatanodeMap
name|datanodeMap
decl_stmt|;
DECL|field|cluster
specifier|private
name|NetworkTopology
name|cluster
decl_stmt|;
comment|/**    * Interval between scans in milliseconds.    */
DECL|field|refreshIntervalMs
specifier|private
specifier|final
name|long
name|refreshIntervalMs
decl_stmt|;
DECL|field|lastAccessedTime
specifier|private
name|long
name|lastAccessedTime
decl_stmt|;
DECL|method|DatanodeCacheManager (Configuration conf)
specifier|public
name|DatanodeCacheManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|refreshIntervalMs
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SPS_DATANODE_CACHE_REFRESH_INTERVAL_MS
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_SPS_DATANODE_CACHE_REFRESH_INTERVAL_MS_DEFAULT
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DatanodeCacheManager refresh interval is {} milliseconds"
argument_list|,
name|refreshIntervalMs
argument_list|)
expr_stmt|;
name|datanodeMap
operator|=
operator|new
name|DatanodeMap
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the live datanodes and its storage details, which has available    * space (> 0) to schedule block moves. This will return array of datanodes    * from its local cache. It has a configurable refresh interval in millis and    * periodically refresh the datanode cache by fetching latest    * {@link Context#getLiveDatanodeStorageReport()} once it elapsed refresh    * interval.    *    * @throws IOException    */
DECL|method|getLiveDatanodeStorageReport ( Context spsContext)
specifier|public
name|DatanodeMap
name|getLiveDatanodeStorageReport
parameter_list|(
name|Context
name|spsContext
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|long
name|elapsedTimeMs
init|=
name|now
operator|-
name|lastAccessedTime
decl_stmt|;
name|boolean
name|refreshNeeded
init|=
name|elapsedTimeMs
operator|>=
name|refreshIntervalMs
decl_stmt|;
name|lastAccessedTime
operator|=
name|now
expr_stmt|;
if|if
condition|(
name|refreshNeeded
condition|)
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
literal|"elapsedTimeMs> refreshIntervalMs : {}> {},"
operator|+
literal|" so refreshing cache"
argument_list|,
name|elapsedTimeMs
argument_list|,
name|refreshIntervalMs
argument_list|)
expr_stmt|;
block|}
name|datanodeMap
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// clear all previously cached items.
comment|// Fetch live datanodes from namenode and prepare DatanodeMap.
name|DatanodeStorageReport
index|[]
name|liveDns
init|=
name|spsContext
operator|.
name|getLiveDatanodeStorageReport
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageReport
name|storage
range|:
name|liveDns
control|)
block|{
name|StorageReport
index|[]
name|storageReports
init|=
name|storage
operator|.
name|getStorageReports
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StorageType
argument_list|>
name|storageTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|remainingSizeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageReport
name|t
range|:
name|storageReports
control|)
block|{
if|if
condition|(
name|t
operator|.
name|getRemaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|storageTypes
operator|.
name|add
argument_list|(
name|t
operator|.
name|getStorage
argument_list|()
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|remainingSizeList
operator|.
name|add
argument_list|(
name|t
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|datanodeMap
operator|.
name|addTarget
argument_list|(
name|storage
operator|.
name|getDatanodeInfo
argument_list|()
argument_list|,
name|storageTypes
argument_list|,
name|remainingSizeList
argument_list|)
expr_stmt|;
block|}
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
literal|"LIVE datanodes: {}"
argument_list|,
name|datanodeMap
argument_list|)
expr_stmt|;
block|}
comment|// get network topology
name|cluster
operator|=
name|spsContext
operator|.
name|getNetworkTopology
argument_list|(
name|datanodeMap
argument_list|)
expr_stmt|;
block|}
return|return
name|datanodeMap
return|;
block|}
DECL|method|getCluster ()
name|NetworkTopology
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
block|}
end_class

end_unit

