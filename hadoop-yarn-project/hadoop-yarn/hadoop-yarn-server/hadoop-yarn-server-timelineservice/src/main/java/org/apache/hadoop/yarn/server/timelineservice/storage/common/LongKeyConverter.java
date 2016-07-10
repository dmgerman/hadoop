begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Encodes and decodes column names / row keys which are long.  */
end_comment

begin_class
DECL|class|LongKeyConverter
specifier|public
specifier|final
class|class
name|LongKeyConverter
implements|implements
name|KeyConverter
argument_list|<
name|Long
argument_list|>
block|{
comment|/**    * To delegate the actual work to.    */
DECL|field|longConverter
specifier|private
specifier|final
name|LongConverter
name|longConverter
init|=
operator|new
name|LongConverter
argument_list|()
decl_stmt|;
DECL|method|LongKeyConverter ()
specifier|public
name|LongKeyConverter
parameter_list|()
block|{   }
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #encode(java.lang.Object)    */
annotation|@
name|Override
DECL|method|encode (Long key)
specifier|public
name|byte
index|[]
name|encode
parameter_list|(
name|Long
name|key
parameter_list|)
block|{
try|try
block|{
comment|// IOException will not be thrown here as we are explicitly passing
comment|// Long.
return|return
name|longConverter
operator|.
name|encodeValue
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #decode(byte[])    */
annotation|@
name|Override
DECL|method|decode (byte[] bytes)
specifier|public
name|Long
name|decode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|Long
operator|)
name|longConverter
operator|.
name|decodeValue
argument_list|(
name|bytes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

