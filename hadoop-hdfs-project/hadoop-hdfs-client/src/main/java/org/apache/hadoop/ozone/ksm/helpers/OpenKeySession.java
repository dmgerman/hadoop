begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
operator|.
name|helpers
package|;
end_package

begin_comment
comment|/**  * This class represents a open key "session". A session here means a key is  * opened by a specific client, the client sends the handler to server, such  * that servers can recognize this client, and thus know how to close the key.  */
end_comment

begin_class
DECL|class|OpenKeySession
specifier|public
class|class
name|OpenKeySession
block|{
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|keyInfo
specifier|private
specifier|final
name|KsmKeyInfo
name|keyInfo
decl_stmt|;
DECL|method|OpenKeySession (int id, KsmKeyInfo info)
specifier|public
name|OpenKeySession
parameter_list|(
name|int
name|id
parameter_list|,
name|KsmKeyInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|keyInfo
operator|=
name|info
expr_stmt|;
block|}
DECL|method|getKeyInfo ()
specifier|public
name|KsmKeyInfo
name|getKeyInfo
parameter_list|()
block|{
return|return
name|keyInfo
return|;
block|}
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

