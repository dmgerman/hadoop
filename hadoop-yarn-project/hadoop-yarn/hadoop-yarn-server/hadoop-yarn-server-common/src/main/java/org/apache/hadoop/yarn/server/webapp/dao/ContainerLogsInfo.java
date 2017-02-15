begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp.dao
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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
name|exceptions
operator|.
name|YarnException
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
name|logaggregation
operator|.
name|ContainerLogMeta
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
name|logaggregation
operator|.
name|ContainerLogAggregationType
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
name|logaggregation
operator|.
name|PerContainerLogFileInfo
import|;
end_import

begin_comment
comment|/**  * {@code ContainerLogsInfo} includes the log meta-data of containers.  *<p>  * The container log meta-data includes details such as:  *<ul>  *<li>A list of {@link PerContainerLogFileInfo}.</li>  *<li>The container Id.</li>  *<li>The NodeManager Id.</li>  *<li>The logType: could be local or aggregated</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"containerLogsInfo"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ContainerLogsInfo
specifier|public
class|class
name|ContainerLogsInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"containerLogInfo"
argument_list|)
DECL|field|containerLogsInfo
specifier|protected
name|List
argument_list|<
name|PerContainerLogFileInfo
argument_list|>
name|containerLogsInfo
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"logAggregationType"
argument_list|)
DECL|field|logType
specifier|protected
name|String
name|logType
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"containerId"
argument_list|)
DECL|field|containerId
specifier|protected
name|String
name|containerId
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"nodeId"
argument_list|)
DECL|field|nodeId
specifier|protected
name|String
name|nodeId
decl_stmt|;
comment|//JAXB needs this
DECL|method|ContainerLogsInfo ()
specifier|public
name|ContainerLogsInfo
parameter_list|()
block|{}
DECL|method|ContainerLogsInfo (ContainerLogMeta logMeta, ContainerLogAggregationType logType)
specifier|public
name|ContainerLogsInfo
parameter_list|(
name|ContainerLogMeta
name|logMeta
parameter_list|,
name|ContainerLogAggregationType
name|logType
parameter_list|)
throws|throws
name|YarnException
block|{
name|this
operator|.
name|containerLogsInfo
operator|=
operator|new
name|ArrayList
argument_list|<
name|PerContainerLogFileInfo
argument_list|>
argument_list|(
name|logMeta
operator|.
name|getContainerLogMeta
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|logType
operator|=
name|logType
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|logMeta
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|logMeta
operator|.
name|getNodeId
argument_list|()
expr_stmt|;
block|}
DECL|method|getContainerLogsInfo ()
specifier|public
name|List
argument_list|<
name|PerContainerLogFileInfo
argument_list|>
name|getContainerLogsInfo
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerLogsInfo
return|;
block|}
DECL|method|getLogType ()
specifier|public
name|String
name|getLogType
parameter_list|()
block|{
return|return
name|this
operator|.
name|logType
return|;
block|}
DECL|method|getContainerId ()
specifier|public
name|String
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
block|}
end_class

end_unit

