begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetImplTestUtils
import|;
end_import

begin_comment
comment|/**  * A factory for creating {@link FsDatasetImplTestUtils} objects.  */
end_comment

begin_class
DECL|class|FsDatasetImplTestUtilsFactory
specifier|public
specifier|final
class|class
name|FsDatasetImplTestUtilsFactory
extends|extends
name|FsDatasetTestUtils
operator|.
name|Factory
argument_list|<
name|FsDatasetTestUtils
argument_list|>
block|{
annotation|@
name|Override
DECL|method|newInstance (DataNode datanode)
specifier|public
name|FsDatasetTestUtils
name|newInstance
parameter_list|(
name|DataNode
name|datanode
parameter_list|)
block|{
return|return
operator|new
name|FsDatasetImplTestUtils
argument_list|(
name|datanode
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultNumOfDataDirs ()
specifier|public
name|int
name|getDefaultNumOfDataDirs
parameter_list|()
block|{
return|return
name|FsDatasetImplTestUtils
operator|.
name|DEFAULT_NUM_OF_DATA_DIRS
return|;
block|}
block|}
end_class

end_unit

