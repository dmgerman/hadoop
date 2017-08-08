begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.io
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
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * An input stream with length.  */
end_comment

begin_class
DECL|class|LengthInputStream
specifier|public
class|class
name|LengthInputStream
extends|extends
name|FilterInputStream
block|{
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
comment|/**    * Create an stream.    * @param in the underlying input stream.    * @param length the length of the stream.    */
DECL|method|LengthInputStream (InputStream in, long length)
specifier|public
name|LengthInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/** @return the length. */
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
DECL|method|getWrappedStream ()
specifier|public
name|InputStream
name|getWrappedStream
parameter_list|()
block|{
return|return
name|in
return|;
block|}
block|}
end_class

end_unit

