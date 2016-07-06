begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp.dao
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
name|nodemanager
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
name|io
operator|.
name|File
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
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|server
operator|.
name|nodemanager
operator|.
name|webapp
operator|.
name|ContainerLogsUtils
import|;
end_import

begin_comment
comment|/**  * {@code ContainerLogsInfo} includes the log meta-data of containers.  *<p>  * The container log meta-data includes details such as:  *<ul>  *<li>The filename of the container log.</li>  *<li>The size of the container log.</li>  *</ul>  */
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
name|ContainerLogInfo
argument_list|>
name|containerLogsInfo
decl_stmt|;
comment|//JAXB needs this
DECL|method|ContainerLogsInfo ()
specifier|public
name|ContainerLogsInfo
parameter_list|()
block|{}
DECL|method|ContainerLogsInfo (final Context nmContext, final ContainerId containerId, String remoteUser)
specifier|public
name|ContainerLogsInfo
parameter_list|(
specifier|final
name|Context
name|nmContext
parameter_list|,
specifier|final
name|ContainerId
name|containerId
parameter_list|,
name|String
name|remoteUser
parameter_list|)
throws|throws
name|YarnException
block|{
name|this
operator|.
name|containerLogsInfo
operator|=
name|getContainerLogsInfo
argument_list|(
name|containerId
argument_list|,
name|remoteUser
argument_list|,
name|nmContext
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainerLogsInfo ()
specifier|public
name|List
argument_list|<
name|ContainerLogInfo
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
DECL|method|getContainerLogsInfo (ContainerId id, String remoteUser, Context nmContext)
specifier|private
specifier|static
name|List
argument_list|<
name|ContainerLogInfo
argument_list|>
name|getContainerLogsInfo
parameter_list|(
name|ContainerId
name|id
parameter_list|,
name|String
name|remoteUser
parameter_list|,
name|Context
name|nmContext
parameter_list|)
throws|throws
name|YarnException
block|{
name|List
argument_list|<
name|ContainerLogInfo
argument_list|>
name|logFiles
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerLogInfo
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|logDirs
init|=
name|ContainerLogsUtils
operator|.
name|getContainerLogDirs
argument_list|(
name|id
argument_list|,
name|remoteUser
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|containerLogsDir
range|:
name|logDirs
control|)
block|{
name|File
index|[]
name|logs
init|=
name|containerLogsDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|logs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|log
range|:
name|logs
control|)
block|{
if|if
condition|(
name|log
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|ContainerLogInfo
name|logMeta
init|=
operator|new
name|ContainerLogInfo
argument_list|(
name|log
operator|.
name|getName
argument_list|()
argument_list|,
name|log
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|logFiles
operator|.
name|add
argument_list|(
name|logMeta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|logFiles
return|;
block|}
DECL|class|ContainerLogInfo
specifier|private
specifier|static
class|class
name|ContainerLogInfo
block|{
DECL|field|fileName
specifier|private
name|String
name|fileName
decl_stmt|;
DECL|field|fileSize
specifier|private
name|long
name|fileSize
decl_stmt|;
comment|//JAXB needs this
DECL|method|ContainerLogInfo ()
specifier|public
name|ContainerLogInfo
parameter_list|()
block|{}
DECL|method|ContainerLogInfo (String fileName, long fileSize)
specifier|public
name|ContainerLogInfo
parameter_list|(
name|String
name|fileName
parameter_list|,
name|long
name|fileSize
parameter_list|)
block|{
name|this
operator|.
name|setFileName
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|this
operator|.
name|setFileSize
argument_list|(
name|fileSize
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileName ()
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
DECL|method|setFileName (String fileName)
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
block|}
DECL|method|getFileSize ()
specifier|public
name|long
name|getFileSize
parameter_list|()
block|{
return|return
name|fileSize
return|;
block|}
DECL|method|setFileSize (long fileSize)
specifier|public
name|void
name|setFileSize
parameter_list|(
name|long
name|fileSize
parameter_list|)
block|{
name|this
operator|.
name|fileSize
operator|=
name|fileSize
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

