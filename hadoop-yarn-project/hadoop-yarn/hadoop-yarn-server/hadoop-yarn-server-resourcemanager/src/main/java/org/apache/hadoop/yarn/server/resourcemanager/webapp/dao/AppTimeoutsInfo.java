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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * This class hosts a set of AppTimeout DAO objects.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"timeouts"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|AppTimeoutsInfo
specifier|public
class|class
name|AppTimeoutsInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"timeout"
argument_list|)
DECL|field|timeouts
specifier|private
name|ArrayList
argument_list|<
name|AppTimeoutInfo
argument_list|>
name|timeouts
init|=
operator|new
name|ArrayList
argument_list|<
name|AppTimeoutInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AppTimeoutsInfo ()
specifier|public
name|AppTimeoutsInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|add (AppTimeoutInfo timeoutInfo)
specifier|public
name|void
name|add
parameter_list|(
name|AppTimeoutInfo
name|timeoutInfo
parameter_list|)
block|{
name|timeouts
operator|.
name|add
argument_list|(
name|timeoutInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|getAppTimeouts ()
specifier|public
name|ArrayList
argument_list|<
name|AppTimeoutInfo
argument_list|>
name|getAppTimeouts
parameter_list|()
block|{
return|return
name|timeouts
return|;
block|}
block|}
end_class

end_unit

