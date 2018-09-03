begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * One key can be stored in one or more containers as one or more blocks.  * This class represents one such block instance.  */
end_comment

begin_class
DECL|class|OzoneKeyLocation
specifier|public
class|class
name|OzoneKeyLocation
block|{
comment|/**    * Which container this key stored.    */
DECL|field|containerID
specifier|private
specifier|final
name|long
name|containerID
decl_stmt|;
comment|/**    * Which block this key stored inside a container.    */
DECL|field|localID
specifier|private
specifier|final
name|long
name|localID
decl_stmt|;
comment|/**    * Data length of this key replica.    */
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
comment|/**    * Offset of this key.    */
DECL|field|offset
specifier|private
specifier|final
name|long
name|offset
decl_stmt|;
comment|/**    * Constructs OzoneKeyLocation.    */
DECL|method|OzoneKeyLocation (long containerID, long localID, long length, long offset)
specifier|public
name|OzoneKeyLocation
parameter_list|(
name|long
name|containerID
parameter_list|,
name|long
name|localID
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|offset
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
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**    * Returns the containerID of this Key.    */
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
comment|/**    * Returns the localID of this Key.    */
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
comment|/**    * Returns the length of this Key.    */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * Returns the offset of this Key.    */
DECL|method|getOffset ()
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
block|}
end_class

end_unit

