begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
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
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|Partitioner
import|;
end_import

begin_comment
comment|/**  * The partitioner partitions the map output according to the operation type.  * The partition number is the hash of the operation type modular the total  * number of the reducers.  */
end_comment

begin_class
DECL|class|SlivePartitioner
specifier|public
class|class
name|SlivePartitioner
implements|implements
name|Partitioner
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
block|{
annotation|@
name|Override
comment|// JobConfigurable
DECL|method|configure (JobConf conf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|)
block|{}
annotation|@
name|Override
comment|// Partitioner
DECL|method|getPartition (Text key, Text value, int numPartitions)
specifier|public
name|int
name|getPartition
parameter_list|(
name|Text
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|int
name|numPartitions
parameter_list|)
block|{
name|OperationOutput
name|oo
init|=
operator|new
name|OperationOutput
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
operator|(
name|oo
operator|.
name|getOperationType
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|&
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|%
name|numPartitions
return|;
block|}
block|}
end_class

end_unit

