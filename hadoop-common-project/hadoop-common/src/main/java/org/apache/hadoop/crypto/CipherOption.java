begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
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
comment|/**  * Used between client and server to negotiate the   * cipher suite, key and iv.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CipherOption
specifier|public
class|class
name|CipherOption
block|{
DECL|field|suite
specifier|private
specifier|final
name|CipherSuite
name|suite
decl_stmt|;
DECL|field|inKey
specifier|private
specifier|final
name|byte
index|[]
name|inKey
decl_stmt|;
DECL|field|inIv
specifier|private
specifier|final
name|byte
index|[]
name|inIv
decl_stmt|;
DECL|field|outKey
specifier|private
specifier|final
name|byte
index|[]
name|outKey
decl_stmt|;
DECL|field|outIv
specifier|private
specifier|final
name|byte
index|[]
name|outIv
decl_stmt|;
DECL|method|CipherOption (CipherSuite suite)
specifier|public
name|CipherOption
parameter_list|(
name|CipherSuite
name|suite
parameter_list|)
block|{
name|this
argument_list|(
name|suite
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CipherOption (CipherSuite suite, byte[] inKey, byte[] inIv, byte[] outKey, byte[] outIv)
specifier|public
name|CipherOption
parameter_list|(
name|CipherSuite
name|suite
parameter_list|,
name|byte
index|[]
name|inKey
parameter_list|,
name|byte
index|[]
name|inIv
parameter_list|,
name|byte
index|[]
name|outKey
parameter_list|,
name|byte
index|[]
name|outIv
parameter_list|)
block|{
name|this
operator|.
name|suite
operator|=
name|suite
expr_stmt|;
name|this
operator|.
name|inKey
operator|=
name|inKey
expr_stmt|;
name|this
operator|.
name|inIv
operator|=
name|inIv
expr_stmt|;
name|this
operator|.
name|outKey
operator|=
name|outKey
expr_stmt|;
name|this
operator|.
name|outIv
operator|=
name|outIv
expr_stmt|;
block|}
DECL|method|getCipherSuite ()
specifier|public
name|CipherSuite
name|getCipherSuite
parameter_list|()
block|{
return|return
name|suite
return|;
block|}
DECL|method|getInKey ()
specifier|public
name|byte
index|[]
name|getInKey
parameter_list|()
block|{
return|return
name|inKey
return|;
block|}
DECL|method|getInIv ()
specifier|public
name|byte
index|[]
name|getInIv
parameter_list|()
block|{
return|return
name|inIv
return|;
block|}
DECL|method|getOutKey ()
specifier|public
name|byte
index|[]
name|getOutKey
parameter_list|()
block|{
return|return
name|outKey
return|;
block|}
DECL|method|getOutIv ()
specifier|public
name|byte
index|[]
name|getOutIv
parameter_list|()
block|{
return|return
name|outIv
return|;
block|}
block|}
end_class

end_unit

