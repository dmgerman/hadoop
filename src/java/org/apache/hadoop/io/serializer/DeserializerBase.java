begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configured
import|;
end_import

begin_class
DECL|class|DeserializerBase
specifier|public
specifier|abstract
class|class
name|DeserializerBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Configured
implements|implements
name|Closeable
implements|,
name|Deserializer
argument_list|<
name|T
argument_list|>
block|{
comment|/**    *<p>Prepare the deserializer for reading.</p>    */
DECL|method|open (InputStream in)
specifier|public
specifier|abstract
name|void
name|open
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *<p>    * Deserialize the next object from the underlying input stream.    * If the object<code>t</code> is non-null then this deserializer    *<i>may</i> set its internal state to the next object read from the input    * stream. Otherwise, if the object<code>t</code> is null a new    * deserialized object will be created.    *</p>    * @return the deserialized object    */
DECL|method|deserialize (T t)
specifier|public
specifier|abstract
name|T
name|deserialize
parameter_list|(
name|T
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

