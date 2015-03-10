begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

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
name|XmlRootElement
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"scheduler"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|SchedulerTypeInfo
specifier|public
class|class
name|SchedulerTypeInfo
block|{
DECL|field|schedulerInfo
specifier|protected
name|SchedulerInfo
name|schedulerInfo
decl_stmt|;
DECL|method|SchedulerTypeInfo ()
specifier|public
name|SchedulerTypeInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|SchedulerTypeInfo (final SchedulerInfo scheduler)
specifier|public
name|SchedulerTypeInfo
parameter_list|(
specifier|final
name|SchedulerInfo
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|schedulerInfo
operator|=
name|scheduler
expr_stmt|;
block|}
DECL|method|getSchedulerInfo ()
specifier|public
name|SchedulerInfo
name|getSchedulerInfo
parameter_list|()
block|{
return|return
name|schedulerInfo
return|;
block|}
block|}
end_class

end_unit

