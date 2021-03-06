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
comment|/**  * Reading JSON-encoded cluster topology and produce the parsed  * {@link LoggedNetworkTopology} object.  */
end_comment

begin_class
DECL|class|ClusterTopologyReader
specifier|public
class|class
name|ClusterTopologyReader
block|{
DECL|field|topology
specifier|private
name|LoggedNetworkTopology
name|topology
decl_stmt|;
DECL|method|readTopology (JsonObjectMapperParser<LoggedNetworkTopology> parser)
specifier|private
name|void
name|readTopology
parameter_list|(
name|JsonObjectMapperParser
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|topology
operator|=
name|parser
operator|.
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|topology
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Input file does not contain valid topology data."
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Constructor.    *     * @param path    *          Path to the JSON-encoded topology file, possibly compressed.    * @param conf    * @throws IOException    */
DECL|method|ClusterTopologyReader (Path path, Configuration conf)
specifier|public
name|ClusterTopologyReader
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
name|JsonObjectMapperParser
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|parser
init|=
operator|new
name|JsonObjectMapperParser
argument_list|<
name|LoggedNetworkTopology
argument_list|>
argument_list|(
name|path
argument_list|,
name|LoggedNetworkTopology
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|readTopology
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    *     * @param input    *          The input stream for the JSON-encoded topology data.    */
DECL|method|ClusterTopologyReader (InputStream input)
specifier|public
name|ClusterTopologyReader
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|JsonObjectMapperParser
argument_list|<
name|LoggedNetworkTopology
argument_list|>
name|parser
init|=
operator|new
name|JsonObjectMapperParser
argument_list|<
name|LoggedNetworkTopology
argument_list|>
argument_list|(
name|input
argument_list|,
name|LoggedNetworkTopology
operator|.
name|class
argument_list|)
decl_stmt|;
name|readTopology
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the {@link LoggedNetworkTopology} object.    *     * @return The {@link LoggedNetworkTopology} object parsed from the input.    */
DECL|method|get ()
specifier|public
name|LoggedNetworkTopology
name|get
parameter_list|()
block|{
return|return
name|topology
return|;
block|}
block|}
end_class

end_unit

