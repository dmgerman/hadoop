begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
package|;
end_package

begin_comment
comment|/**  * A wrapper of block level data source/output that {@link ECChunk}s can be  * extracted from. For HDFS, it can be an HDFS block (250MB). Note it only cares  * about erasure coding specific logic thus avoids coupling with any HDFS block  * details. We can have something like HdfsBlock extend it.  */
end_comment

begin_class
DECL|class|ECBlock
specifier|public
class|class
name|ECBlock
block|{
DECL|field|isParity
specifier|private
name|boolean
name|isParity
decl_stmt|;
DECL|field|isErased
specifier|private
name|boolean
name|isErased
decl_stmt|;
comment|/**    * A default constructor. isParity and isErased are false by default.    */
DECL|method|ECBlock ()
specifier|public
name|ECBlock
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * A constructor specifying isParity and isErased.    * @param isParity is a parity block    * @param isErased is erased or not    */
DECL|method|ECBlock (boolean isParity, boolean isErased)
specifier|public
name|ECBlock
parameter_list|(
name|boolean
name|isParity
parameter_list|,
name|boolean
name|isErased
parameter_list|)
block|{
name|this
operator|.
name|isParity
operator|=
name|isParity
expr_stmt|;
name|this
operator|.
name|isErased
operator|=
name|isErased
expr_stmt|;
block|}
comment|/**    * Set true if it's for a parity block.    * @param isParity is parity or not    */
DECL|method|setParity (boolean isParity)
specifier|public
name|void
name|setParity
parameter_list|(
name|boolean
name|isParity
parameter_list|)
block|{
name|this
operator|.
name|isParity
operator|=
name|isParity
expr_stmt|;
block|}
comment|/**    * Set true if the block is missing.    * @param isErased is erased or not    */
DECL|method|setErased (boolean isErased)
specifier|public
name|void
name|setErased
parameter_list|(
name|boolean
name|isErased
parameter_list|)
block|{
name|this
operator|.
name|isErased
operator|=
name|isErased
expr_stmt|;
block|}
comment|/**    *    * @return true if it's parity block, otherwise false    */
DECL|method|isParity ()
specifier|public
name|boolean
name|isParity
parameter_list|()
block|{
return|return
name|isParity
return|;
block|}
comment|/**    *    * @return true if it's erased due to erasure, otherwise false    */
DECL|method|isErased ()
specifier|public
name|boolean
name|isErased
parameter_list|()
block|{
return|return
name|isErased
return|;
block|}
block|}
end_class

end_unit

