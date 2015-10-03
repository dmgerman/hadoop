begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|protocol
operator|.
name|LocatedBlock
import|;
end_import

begin_comment
comment|/**  * Wrapper for {@link BlockLocation} that also includes a {@link LocatedBlock},  * allowing more detailed queries to the datanode about a block.  *  */
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
DECL|class|HdfsBlockLocation
specifier|public
class|class
name|HdfsBlockLocation
extends|extends
name|BlockLocation
block|{
DECL|field|block
specifier|private
specifier|final
name|LocatedBlock
name|block
decl_stmt|;
DECL|method|HdfsBlockLocation (BlockLocation loc, LocatedBlock block)
specifier|public
name|HdfsBlockLocation
parameter_list|(
name|BlockLocation
name|loc
parameter_list|,
name|LocatedBlock
name|block
parameter_list|)
block|{
comment|// Initialize with data from passed in BlockLocation
name|super
argument_list|(
name|loc
argument_list|)
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
block|}
DECL|method|getLocatedBlock ()
specifier|public
name|LocatedBlock
name|getLocatedBlock
parameter_list|()
block|{
return|return
name|block
return|;
block|}
block|}
end_class

end_unit

