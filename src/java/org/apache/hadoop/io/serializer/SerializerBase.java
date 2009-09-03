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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
DECL|class|SerializerBase
specifier|public
specifier|abstract
class|class
name|SerializerBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Configured
implements|implements
name|Closeable
implements|,
name|Serializer
argument_list|<
name|T
argument_list|>
block|{
comment|/**    *<p>Prepare the serializer for writing.</p>    */
DECL|method|open (OutputStream out)
specifier|public
specifier|abstract
name|void
name|open
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *<p>Serialize<code>t</code> to the underlying output stream.</p>    */
DECL|method|serialize (T t)
specifier|public
specifier|abstract
name|void
name|serialize
parameter_list|(
name|T
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getMetadata ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

