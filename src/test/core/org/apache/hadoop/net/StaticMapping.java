begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|conf
operator|.
name|Configured
import|;
end_import

begin_comment
comment|/**  * Implements the {@link DNSToSwitchMapping} via static mappings. Used  * in testcases that simulate racks.  *  */
end_comment

begin_class
DECL|class|StaticMapping
specifier|public
class|class
name|StaticMapping
extends|extends
name|Configured
implements|implements
name|DNSToSwitchMapping
block|{
DECL|method|setconf (Configuration conf)
specifier|public
name|void
name|setconf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
index|[]
name|mappings
init|=
name|conf
operator|.
name|getStrings
argument_list|(
literal|"hadoop.configured.node.mapping"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappings
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mappings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|str
init|=
name|mappings
index|[
name|i
index|]
decl_stmt|;
name|String
name|host
init|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|str
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rack
init|=
name|str
operator|.
name|substring
argument_list|(
name|str
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|addNodeToRack
argument_list|(
name|host
argument_list|,
name|rack
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* Only one instance per JVM */
DECL|field|nameToRackMap
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nameToRackMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|addNodeToRack (String name, String rackId)
specifier|static
specifier|synchronized
specifier|public
name|void
name|addNodeToRack
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|rackId
parameter_list|)
block|{
name|nameToRackMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|rackId
argument_list|)
expr_stmt|;
block|}
DECL|method|resolve (List<String> names)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|m
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|nameToRackMap
init|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|String
name|rackId
decl_stmt|;
if|if
condition|(
operator|(
name|rackId
operator|=
name|nameToRackMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|add
argument_list|(
name|rackId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|m
operator|.
name|add
argument_list|(
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|m
return|;
block|}
block|}
block|}
end_class

end_unit

