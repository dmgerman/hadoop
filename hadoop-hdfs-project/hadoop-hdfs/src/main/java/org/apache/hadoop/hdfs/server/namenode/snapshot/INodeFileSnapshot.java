begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
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

begin_comment
comment|/**  *  INode representing a snapshot of a file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileSnapshot
specifier|public
class|class
name|INodeFileSnapshot
extends|extends
name|INodeFileWithSnapshot
block|{
comment|/** The file size at snapshot creation time. */
DECL|field|size
specifier|final
name|long
name|size
decl_stmt|;
DECL|method|INodeFileSnapshot (INodeFileWithSnapshot f)
name|INodeFileSnapshot
parameter_list|(
name|INodeFileWithSnapshot
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|f
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|insert
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeFileSize (boolean includesBlockInfoUnderConstruction)
specifier|public
name|long
name|computeFileSize
parameter_list|(
name|boolean
name|includesBlockInfoUnderConstruction
parameter_list|)
block|{
comment|//ignore includesBlockInfoUnderConstruction
comment|//since files in a snapshot are considered as closed.
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

