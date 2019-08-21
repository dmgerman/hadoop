begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|containermanager
operator|.
name|linux
operator|.
name|resources
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEFAULT_NM_RESOURCE_PLUGINS_FAIL_FAST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PLUGINS_FAIL_FAST
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * Small utility class which only re-throws YarnException if  * NM_RESOURCE_PLUGINS_FAIL_FAST property is true.  *  */
end_comment

begin_class
DECL|class|ResourcesExceptionUtil
specifier|public
specifier|final
class|class
name|ResourcesExceptionUtil
block|{
DECL|method|ResourcesExceptionUtil ()
specifier|private
name|ResourcesExceptionUtil
parameter_list|()
block|{}
DECL|method|throwIfNecessary (YarnException e, Configuration conf)
specifier|public
specifier|static
name|void
name|throwIfNecessary
parameter_list|(
name|YarnException
name|e
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|NM_RESOURCE_PLUGINS_FAIL_FAST
argument_list|,
name|DEFAULT_NM_RESOURCE_PLUGINS_FAIL_FAST
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

