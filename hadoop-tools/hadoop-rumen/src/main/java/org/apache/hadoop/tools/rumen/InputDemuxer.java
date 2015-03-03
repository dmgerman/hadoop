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
comment|/**  * {@link InputDemuxer} dem-ultiplexes the input files into individual input  * streams.  */
end_comment

begin_interface
DECL|interface|InputDemuxer
specifier|public
interface|interface
name|InputDemuxer
extends|extends
name|Closeable
block|{
comment|/**    * Bind the {@link InputDemuxer} to a particular file.    *     * @param path    *          The path to the file it should bind to.    * @param conf    *          Configuration    * @throws IOException    *     *           Returns true when the binding succeeds. If the file can be read    *           but is in the wrong format, returns false. IOException is    *           reserved for read errors.    */
DECL|method|bindTo (Path path, Configuration conf)
specifier|public
name|void
name|bindTo
parameter_list|(
name|Path
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the next&lt;name, input&gt; pair. The name should preserve the original job    * history file or job conf file name. The input object should be closed    * before calling getNext() again. The old input object would be invalid after    * calling getNext() again.    *     * @return the next&lt;name, input&gt; pair.    */
DECL|method|getNext ()
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|InputStream
argument_list|>
name|getNext
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

