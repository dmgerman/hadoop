begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.view
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|view
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
operator|.
name|DIV
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
operator|.
name|LI
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
operator|.
name|UL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|ApplicationLivenessInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|registry
operator|.
name|docstore
operator|.
name|ExportEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|registry
operator|.
name|docstore
operator|.
name|PublishedExports
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|registry
operator|.
name|docstore
operator|.
name|PublishedExportsSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|metrics
operator|.
name|SliderMetrics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|WebAppApi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collections
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
operator|.
name|Entry
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
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|RestPaths
operator|.
name|LIVE_COMPONENTS
import|;
end_import

begin_comment
comment|/**  * The main content on the Slider AM web page  */
end_comment

begin_class
DECL|class|IndexBlock
specifier|public
class|class
name|IndexBlock
extends|extends
name|SliderHamletBlock
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexBlock
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Message printed when application is at full size.    *    * {@value}    */
DECL|field|ALL_CONTAINERS_ALLOCATED
specifier|public
specifier|static
specifier|final
name|String
name|ALL_CONTAINERS_ALLOCATED
init|=
literal|"all containers allocated"
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexBlock (WebAppApi slider)
specifier|public
name|IndexBlock
parameter_list|(
name|WebAppApi
name|slider
parameter_list|)
block|{
name|super
argument_list|(
name|slider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|doIndex
argument_list|(
name|html
argument_list|,
name|getProviderName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// An extra method to make testing easier since you can't make an instance of Block
annotation|@
name|VisibleForTesting
DECL|method|doIndex (Hamlet html, String providerName)
specifier|protected
name|void
name|doIndex
parameter_list|(
name|Hamlet
name|html
parameter_list|,
name|String
name|providerName
parameter_list|)
block|{
name|String
name|name
init|=
name|appState
operator|.
name|getApplicationName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|(
name|name
operator|.
name|startsWith
argument_list|(
literal|" "
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|" "
argument_list|)
operator|)
condition|)
block|{
name|name
operator|=
literal|"'"
operator|+
name|name
operator|+
literal|"'"
expr_stmt|;
block|}
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|div
argument_list|(
literal|"general_info"
argument_list|)
operator|.
name|h1
argument_list|(
literal|"index_header"
argument_list|,
literal|"Application: "
operator|+
name|name
argument_list|)
decl_stmt|;
name|ApplicationLivenessInformation
name|liveness
init|=
name|appState
operator|.
name|getApplicationLivenessInformation
argument_list|()
decl_stmt|;
name|String
name|livestatus
init|=
name|liveness
operator|.
name|allRequestsSatisfied
condition|?
name|ALL_CONTAINERS_ALLOCATED
else|:
name|String
operator|.
name|format
argument_list|(
literal|"Awaiting %d containers"
argument_list|,
name|liveness
operator|.
name|requestsOutstanding
argument_list|)
decl_stmt|;
name|Hamlet
operator|.
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table1
init|=
name|div
operator|.
name|table
argument_list|()
decl_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Status"
argument_list|)
operator|.
name|td
argument_list|(
name|livestatus
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Total number of containers"
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|appState
operator|.
name|getNumOwnedContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Create time: "
argument_list|)
operator|.
name|td
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Running since: "
argument_list|)
operator|.
name|td
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Time last flexed: "
argument_list|)
operator|.
name|td
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Application storage path: "
argument_list|)
operator|.
name|td
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
literal|"Application configuration path: "
argument_list|)
operator|.
name|td
argument_list|(
literal|"N/A"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|table1
operator|.
name|_
argument_list|()
expr_stmt|;
name|div
operator|.
name|_
argument_list|()
expr_stmt|;
name|div
operator|=
literal|null
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|containers
init|=
name|html
operator|.
name|div
argument_list|(
literal|"container_instances"
argument_list|)
operator|.
name|h3
argument_list|(
literal|"Component Instances"
argument_list|)
decl_stmt|;
name|int
name|aaRoleWithNoSuitableLocations
init|=
literal|0
decl_stmt|;
name|int
name|aaRoleWithOpenRequest
init|=
literal|0
decl_stmt|;
name|int
name|roleWithOpenRequest
init|=
literal|0
decl_stmt|;
name|Hamlet
operator|.
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table
init|=
name|containers
operator|.
name|table
argument_list|()
decl_stmt|;
name|Hamlet
operator|.
name|TR
argument_list|<
name|Hamlet
operator|.
name|THEAD
argument_list|<
name|Hamlet
operator|.
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|header
init|=
name|table
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
decl_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Component"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Desired"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Actual"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Outstanding Requests"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Failed"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Failed to start"
argument_list|)
expr_stmt|;
name|trb
argument_list|(
name|header
argument_list|,
literal|"Placement"
argument_list|)
expr_stmt|;
name|header
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
comment|// tr& thead
name|List
argument_list|<
name|RoleStatus
argument_list|>
name|roleStatuses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|appState
operator|.
name|getRoleStatusMap
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|roleStatuses
argument_list|,
operator|new
name|RoleStatus
operator|.
name|CompareByName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RoleStatus
name|status
range|:
name|roleStatuses
control|)
block|{
name|String
name|roleName
init|=
name|status
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|nameUrl
init|=
name|apiPath
argument_list|(
name|LIVE_COMPONENTS
argument_list|)
operator|+
literal|"/"
operator|+
name|roleName
decl_stmt|;
name|String
name|aatext
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|isAntiAffinePlacement
argument_list|()
condition|)
block|{
name|boolean
name|aaRequestOutstanding
init|=
name|status
operator|.
name|isAARequestOutstanding
argument_list|()
decl_stmt|;
name|int
name|pending
init|=
operator|(
name|int
operator|)
name|status
operator|.
name|getAAPending
argument_list|()
decl_stmt|;
name|aatext
operator|=
name|buildAADetails
argument_list|(
name|aaRequestOutstanding
argument_list|,
name|pending
argument_list|)
expr_stmt|;
if|if
condition|(
name|SliderUtils
operator|.
name|isSet
argument_list|(
name|status
operator|.
name|getLabelExpression
argument_list|()
argument_list|)
condition|)
block|{
name|aatext
operator|+=
literal|" (label: "
operator|+
name|status
operator|.
name|getLabelExpression
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|pending
operator|>
literal|0
operator|&&
operator|!
name|aaRequestOutstanding
condition|)
block|{
name|aaRoleWithNoSuitableLocations
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aaRequestOutstanding
condition|)
block|{
name|aaRoleWithOpenRequest
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|SliderUtils
operator|.
name|isSet
argument_list|(
name|status
operator|.
name|getLabelExpression
argument_list|()
argument_list|)
condition|)
block|{
name|aatext
operator|=
literal|"label: "
operator|+
name|status
operator|.
name|getLabelExpression
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aatext
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getPending
argument_list|()
operator|>
literal|0
condition|)
block|{
name|roleWithOpenRequest
operator|++
expr_stmt|;
block|}
block|}
name|SliderMetrics
name|metrics
init|=
name|status
operator|.
name|getComponentMetrics
argument_list|()
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|nameUrl
argument_list|,
name|roleName
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
name|metrics
operator|.
name|containersDesired
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
name|metrics
operator|.
name|containersRunning
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
name|metrics
operator|.
name|containersPending
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
name|metrics
operator|.
name|containersFailed
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|aatext
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
comment|// empty row for some more spacing
name|table
operator|.
name|tr
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
comment|// close table
name|table
operator|.
name|_
argument_list|()
expr_stmt|;
name|containers
operator|.
name|_
argument_list|()
expr_stmt|;
name|containers
operator|=
literal|null
expr_stmt|;
comment|// some spacing
name|html
operator|.
name|div
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
name|html
operator|.
name|div
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|diagnostics
init|=
name|html
operator|.
name|div
argument_list|(
literal|"diagnostics"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|statusEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|roleWithOpenRequest
operator|>
literal|0
condition|)
block|{
name|statusEntries
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d %s with requests unsatisfiable by cluster"
argument_list|,
name|roleWithOpenRequest
argument_list|,
name|plural
argument_list|(
name|roleWithOpenRequest
argument_list|,
literal|"component"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aaRoleWithNoSuitableLocations
operator|>
literal|0
condition|)
block|{
name|statusEntries
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d anti-affinity %s no suitable nodes in the cluster"
argument_list|,
name|aaRoleWithNoSuitableLocations
argument_list|,
name|plural
argument_list|(
name|aaRoleWithNoSuitableLocations
argument_list|,
literal|"component has"
argument_list|,
literal|"components have"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|aaRoleWithOpenRequest
operator|>
literal|0
condition|)
block|{
name|statusEntries
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d anti-affinity %s with requests unsatisfiable by cluster"
argument_list|,
name|aaRoleWithOpenRequest
argument_list|,
name|plural
argument_list|(
name|aaRoleWithOpenRequest
argument_list|,
literal|"component has"
argument_list|,
literal|"components have"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|statusEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|diagnostics
operator|.
name|h3
argument_list|(
literal|"Diagnostics"
argument_list|)
expr_stmt|;
name|Hamlet
operator|.
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|diagnosticsTable
init|=
name|diagnostics
operator|.
name|table
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|statusEntries
control|)
block|{
name|diagnosticsTable
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|entry
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|diagnosticsTable
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|diagnostics
operator|.
name|_
argument_list|()
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|provider_info
init|=
name|html
operator|.
name|div
argument_list|(
literal|"provider_info"
argument_list|)
decl_stmt|;
name|provider_info
operator|.
name|h3
argument_list|(
name|providerName
operator|+
literal|" information"
argument_list|)
expr_stmt|;
name|UL
argument_list|<
name|Hamlet
argument_list|>
name|ul
init|=
name|html
operator|.
name|ul
argument_list|()
decl_stmt|;
comment|//TODO render app/cluster status
name|ul
operator|.
name|_
argument_list|()
expr_stmt|;
name|provider_info
operator|.
name|_
argument_list|()
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|exports
init|=
name|html
operator|.
name|div
argument_list|(
literal|"exports"
argument_list|)
decl_stmt|;
name|exports
operator|.
name|h3
argument_list|(
literal|"Exports"
argument_list|)
expr_stmt|;
name|ul
operator|=
name|html
operator|.
name|ul
argument_list|()
expr_stmt|;
name|enumeratePublishedExports
argument_list|(
name|appState
operator|.
name|getPublishedExportsSet
argument_list|()
argument_list|,
name|ul
argument_list|)
expr_stmt|;
name|ul
operator|.
name|_
argument_list|()
expr_stmt|;
name|exports
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|buildAADetails (boolean outstanding, int pending)
name|String
name|buildAADetails
parameter_list|(
name|boolean
name|outstanding
parameter_list|,
name|int
name|pending
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"Anti-affinity:%s %d pending %s"
argument_list|,
operator|(
name|outstanding
condition|?
literal|" 1 active request and"
else|:
literal|""
operator|)
argument_list|,
name|pending
argument_list|,
name|plural
argument_list|(
name|pending
argument_list|,
literal|"request"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|plural (int n, String singular)
specifier|private
name|String
name|plural
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|singular
parameter_list|)
block|{
return|return
name|plural
argument_list|(
name|n
argument_list|,
name|singular
argument_list|,
name|singular
operator|+
literal|"s"
argument_list|)
return|;
block|}
DECL|method|plural (int n, String singular, String plural)
specifier|private
name|String
name|plural
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|singular
parameter_list|,
name|String
name|plural
parameter_list|)
block|{
return|return
name|n
operator|==
literal|1
condition|?
name|singular
else|:
name|plural
return|;
block|}
DECL|method|trb (Hamlet.TR tr, String text)
specifier|private
name|void
name|trb
parameter_list|(
name|Hamlet
operator|.
name|TR
name|tr
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|tr
operator|.
name|td
argument_list|()
operator|.
name|b
argument_list|(
name|text
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
DECL|method|getProviderName ()
specifier|private
name|String
name|getProviderName
parameter_list|()
block|{
return|return
literal|"docker"
return|;
block|}
DECL|method|enumeratePublishedExports (PublishedExportsSet exports, UL<Hamlet> ul)
specifier|protected
name|void
name|enumeratePublishedExports
parameter_list|(
name|PublishedExportsSet
name|exports
parameter_list|,
name|UL
argument_list|<
name|Hamlet
argument_list|>
name|ul
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|exports
operator|.
name|keys
argument_list|()
control|)
block|{
name|PublishedExports
name|export
init|=
name|exports
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|LI
argument_list|<
name|UL
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|item
init|=
name|ul
operator|.
name|li
argument_list|()
decl_stmt|;
name|item
operator|.
name|span
argument_list|()
operator|.
name|$class
argument_list|(
literal|"bold"
argument_list|)
operator|.
name|_
argument_list|(
name|export
operator|.
name|description
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|UL
name|sublist
init|=
name|item
operator|.
name|ul
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|ExportEntry
argument_list|>
argument_list|>
name|entry
range|:
name|export
operator|.
name|sortedEntries
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|SliderUtils
operator|.
name|isNotEmpty
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|LI
name|sublistItem
init|=
name|sublist
operator|.
name|li
argument_list|()
operator|.
name|_
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ExportEntry
name|exportEntry
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|sublistItem
operator|.
name|_
argument_list|(
name|exportEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sublistItem
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
name|sublist
operator|.
name|_
argument_list|()
expr_stmt|;
name|item
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

