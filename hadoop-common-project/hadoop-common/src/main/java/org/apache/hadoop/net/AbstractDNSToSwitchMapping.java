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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|Configurable
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
name|CommonConfigurationKeys
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
comment|/**  * This is a base class for DNS to Switch mappings.<p> It is not mandatory to  * derive {@link DNSToSwitchMapping} implementations from it, but it is strongly  * recommended, as it makes it easy for the Hadoop developers to add new methods  * to this base class that are automatically picked up by all implementations.  *<p>  *  * This class does not extend the<code>Configured</code>  * base class, and should not be changed to do so, as it causes problems  * for subclasses. The constructor of the<code>Configured</code> calls  * the  {@link #setConf(Configuration)} method, which will call into the  * subclasses before they have been fully constructed.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AbstractDNSToSwitchMapping
specifier|public
specifier|abstract
class|class
name|AbstractDNSToSwitchMapping
implements|implements
name|DNSToSwitchMapping
implements|,
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Create an unconfigured instance    */
DECL|method|AbstractDNSToSwitchMapping ()
specifier|protected
name|AbstractDNSToSwitchMapping
parameter_list|()
block|{   }
comment|/**    * Create an instance, caching the configuration file.    * This constructor does not call {@link #setConf(Configuration)}; if    * a subclass extracts information in that method, it must call it explicitly.    * @param conf the configuration    */
DECL|method|AbstractDNSToSwitchMapping (Configuration conf)
specifier|protected
name|AbstractDNSToSwitchMapping
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
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
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Predicate that indicates that the switch mapping is known to be    * single-switch. The base class returns false: it assumes all mappings are    * multi-rack. Subclasses may override this with methods that are more aware    * of their topologies.    *    *<p>    *    * This method is used when parts of Hadoop need know whether to apply    * single rack vs multi-rack policies, such as during block placement.    * Such algorithms behave differently if they are on multi-switch systems.    *</p>    *    * @return true if the mapping thinks that it is on a single switch    */
DECL|method|isSingleSwitch ()
specifier|public
name|boolean
name|isSingleSwitch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Get a copy of the map (for diagnostics)    * @return a clone of the map or null for none known    */
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
return|return
literal|null
return|;
block|}
comment|/**    * Generate a string listing the switch mapping implementation,    * the mapping for every known node and the number of nodes and    * unique switches known about -each entry to a separate line.    * @return a string that can be presented to the ops team or used in    * debug messages.    */
DECL|method|dumpTopology ()
specifier|public
name|String
name|dumpTopology
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|rack
init|=
name|getSwitchMap
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Mapping: "
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|rack
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"Map:\n"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|switches
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|rack
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|switches
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"Nodes: "
argument_list|)
operator|.
name|append
argument_list|(
name|rack
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"Switches: "
argument_list|)
operator|.
name|append
argument_list|(
name|switches
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"No topology information"
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|isSingleSwitchByScriptPolicy ()
specifier|protected
name|boolean
name|isSingleSwitchByScriptPolicy
parameter_list|()
block|{
return|return
name|conf
operator|!=
literal|null
operator|&&
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|)
operator|==
literal|null
return|;
block|}
comment|/**    * Query for a {@link DNSToSwitchMapping} instance being on a single    * switch.    *<p>    * This predicate simply assumes that all mappings not derived from    * this class are multi-switch.    * @param mapping the mapping to query    * @return true if the base class says it is single switch, or the mapping    * is not derived from this class.    */
DECL|method|isMappingSingleSwitch (DNSToSwitchMapping mapping)
specifier|public
specifier|static
name|boolean
name|isMappingSingleSwitch
parameter_list|(
name|DNSToSwitchMapping
name|mapping
parameter_list|)
block|{
return|return
name|mapping
operator|!=
literal|null
operator|&&
name|mapping
operator|instanceof
name|AbstractDNSToSwitchMapping
operator|&&
operator|(
operator|(
name|AbstractDNSToSwitchMapping
operator|)
name|mapping
operator|)
operator|.
name|isSingleSwitch
argument_list|()
return|;
block|}
block|}
end_class

end_unit

