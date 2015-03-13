begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|BlockListAsLongs
import|;
end_import

begin_comment
comment|/**  * Block report for a Datanode storage  */
end_comment

begin_class
DECL|class|StorageBlockReport
specifier|public
class|class
name|StorageBlockReport
block|{
DECL|field|storage
specifier|private
specifier|final
name|DatanodeStorage
name|storage
decl_stmt|;
DECL|field|blocks
specifier|private
specifier|final
name|BlockListAsLongs
name|blocks
decl_stmt|;
DECL|method|StorageBlockReport (DatanodeStorage storage, BlockListAsLongs blocks)
specifier|public
name|StorageBlockReport
parameter_list|(
name|DatanodeStorage
name|storage
parameter_list|,
name|BlockListAsLongs
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|getStorage ()
specifier|public
name|DatanodeStorage
name|getStorage
parameter_list|()
block|{
return|return
name|storage
return|;
block|}
DECL|method|getBlocks ()
specifier|public
name|BlockListAsLongs
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
block|}
end_class

end_unit

