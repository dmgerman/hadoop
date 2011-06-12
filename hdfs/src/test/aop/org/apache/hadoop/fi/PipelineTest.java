begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fi
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
name|protocol
operator|.
name|DatanodeID
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
name|LocatedBlock
import|;
end_import

begin_comment
comment|/** A pipeline contains a list of datanodes. */
end_comment

begin_interface
DECL|interface|PipelineTest
specifier|public
interface|interface
name|PipelineTest
block|{
DECL|method|initPipeline (LocatedBlock lb)
specifier|public
name|Pipeline
name|initPipeline
parameter_list|(
name|LocatedBlock
name|lb
parameter_list|)
function_decl|;
DECL|method|getPipelineForDatanode (DatanodeID id)
specifier|public
name|Pipeline
name|getPipelineForDatanode
parameter_list|(
name|DatanodeID
name|id
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

