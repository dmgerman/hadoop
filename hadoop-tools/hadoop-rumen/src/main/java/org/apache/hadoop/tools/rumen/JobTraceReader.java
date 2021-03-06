begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|Configuration
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
name|fs
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Reading JSON-encoded job traces and produce {@link LoggedJob} instances.  */
end_comment

begin_class
DECL|class|JobTraceReader
specifier|public
class|class
name|JobTraceReader
extends|extends
name|JsonObjectMapperParser
argument_list|<
name|LoggedJob
argument_list|>
block|{
comment|/**    * Constructor.    *     * @param path    *          Path to the JSON trace file, possibly compressed.    * @param conf    * @throws IOException    */
DECL|method|JobTraceReader (Path path, Configuration conf)
specifier|public
name|JobTraceReader
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|LoggedJob
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    *     * @param input    *          The input stream for the JSON trace.    */
DECL|method|JobTraceReader (InputStream input)
specifier|public
name|JobTraceReader
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|input
argument_list|,
name|LoggedJob
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

