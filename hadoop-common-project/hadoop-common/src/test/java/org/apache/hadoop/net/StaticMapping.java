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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Implements the {@link DNSToSwitchMapping} via static mappings. Used  * in testcases that simulate racks, and in the  * {@link org.apache.hadoop.hdfs.MiniDFSCluster}  *  * A shared, static mapping is used; to reset it call {@link #resetMap()}.  *  * When an instance of the class has its {@link #setConf(Configuration)}  * method called, nodes listed in the configuration will be added to the map.  * These do not get removed when the instance is garbage collected.  *  * The switch mapping policy of this class is the same as for the  * {@link ScriptBasedMapping} -the presence of a non-empty topology script.  * The script itself is not used.  */
end_comment

begin_class
DECL|class|StaticMapping
specifier|public
class|class
name|StaticMapping
extends|extends
name|AbstractDNSToSwitchMapping
block|{
comment|/**    * Key to define the node mapping as a comma-delimited list of host=rack    * mappings, e.g.<code>host1=r1,host2=r1,host3=r2</code>.    *<p/>    * Value: {@value}    *<p/>    *<b>Important:</b>spaces not trimmed and are considered significant.    */
DECL|field|KEY_HADOOP_CONFIGURED_NODE_MAPPING
specifier|public
specifier|static
specifier|final
name|String
name|KEY_HADOOP_CONFIGURED_NODE_MAPPING
init|=
literal|"hadoop.configured.node.mapping"
decl_stmt|;
comment|/**    * Configure the mapping by extracting any mappings defined in the    * {@link #KEY_HADOOP_CONFIGURED_NODE_MAPPING} field    * @param conf new configuration    */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|mappings
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|KEY_HADOOP_CONFIGURED_NODE_MAPPING
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
name|String
name|str
range|:
name|mappings
control|)
block|{
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
block|}
comment|/**    * retained lower case setter for compatibility reasons; relays to    * {@link #setConf(Configuration)}    * @param conf new configuration    */
DECL|method|setconf (Configuration conf)
specifier|public
name|void
name|setconf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/* Only one instance per JVM */
DECL|field|nameToRackMap
specifier|private
specifier|static
specifier|final
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
comment|/**    * Add a node to the static map. The moment any entry is added to the map,    * the map goes multi-rack.    * @param name node name    * @param rackId rack ID    */
DECL|method|addNodeToRack (String name, String rackId)
specifier|public
specifier|static
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
synchronized|synchronized
init|(
name|nameToRackMap
init|)
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
block|}
annotation|@
name|Override
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
comment|/**    * The switch policy of this mapping is driven by the same policy    * as the Scripted mapping: the presence of the script name in    * the configuration file    * @return false, always    */
annotation|@
name|Override
DECL|method|isSingleSwitch ()
specifier|public
name|boolean
name|isSingleSwitch
parameter_list|()
block|{
return|return
name|isSingleSwitchByScriptPolicy
argument_list|()
return|;
block|}
comment|/**    * Get a copy of the map (for diagnostics)    * @return a clone of the map or null for none known    */
annotation|@
name|Override
DECL|method|getSwitchMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSwitchMap
parameter_list|()
block|{
synchronized|synchronized
init|(
name|nameToRackMap
init|)
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|nameToRackMap
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"static mapping with single switch = "
operator|+
name|isSingleSwitch
argument_list|()
return|;
block|}
comment|/**    * Clear the map    */
DECL|method|resetMap ()
specifier|public
specifier|static
name|void
name|resetMap
parameter_list|()
block|{
synchronized|synchronized
init|(
name|nameToRackMap
init|)
block|{
name|nameToRackMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

