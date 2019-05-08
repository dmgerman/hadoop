begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|DNConf
import|;
end_import

begin_comment
comment|/**  * Creates MappableBlockLoader.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MappableBlockLoaderFactory
specifier|public
specifier|final
class|class
name|MappableBlockLoaderFactory
block|{
DECL|method|MappableBlockLoaderFactory ()
specifier|private
name|MappableBlockLoaderFactory
parameter_list|()
block|{
comment|// Prevent instantiation
block|}
comment|/**    * Create a specific cache loader according to the configuration.    * If persistent memory volume is not configured, return a cache loader    * for DRAM cache. Otherwise, return a cache loader for pmem cache.    */
DECL|method|createCacheLoader (DNConf conf)
specifier|public
specifier|static
name|MappableBlockLoader
name|createCacheLoader
parameter_list|(
name|DNConf
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|.
name|getPmemVolumes
argument_list|()
operator|==
literal|null
operator|||
name|conf
operator|.
name|getPmemVolumes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MemoryMappableBlockLoader
argument_list|()
return|;
block|}
return|return
operator|new
name|PmemMappableBlockLoader
argument_list|()
return|;
block|}
block|}
end_class

end_unit

