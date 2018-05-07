begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|ToStringBuilder
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
import|;
end_import

begin_comment
comment|/**  * BlockID of ozone (containerID + localID)  */
end_comment

begin_class
DECL|class|BlockID
specifier|public
class|class
name|BlockID
block|{
DECL|field|containerID
specifier|private
name|long
name|containerID
decl_stmt|;
DECL|field|localID
specifier|private
name|long
name|localID
decl_stmt|;
DECL|method|BlockID (long containerID, long localID)
specifier|public
name|BlockID
parameter_list|(
name|long
name|containerID
parameter_list|,
name|long
name|localID
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
name|this
operator|.
name|localID
operator|=
name|localID
expr_stmt|;
block|}
DECL|method|getContainerID ()
specifier|public
name|long
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
DECL|method|getLocalID ()
specifier|public
name|long
name|getLocalID
parameter_list|()
block|{
return|return
name|localID
return|;
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
operator|new
name|ToStringBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|append
argument_list|(
literal|"containerID"
argument_list|,
name|containerID
argument_list|)
operator|.
name|append
argument_list|(
literal|"localID"
argument_list|,
name|localID
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getProtobuf ()
specifier|public
name|HddsProtos
operator|.
name|BlockID
name|getProtobuf
parameter_list|()
block|{
return|return
name|HddsProtos
operator|.
name|BlockID
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|setLocalID
argument_list|(
name|localID
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getFromProtobuf (HddsProtos.BlockID blockID)
specifier|public
specifier|static
name|BlockID
name|getFromProtobuf
parameter_list|(
name|HddsProtos
operator|.
name|BlockID
name|blockID
parameter_list|)
block|{
return|return
operator|new
name|BlockID
argument_list|(
name|blockID
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

