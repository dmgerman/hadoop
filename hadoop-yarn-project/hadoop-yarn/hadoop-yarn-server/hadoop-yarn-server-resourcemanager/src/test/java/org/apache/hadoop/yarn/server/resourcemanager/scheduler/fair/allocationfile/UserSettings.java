begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.allocationfile
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|allocationfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_comment
comment|/**  * Value class that stores user settings and can render data in XML format,  * see {@link #render()}.  */
end_comment

begin_class
DECL|class|UserSettings
specifier|public
class|class
name|UserSettings
block|{
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|maxRunningApps
specifier|private
specifier|final
name|Integer
name|maxRunningApps
decl_stmt|;
DECL|method|UserSettings (Builder builder)
name|UserSettings
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|builder
operator|.
name|username
expr_stmt|;
name|this
operator|.
name|maxRunningApps
operator|=
name|builder
operator|.
name|maxRunningApps
expr_stmt|;
block|}
DECL|method|render ()
specifier|public
name|String
name|render
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|addStartTag
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|AllocationFileWriter
operator|.
name|addIfPresent
argument_list|(
name|pw
argument_list|,
literal|"maxRunningApps"
argument_list|,
name|maxRunningApps
argument_list|)
expr_stmt|;
name|addEndTag
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|addStartTag (PrintWriter pw)
specifier|private
name|void
name|addStartTag
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"<user name=\""
operator|+
name|username
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
block|}
DECL|method|addEndTag (PrintWriter pw)
specifier|private
name|void
name|addEndTag
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|println
argument_list|(
literal|"</user>"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builder class for {@link UserSettings}    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|maxRunningApps
specifier|private
name|Integer
name|maxRunningApps
decl_stmt|;
DECL|method|Builder (String username)
specifier|public
name|Builder
parameter_list|(
name|String
name|username
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
block|}
DECL|method|maxRunningApps (int value)
specifier|public
name|Builder
name|maxRunningApps
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|maxRunningApps
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|UserSettings
name|build
parameter_list|()
block|{
return|return
operator|new
name|UserSettings
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

