begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONJAXBContext
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
name|Singleton
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|ContextResolver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
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
name|JAXBContext
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
name|dao
operator|.
name|AppInfo
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
name|dao
operator|.
name|AppsInfo
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
name|dao
operator|.
name|AuxiliaryServiceInfo
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
name|dao
operator|.
name|AuxiliaryServicesInfo
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
name|dao
operator|.
name|ContainerInfo
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
name|dao
operator|.
name|ContainersInfo
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
name|dao
operator|.
name|NodeInfo
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
name|RemoteExceptionData
import|;
end_import

begin_class
annotation|@
name|Singleton
annotation|@
name|Provider
DECL|class|JAXBContextResolver
specifier|public
class|class
name|JAXBContextResolver
implements|implements
name|ContextResolver
argument_list|<
name|JAXBContext
argument_list|>
block|{
DECL|field|context
specifier|private
name|JAXBContext
name|context
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|types
decl_stmt|;
comment|// you have to specify all the dao classes here
DECL|field|cTypes
specifier|private
specifier|final
name|Class
index|[]
name|cTypes
init|=
block|{
name|AppInfo
operator|.
name|class
block|,
name|AppsInfo
operator|.
name|class
block|,
name|AuxiliaryServicesInfo
operator|.
name|class
block|,
name|AuxiliaryServiceInfo
operator|.
name|class
block|,
name|ContainerInfo
operator|.
name|class
block|,
name|ContainersInfo
operator|.
name|class
block|,
name|NodeInfo
operator|.
name|class
block|,
name|RemoteExceptionData
operator|.
name|class
block|}
decl_stmt|;
DECL|method|JAXBContextResolver ()
specifier|public
name|JAXBContextResolver
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|types
operator|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|cTypes
argument_list|)
argument_list|)
expr_stmt|;
comment|// sets the json configuration so that the json output looks like
comment|// the xml output
name|this
operator|.
name|context
operator|=
operator|new
name|JSONJAXBContext
argument_list|(
name|JSONConfiguration
operator|.
name|natural
argument_list|()
operator|.
name|rootUnwrapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|cTypes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContext (Class<?> objectType)
specifier|public
name|JAXBContext
name|getContext
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|objectType
parameter_list|)
block|{
return|return
operator|(
name|types
operator|.
name|contains
argument_list|(
name|objectType
argument_list|)
operator|)
condition|?
name|context
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

