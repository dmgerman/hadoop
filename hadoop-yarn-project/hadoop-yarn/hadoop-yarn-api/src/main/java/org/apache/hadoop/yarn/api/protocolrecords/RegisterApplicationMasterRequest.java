begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
package|;
end_package

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
operator|.
name|Public
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
operator|.
name|Stable
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
operator|.
name|Unstable
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocol
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
name|yarn
operator|.
name|api
operator|.
name|resource
operator|.
name|PlacementConstraint
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * The request sent by the {@code ApplicationMaster} to {@code ResourceManager}  * on registration.  *<p>  * The registration includes details such as:  *<ul>  *<li>Hostname on which the AM is running.</li>  *<li>RPC Port</li>  *<li>Tracking URL</li>  *</ul>  *   * @see ApplicationMasterProtocol#registerApplicationMaster(RegisterApplicationMasterRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|RegisterApplicationMasterRequest
specifier|public
specifier|abstract
class|class
name|RegisterApplicationMasterRequest
block|{
comment|/**    * Create a new instance of<code>RegisterApplicationMasterRequest</code>.    * If<em>port, trackingUrl</em> is not used, use the following default value:    *<ul>    *<li>port: -1</li>    *<li>trackingUrl: null</li>    *</ul>    * The port is allowed to be any integer larger than or equal to -1.    * @return the new instance of<code>RegisterApplicationMasterRequest</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (String host, int port, String trackingUrl)
specifier|public
specifier|static
name|RegisterApplicationMasterRequest
name|newInstance
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|trackingUrl
parameter_list|)
block|{
name|RegisterApplicationMasterRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRpcPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|trackingUrl
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the<em>host</em> on which the<code>ApplicationMaster</code> is     * running.    * @return<em>host</em> on which the<code>ApplicationMaster</code> is running    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHost ()
specifier|public
specifier|abstract
name|String
name|getHost
parameter_list|()
function_decl|;
comment|/**    * Set the<em>host</em> on which the<code>ApplicationMaster</code> is     * running.    * @param host<em>host</em> on which the<code>ApplicationMaster</code>     *             is running    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setHost (String host)
specifier|public
specifier|abstract
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
comment|/**    * Get the<em>RPC port</em> on which the {@code ApplicationMaster} is    * responding.    * @return the<em>RPC port</em> on which the {@code ApplicationMaster}    *         is responding    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRpcPort ()
specifier|public
specifier|abstract
name|int
name|getRpcPort
parameter_list|()
function_decl|;
comment|/**    * Set the<em>RPC port</em> on which the {@code ApplicationMaster} is    * responding.    * @param port<em>RPC port</em> on which the {@code ApplicationMaster}    *             is responding    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setRpcPort (int port)
specifier|public
specifier|abstract
name|void
name|setRpcPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
comment|/**    * Get the<em>tracking URL</em> for the<code>ApplicationMaster</code>.    * This url if contains scheme then that will be used by resource manager    * web application proxy otherwise it will default to http.    * @return<em>tracking URL</em> for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getTrackingUrl ()
specifier|public
specifier|abstract
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * Set the<em>tracking URL</em>for the<code>ApplicationMaster</code> while    * it is running. This is the web-URL to which ResourceManager or    * web-application proxy will redirect client/users while the application and    * the<code>ApplicationMaster</code> are still running.    *<p>    * If the passed url has a scheme then that will be used by the    * ResourceManager and web-application proxy, otherwise the scheme will    * default to http.    *</p>    *<p>    * Empty, null, "N/A" strings are all valid besides a real URL. In case an url    * isn't explicitly passed, it defaults to "N/A" on the ResourceManager.    *<p>    *    * @param trackingUrl    *<em>tracking URL</em>for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setTrackingUrl (String trackingUrl)
specifier|public
specifier|abstract
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|trackingUrl
parameter_list|)
function_decl|;
comment|/**    * Return all Placement Constraints specified at the Application level. The    * mapping is from a set of allocation tags to a    *<code>PlacementConstraint</code> associated with the tags, i.e., each    * {@link org.apache.hadoop.yarn.api.records.SchedulingRequest} that has those    * tags will be placed taking into account the corresponding constraint.    *    * @return A map of Placement Constraints.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPlacementConstraints ()
specifier|public
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|PlacementConstraint
argument_list|>
name|getPlacementConstraints
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * Set Placement Constraints applicable to the    * {@link org.apache.hadoop.yarn.api.records.SchedulingRequest}s    * of this application.    * The mapping is from a set of allocation tags to a    *<code>PlacementConstraint</code> associated with the tags.    * For example:    *  Map&lt;    *&lt;hb_regionserver&gt; -&gt; node_anti_affinity,    *&lt;hb_regionserver, hb_master&gt; -&gt; rack_affinity,    *   ...    *&gt;    * @param placementConstraints Placement Constraint Mapping.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setPlacementConstraints ( Map<Set<String>, PlacementConstraint> placementConstraints)
specifier|public
name|void
name|setPlacementConstraints
parameter_list|(
name|Map
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|,
name|PlacementConstraint
argument_list|>
name|placementConstraints
parameter_list|)
block|{   }
block|}
end_class

end_unit

