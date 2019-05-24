begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ratis.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ratis
operator|.
name|helpers
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
name|ozone
operator|.
name|om
operator|.
name|response
operator|.
name|OMClientResponse
import|;
end_import

begin_comment
comment|/**  * Entry in OzoneManagerDouble Buffer.  * @param<Response>  */
end_comment

begin_class
DECL|class|DoubleBufferEntry
specifier|public
class|class
name|DoubleBufferEntry
parameter_list|<
name|Response
extends|extends
name|OMClientResponse
parameter_list|>
block|{
DECL|field|trxLogIndex
specifier|private
name|long
name|trxLogIndex
decl_stmt|;
DECL|field|response
specifier|private
name|Response
name|response
decl_stmt|;
DECL|method|DoubleBufferEntry (long trxLogIndex, Response response)
specifier|public
name|DoubleBufferEntry
parameter_list|(
name|long
name|trxLogIndex
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
name|this
operator|.
name|trxLogIndex
operator|=
name|trxLogIndex
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|getTrxLogIndex ()
specifier|public
name|long
name|getTrxLogIndex
parameter_list|()
block|{
return|return
name|trxLogIndex
return|;
block|}
DECL|method|getResponse ()
specifier|public
name|Response
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

