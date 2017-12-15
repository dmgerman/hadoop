begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
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
operator|.
name|Private
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
name|records
operator|.
name|ContainerId
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
name|records
operator|.
name|NodeId
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
name|view
operator|.
name|HtmlBlock
operator|.
name|Block
import|;
end_import

begin_comment
comment|/**  * Utils for rendering aggregated logs block.  *  */
end_comment

begin_class
annotation|@
name|Private
DECL|class|LogAggregationWebUtils
specifier|public
specifier|final
class|class
name|LogAggregationWebUtils
block|{
DECL|method|LogAggregationWebUtils ()
specifier|private
name|LogAggregationWebUtils
parameter_list|()
block|{}
comment|/**    * Parse start index from html.    * @param html the html    * @param startStr the start index string    * @return the startIndex    */
DECL|method|getLogStartIndex (Block html, String startStr)
specifier|public
specifier|static
name|long
name|getLogStartIndex
parameter_list|(
name|Block
name|html
parameter_list|,
name|String
name|startStr
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|long
name|start
init|=
operator|-
literal|4096
decl_stmt|;
if|if
condition|(
name|startStr
operator|!=
literal|null
operator|&&
operator|!
name|startStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|start
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|startStr
argument_list|)
expr_stmt|;
block|}
return|return
name|start
return|;
block|}
comment|/**    * Parse end index from html.    * @param html the html    * @param endStr the end index string    * @return the endIndex    */
DECL|method|getLogEndIndex (Block html, String endStr)
specifier|public
specifier|static
name|long
name|getLogEndIndex
parameter_list|(
name|Block
name|html
parameter_list|,
name|String
name|endStr
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|long
name|end
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|endStr
operator|!=
literal|null
operator|&&
operator|!
name|endStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|end
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|endStr
argument_list|)
expr_stmt|;
block|}
return|return
name|end
return|;
block|}
comment|/**    * Verify and parse containerId.    * @param html the html    * @param containerIdStr the containerId string    * @return the {@link ContainerId}    */
DECL|method|verifyAndGetContainerId (Block html, String containerIdStr)
specifier|public
specifier|static
name|ContainerId
name|verifyAndGetContainerId
parameter_list|(
name|Block
name|html
parameter_list|,
name|String
name|containerIdStr
parameter_list|)
block|{
if|if
condition|(
name|containerIdStr
operator|==
literal|null
operator|||
name|containerIdStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Cannot get container logs without a ContainerId"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ContainerId
operator|.
name|fromString
argument_list|(
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Cannot get container logs for invalid containerId: "
operator|+
name|containerIdStr
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|containerId
return|;
block|}
comment|/**    * Verify and parse NodeId.    * @param html the html    * @param nodeIdStr the nodeId string    * @return the {@link NodeId}    */
DECL|method|verifyAndGetNodeId (Block html, String nodeIdStr)
specifier|public
specifier|static
name|NodeId
name|verifyAndGetNodeId
parameter_list|(
name|Block
name|html
parameter_list|,
name|String
name|nodeIdStr
parameter_list|)
block|{
if|if
condition|(
name|nodeIdStr
operator|==
literal|null
operator|||
name|nodeIdStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Cannot get container logs without a NodeId"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|NodeId
name|nodeId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nodeId
operator|=
name|NodeId
operator|.
name|fromString
argument_list|(
name|nodeIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Cannot get container logs. Invalid nodeId: "
operator|+
name|nodeIdStr
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|nodeId
return|;
block|}
comment|/**    * Verify and parse the application owner.    * @param html the html    * @param appOwner the Application owner    * @return the appOwner    */
DECL|method|verifyAndGetAppOwner (Block html, String appOwner)
specifier|public
specifier|static
name|String
name|verifyAndGetAppOwner
parameter_list|(
name|Block
name|html
parameter_list|,
name|String
name|appOwner
parameter_list|)
block|{
if|if
condition|(
name|appOwner
operator|==
literal|null
operator|||
name|appOwner
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|h1
argument_list|()
operator|.
name|__
argument_list|(
literal|"Cannot get container logs without an app owner"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
return|return
name|appOwner
return|;
block|}
comment|/**    * Parse log start time from html.    * @param startStr the start time string    * @return the startIndex    */
DECL|method|getLogStartTime (String startStr)
specifier|public
specifier|static
name|long
name|getLogStartTime
parameter_list|(
name|String
name|startStr
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|long
name|start
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|startStr
operator|!=
literal|null
operator|&&
operator|!
name|startStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|start
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|startStr
argument_list|)
expr_stmt|;
block|}
return|return
name|start
return|;
block|}
comment|/**    * Parse log end time from html.    * @param endStr the end time string    * @return the endIndex    */
DECL|method|getLogEndTime (String endStr)
specifier|public
specifier|static
name|long
name|getLogEndTime
parameter_list|(
name|String
name|endStr
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|long
name|end
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|endStr
operator|!=
literal|null
operator|&&
operator|!
name|endStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|end
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|endStr
argument_list|)
expr_stmt|;
block|}
return|return
name|end
return|;
block|}
block|}
end_class

end_unit

