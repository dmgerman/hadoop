begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Encapsulates various options related to how fine-grained data checksums are  * combined into block-level checksums.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockChecksumOptions
specifier|public
class|class
name|BlockChecksumOptions
block|{
DECL|field|blockChecksumType
specifier|private
specifier|final
name|BlockChecksumType
name|blockChecksumType
decl_stmt|;
DECL|field|stripeLength
specifier|private
specifier|final
name|long
name|stripeLength
decl_stmt|;
DECL|method|BlockChecksumOptions ( BlockChecksumType blockChecksumType, long stripeLength)
specifier|public
name|BlockChecksumOptions
parameter_list|(
name|BlockChecksumType
name|blockChecksumType
parameter_list|,
name|long
name|stripeLength
parameter_list|)
block|{
name|this
operator|.
name|blockChecksumType
operator|=
name|blockChecksumType
expr_stmt|;
name|this
operator|.
name|stripeLength
operator|=
name|stripeLength
expr_stmt|;
block|}
DECL|method|BlockChecksumOptions (BlockChecksumType blockChecksumType)
specifier|public
name|BlockChecksumOptions
parameter_list|(
name|BlockChecksumType
name|blockChecksumType
parameter_list|)
block|{
name|this
argument_list|(
name|blockChecksumType
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlockChecksumType ()
specifier|public
name|BlockChecksumType
name|getBlockChecksumType
parameter_list|()
block|{
return|return
name|blockChecksumType
return|;
block|}
DECL|method|getStripeLength ()
specifier|public
name|long
name|getStripeLength
parameter_list|()
block|{
return|return
name|stripeLength
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
name|String
operator|.
name|format
argument_list|(
literal|"blockChecksumType=%s, stripedLength=%d"
argument_list|,
name|blockChecksumType
argument_list|,
name|stripeLength
argument_list|)
return|;
block|}
block|}
end_class

end_unit

